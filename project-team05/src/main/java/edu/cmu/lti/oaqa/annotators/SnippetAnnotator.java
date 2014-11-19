package edu.cmu.lti.oaqa.annotators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import json.gson.Snippet;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import util.FullDocumentSources;
import util.SimilarityMeasures;
import util.Utils;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.SynSet;
import edu.cmu.lti.oaqa.type.retrieval.Synonym;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class SnippetAnnotator extends JCasAnnotator_ImplBase {

  private final int  topRank = 5;
  public static final String PUBMED_URL ="http://www.ncbi.nlm.nih.gov/pubmed/";
  public static final String DEFAULT_SECTION ="section.0";

  public class ScoreComparator implements Comparator<Snippet> {
    public int compare(Snippet o1, Snippet o2) {
      if (o2.score > o1.score)
        return 1;
      return -1;
    }
  }
  FileWriter snippetWriter = null;
  File snippet = new File("snippetResults.txt");
  
  public void initialize(UimaContext u) {
    try {
      snippetWriter = new FileWriter(snippet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void collectionProcessComplete() {
    try {
      snippetWriter.flush();
      snippetWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    int rank;

    FSIterator<Annotation> questions = jcas.getAnnotationIndex(ExpandedQuestion.type).iterator();

    while (questions.hasNext()) {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();
      ArrayList<SynSet> as = Utils.fromFSListToCollection(question.getSynSets(), SynSet.class);
      edu.cmu.lti.oaqa.type.retrieval.SynSet synset;

      String query = "";

      ArrayList<String> synonymList = new ArrayList<String>();
      for (SynSet syns : as) 
      {
        ArrayList<Synonym> synonyms = Utils.fromFSListToCollection(syns.getSynonyms(),Synonym.class);
        for (Synonym synonym : synonyms)
          synonymList.add(synonym.getSynonym());

        synonymList.add(syns.getOriginalToken());
      }
      
      List<edu.cmu.lti.oaqa.type.retrieval.Document> documentItems = getDocumentItems(jcas);
     
      edu.cmu.lti.oaqa.type.retrieval.Document doc;

      if (documentItems != null && !documentItems.isEmpty()) {
        rank = 0;
        ArrayList<Snippet> snippetList = new ArrayList<Snippet>();
        SnippetSearchResult snippetSearchResult = new SnippetSearchResult(jcas);
        for (edu.cmu.lti.oaqa.type.retrieval.Document document : documentItems) {
          doc = new edu.cmu.lti.oaqa.type.retrieval.Document(jcas);

          List<String> text = null;
       
          try {
            text = FullDocumentSources.getFullText(document);
            if (text == null||"".equals(text))
              continue;
          } catch (IOException e) {
            e.printStackTrace();
          }
          ArrayList<Snippet> snippets = new ArrayList<Snippet>();

          // concept matching?
        
          for (String docText:text) 
          {
            int offsetPtr = 0;
            Reader reader = new StringReader(docText);
            DocumentPreprocessor dp = new DocumentPreprocessor(reader);

            String nowSection = DEFAULT_SECTION;
            List<String> sentenceTokens;
            for (List<HasWord> sentence : dp) {
              sentenceTokens = new ArrayList<String>();
              StringBuffer wholeSentence = new StringBuffer();
              for (HasWord word : sentence) {
                sentenceTokens.add(word.word());
                wholeSentence.append(word.word()+ " ");
              }
              SimilarityMeasures sm = new SimilarityMeasures();
              double score = sm.getSimilarity(sentenceTokens, synonymList);
              
              Snippet s = new Snippet(score, PUBMED_URL+document.getDocId(), wholeSentence.toString(),
                      offsetPtr, offsetPtr + sentence.size(), nowSection, nowSection);

              snippetList.add(s);
              offsetPtr = offsetPtr + sentence.size();

            }
          }
        }
        int rankThreshold = 1;
        Collections.sort(snippetList, new ScoreComparator());

        for (Snippet snippet : snippetList) {
          try {
            snippetWriter.write("Q:"+question.getText()+ " Document:"+ snippet.getDocument()+" offsetBegin: "+ 
          snippet.getOffsetInBeginSection()+" offsetEnd: "+snippet.getOffsetInEndSection()+" A: "+snippet.getText() +"\n");
          } catch (IOException e) {
            e.printStackTrace();
          }
          
          System.out.println(snippet.score + " " + snippet.getText());
          
          snippetSearchResult.setSnippets(createPassage(snippet,jcas));
          rankThreshold++;
          
          snippetSearchResult.addToIndexes();

          if (rankThreshold > topRank)
            break;
        }
      }
    }
  }

  private Passage createPassage(Snippet snippet, JCas jcas) {
    Passage passage = new Passage(jcas);
    passage.setDocId(snippet.getDocument());
    passage.setBeginSection(snippet.getBeginSection());
    passage.setEndSection(snippet.getEndSection());
    passage.setOffsetInBeginSection(snippet.getOffsetInBeginSection());
    passage.setOffsetInEndSection(snippet.getOffsetInEndSection());
    passage.setText(snippet.getText());
    return passage;
  }

  private List<edu.cmu.lti.oaqa.type.retrieval.Document> getDocumentItems(JCas jcas) {
    FSIterator<TOP> documentsIter = jcas.getJFSIndexRepository().getAllIndexedFS(
            edu.cmu.lti.oaqa.type.retrieval.Document.type);
    List<edu.cmu.lti.oaqa.type.retrieval.Document> documentItems = new ArrayList<edu.cmu.lti.oaqa.type.retrieval.Document>();
    while (documentsIter.hasNext()) {
      edu.cmu.lti.oaqa.type.retrieval.Document document = (edu.cmu.lti.oaqa.type.retrieval.Document) documentsIter.next();
      documentItems.add(document);
    }
    return documentItems;
  }
}