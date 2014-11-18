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
import org.apache.uima.jcas.tcas.Annotation;

import util.FullDocumentSources;
import util.GoPubMedServiceSingleton;
import util.SimilarityMeasures;
import util.Utils;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse.Document;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.SynSet;
import edu.cmu.lti.oaqa.type.retrieval.Synonym;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class SnippetAnnotator extends JCasAnnotator_ImplBase {

  int topRank = 10;

  public class ScoreComparator implements Comparator<Snippet> {
    public int compare(Snippet o1, Snippet o2) {

      if (o2.score > o1.score)
        return 1;
      else
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
    // String questionText = "PnP";

    FSIterator<Annotation> questions = jcas.getAnnotationIndex(ExpandedQuestion.type).iterator();

    while (questions.hasNext()) {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();

      // ArrayList<SynSet> SynsetJcas = new ArrayList<SynSet>();

      // ArrayList<Synonym> SynonymJcas = new ArrayList<Synonym>();
      // vjcas.getAnnotationIndex(SynSet.type)
      System.out.println(question.getSynSets());
      ArrayList<SynSet> as = Utils.fromFSListToCollection(question.getSynSets(), SynSet.class);

      // FSIterator<TOP> synsets = jcas.getJFSIndexRepository().getAllIndexedFS(SynSet.type);
      edu.cmu.lti.oaqa.type.retrieval.SynSet synset;

      String query = new String("");
      for (int i = 0; i < as.size(); i++) {
        SynSet s = as.get(i);
        ArrayList<Synonym> asSynonym = Utils.fromFSListToCollection(s.getSynonyms(), Synonym.class);
        
        System.out.print(s.getOriginalToken()+" Syn:");
        for (Synonym tempS:asSynonym)        
          System.out.print(tempS.getSynonym()+" ");
        
        System.out.println();
        query += s.getOriginalToken() + " ";
      }
      /*
       * while (synsets.hasNext()) { try { Thread.sleep(200000); } catch (InterruptedException e1) {
       * // TODO Auto-generated catch block e1.printStackTrace(); } System.out.println("bad");
       * synset = (SynSet)synsets.next(); System.out.println(synset.getOriginalToken());
       * FSIterator<Synonym> synonyms = (FSIterator<Synonym>) synset.getSynonyms(); while
       * (synonyms.hasNext()) { System.out.println( synonyms.next()); } }
       */
      /*
       * Synonym synonymsList= new Synonym(); int numOfConceptMatching = 0; new while
       * (synonyms.hasNext()) { Synonym synonym = (Synonym) synonyms.next(); if
       * (sentence.contains(synonym.getSynonym())) numOfConceptMatching++; }
       */

      // need to use concept list for query?
      PubMedSearchServiceResponse.Result documents = GoPubMedServiceSingleton.getService()
              .getDocuments(query);

      edu.cmu.lti.oaqa.type.retrieval.Document doc;

      if (documents != null && documents.getDocuments() != null
              && !documents.getDocuments().isEmpty()) {
        rank = 0;
        ArrayList<Snippet> snippetList = new ArrayList<Snippet>();
        SnippetSearchResult snippetSearchResult = new SnippetSearchResult(jcas);
        for (Document document : documents.getDocuments()) {
         // System.out.println(document.getPmid());
          doc = new edu.cmu.lti.oaqa.type.retrieval.Document(jcas);

          List<String> text = null;
          String rawText;
       
          try {
            text = FullDocumentSources.getFullText(document);
            //System.out.println(document);
            //System.out.println(text);
            if (text == null)
              continue;
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          ArrayList<Snippet> snippets = new ArrayList<Snippet>();

          // concept matching?
        
          for (int i = 0; i < text.size(); i++) {
            int offsetPtr = 0;
            String docText = text.get(i);
            Reader reader = new StringReader(docText);
            DocumentPreprocessor dp = new DocumentPreprocessor(reader);

            String nowSection = "section.0";
            List<String> sentenceTokens;
            for (List<HasWord> sentence : dp) {
              //System.out.println(sentence.toString());
              sentenceTokens = new ArrayList<String>();
              StringBuffer wholeSentence = new StringBuffer();
              for (HasWord word : sentence) {
                sentenceTokens.add(word.word());
                wholeSentence.append(word.word()+ " ");
              }

              // FSIterator<Synonym> synonyms = (FSIterator<Synonym>) question.getSynSets();
              ArrayList<String> synonymList = new ArrayList<String>();
              for (SynSet syns : as) {

                ArrayList<Synonym> synonyms = Utils.fromFSListToCollection(syns.getSynonyms(),
                        Synonym.class);
                for (Synonym synonym : synonyms)
                  synonymList.add(synonym.getSynonym());

                synonymList.add(syns.getOriginalToken());
              }
              SimilarityMeasures sm = new SimilarityMeasures();
              double score = sm.getSimilarity(sentenceTokens, synonymList);
              
              Snippet s = new Snippet(score, "http://www.ncbi.nlm.nih.gov/pubmed/"+document.getPmid(), wholeSentence.toString(),
                      offsetPtr, offsetPtr + sentence.size(), nowSection, nowSection);

              snippetList.add(s);

            //  System.out.println(document.getPmid() + " " + sentence.toString());
              offsetPtr = offsetPtr + sentence.size();

            }
          }
        }
        int i = 1;
        Collections.sort(snippetList, new ScoreComparator());

        System.out.println(snippetList.size());
        for (Snippet snippet : snippetList) {
          try {
            snippetWriter.write("Q:"+question.getText()+ " Document:"+ snippet.getDocument()+" offsetBegin: "+ snippet.getOffsetInBeginSection()+" offsetEnd: "+snippet.getOffsetInEndSection()+" A: "+snippet.getText() +"\n");
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          
          Passage p = new Passage(jcas);
          p.setDocId(snippet.getDocument());
          p.setBeginSection(snippet.getBeginSection());
          p.setEndSection(snippet.getEndSection());
          p.setOffsetInBeginSection(snippet.getOffsetInBeginSection());
          p.setOffsetInEndSection(snippet.getOffsetInEndSection());
          p.setText(snippet.getText());
          System.out.println(snippet.score + " " + snippet.getText());
          i++;
          snippetSearchResult.setSnippets(p);
          
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          
          snippetSearchResult.addToIndexes();

          if (i > topRank)
            break;
        }
      }
    }
  }
}
