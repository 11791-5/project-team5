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
import org.apache.uima.jcas.cas.StringList;
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

  private static final int SnippetRetrievedNum = 1000;

  public static final String PUBMED_URL = "http://www.ncbi.nlm.nih.gov/pubmed/";

  private static final int MinConceptMatch = 1;

  private static final int SENTENCE_MODE = 1;

  private static final int WINDOWS_MODE = 2;

  private static final int WindowsSize = 6;

  private static final int NumOfShift = 20;

  int NOW_MODE = 2;


  public class ScoreComparator implements Comparator<Snippet> {
    public int compare(Snippet o1, Snippet o2) {

      if (o2.score > o1.score)
        return 1;
      else if (o2.score < o1.score)
        return -1;
      else
        return 0;
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

       // get synset from
      ArrayList<SynSet> as = Utils.fromFSListToCollection(question.getSynSets(), SynSet.class);

      edu.cmu.lti.oaqa.type.retrieval.SynSet synset;

      String query = "";
      ArrayList<ArrayList<String>> synonymListByGroup = new ArrayList<ArrayList<String>>();
      ArrayList<String> synonymList = new ArrayList<String>();
      for (SynSet syns : as) {
        ArrayList<String> tempAL = new ArrayList<String>();
        ArrayList<Synonym> synonyms = Utils.fromFSListToCollection(syns.getSynonyms(),
                Synonym.class);
        tempAL.add(syns.getOriginalToken());
        for (Synonym synonym : synonyms) {
          synonymList.add(synonym.getSynonym());
          tempAL.add(synonym.getSynonym());

        }
        synonymListByGroup.add(tempAL);
        synonymList.add(syns.getOriginalToken());

      }

      List<edu.cmu.lti.oaqa.type.retrieval.Document> documentItems = getDocumentItems(jcas);

      edu.cmu.lti.oaqa.type.retrieval.Document doc;

      String nowSection = "section.0";
      System.out.println("QuestionItems:  " + question.getText());
      System.out.println("DocumentItems:  " + documentItems.size());

      if (documentItems != null && !documentItems.isEmpty()) {
        rank = 0;
        ArrayList<Snippet> snippetList = new ArrayList<Snippet>();
    //    SnippetSearchResult snippetSearchResult = new SnippetSearchResult(jcas);

        for (edu.cmu.lti.oaqa.type.retrieval.Document document : documentItems) {
          doc = new edu.cmu.lti.oaqa.type.retrieval.Document(jcas);

          List<String> text = null;
          String docText = null;
          try {
            text = FullDocumentSources.getFullText(document);
            if (text == null)
              docText = document.getText();

          } catch (IOException e) {
            e.printStackTrace();
          }
          ArrayList<Snippet> snippets = new ArrayList<Snippet>();

          // concept matching?
          for (int i = 0; i < 1; i++) {
            int offsetPtr = 0;
            if (docText == null)
              docText = text.get(i);

            if (NOW_MODE == SENTENCE_MODE) {
              Reader reader = new StringReader(docText);
              DocumentPreprocessor dp = new DocumentPreprocessor(reader);
              List<String> sentenceTokens;
              for (List<HasWord> sentence : dp) {
                sentenceTokens = new ArrayList<String>();
                StringBuffer wholeSentence = new StringBuffer();
                for (HasWord word : sentence) {
                  sentenceTokens.add(word.word());
                  wholeSentence.append(word.word() + " ");
                }

                int conceptMatch = 0;
                String wholeSentenceStr = wholeSentence.toString();

                int kk = 0;
                for (ArrayList<String> synonymsGroup : synonymListByGroup) {
                  kk++;
                  for (String synonymTempStr : synonymsGroup) {
                    System.out.println(kk + "||" + synonymTempStr + "||" + wholeSentenceStr);
                    if (wholeSentenceStr.contains(synonymTempStr)) {
                      conceptMatch++;
                      break;
                    }
                  }
                }
                System.out.println(conceptMatch + " " + wholeSentenceStr);
                if (conceptMatch >= MinConceptMatch) {
                  SimilarityMeasures sm = new SimilarityMeasures();
                  double score = sm.getSimilarity(sentenceTokens, synonymList);

                  Snippet s = new Snippet(score, PUBMED_URL + document.getDocId(),
                          wholeSentence.toString(), offsetPtr, offsetPtr + sentence.size(),
                          nowSection, nowSection);

                  snippetList.add(s);
                }
                offsetPtr = offsetPtr + sentence.size();

              }
            } else {
              String[] terms = docText.split(" ");

              for (int w = 0; w < terms.length - WindowsSize; w += NumOfShift) {

                StringBuffer sentenceBuf = new StringBuffer();
                for (int j = 0; j < WindowsSize; j++) {
                  if (j == WindowsSize - 1)
                    sentenceBuf.append(terms[w + j]);
                  else
                    sentenceBuf.append(terms[w + j] + " ");
                }
                String sentence = sentenceBuf.toString();
                List<String> sentenceTokens;
                sentenceTokens = new ArrayList<String>();
                StringBuffer wholeSentence = new StringBuffer();
                int conceptMatch = 0;

                int kk = 0;
              
                for (ArrayList<String> synonymsGroup : synonymListByGroup) {
                  kk++;
                  for (String synonymTempStr : synonymsGroup) {
                    System.out.println(kk + "||" + synonymTempStr + "||" + sentence);
                    if (sentence.contains(synonymTempStr)) {
                      conceptMatch++;
                      break;
                    }
                  }
                }
                System.out.println(conceptMatch + " " + sentence);
                if (conceptMatch >= MinConceptMatch) {
                  SimilarityMeasures sm = new SimilarityMeasures();
                  double score = sm.getSimilarity(sentenceTokens, synonymList);

                  Snippet s = new Snippet(score, PUBMED_URL + document.getDocId(),
                          sentence, offsetPtr, offsetPtr + sentence.length(),
                          nowSection, nowSection);

                  snippetList.add(s);
                }
                offsetPtr = offsetPtr + sentence.length();
              }
            }
          }
        }
        int rankThreshold = 1;
        Collections.sort(snippetList, new ScoreComparator());

        System.out.println(snippetList.size());
        for (Snippet snippet : snippetList) {
          SnippetSearchResult snippetSearchResult = new SnippetSearchResult(jcas);
         try {
            snippetWriter.write("Q:" + question.getText() + " Document:" + snippet.getDocument()
                    + " offsetBegin: " + snippet.getOffsetInBeginSection() + " offsetEnd: "
                    + snippet.getOffsetInEndSection() + " A: " + snippet.getText() + "\n");
          } catch (IOException e) {
            e.printStackTrace();
          }

          System.out.println(snippet.score + " " + snippet.getText());

          snippetSearchResult.setSnippets(createPassage(snippet, jcas));
          rankThreshold++;

          snippetSearchResult.setQuestionsSyn(Utils.createStringList(jcas, synonymList));
          snippetSearchResult.addToIndexes();

          if (rankThreshold > SnippetRetrievedNum)
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
      edu.cmu.lti.oaqa.type.retrieval.Document document = (edu.cmu.lti.oaqa.type.retrieval.Document) documentsIter
              .next();
      documentItems.add(document);
    }
    return documentItems;
  }
}