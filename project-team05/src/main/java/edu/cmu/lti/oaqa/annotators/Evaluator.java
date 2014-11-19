package edu.cmu.lti.oaqa.annotators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import json.gson.Snippet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;
import edu.stanford.nlp.util.CollectionUtils;

public class Evaluator extends JCasAnnotator_ImplBase {

  private ArrayList<Double> averageConceptPrecision = new ArrayList<Double>();

  private ArrayList<Double> averageDocumentPrecision = new ArrayList<Double>();

  private ArrayList<Double> averageTriplePrecision = new ArrayList<Double>();

  private ArrayList<Double> averageSnippetPrecision = new ArrayList<Double>();

  File evaluation = new File("evaluation.txt");

  FileWriter evaluationWriter = null;

  public static void main(String[] args) {
    List<Object> articleOffsetPairs = new ArrayList<Object>();
    ArrayList<Object> newList1 = new ArrayList<Object>();
    extractDocumentOffsetPairs(newList1, "as", 14, "doc1");
    ArrayList<Object> newList2 = new ArrayList<Object>();
    extractDocumentOffsetPairs(newList2, "asdf", 12, "doc2");
    articleOffsetPairs.add(newList1);
    articleOffsetPairs.add(newList2);

    List<Object> goldArticleOffsetPairs = new ArrayList<Object>();


    extractDocumentOffsetPairs(goldArticleOffsetPairs, "t", 15, "doc1");

    extractDocumentOffsetPairs(goldArticleOffsetPairs, "tsdf", 10, "doc2");


    //TODO: should they be in a set?
    ArrayList<Object> allRecalledArticleOffsetPairsList = new ArrayList<Object>();
    for(Object o: articleOffsetPairs) {
      allRecalledArticleOffsetPairsList.addAll(((ArrayList<Object>)o));
    }
    double passagePrecision = getPrecision(allRecalledArticleOffsetPairsList, goldArticleOffsetPairs);
    System.out.println(passagePrecision);
  }
  
  public void initialize(UimaContext u) {
    try {
      evaluationWriter = new FileWriter(evaluation);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> questions = aJCas.getAnnotationIndex(ExpandedQuestion.type).iterator();
    while (questions.hasNext()) {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();
      String questionid = question.getId();
      List<Object> goldConcepts = new ArrayList<Object>(GoldStandardSingleton.getInstance().getGoldStandardAnswer()
              .get(questionid).getConcepts());
      List<Object> goldDocuments = new ArrayList<Object>(GoldStandardSingleton.getInstance().getGoldStandardAnswer()
              .get(questionid).getDocuments());
      List<Object> goldTriples = new ArrayList<Object>(getJsonTriplesAsStringList(GoldStandardSingleton.getInstance()
              .getGoldStandardAnswer().get(questionid).getTriples()));
      List<Snippet> goldSnippets = GoldStandardSingleton.getInstance().getGoldStandardAnswer()
              .get(questionid).getSnippets();

      calculateConceptsMetrics(aJCas, goldConcepts, questionid);

      calculateDocumentMetrics(aJCas, goldDocuments, questionid);

      calculateTriplesMetrics(aJCas, goldTriples, questionid);

      calculateSnippetsMetrics(aJCas, goldSnippets, questionid);
    }
  }

  private void calculateSnippetsMetrics(JCas aJCas, List<Snippet> goldSnippets, String queryId) {
    // calcualte metrics for triples
    List<SnippetSearchResult> passageItems = getProcessedSnippetsAsList(aJCas);
    List<ArrayList<Object>> retrievedArticleOffsetPairs = new ArrayList<ArrayList<Object>>();
    for(SnippetSearchResult currentSnippet: passageItems) {
      //ArrayList<Passage> passageList = util.Utils.fromFSListToPassageList(currentSnippet.getSnippets(), Passage.class);
      Passage p = currentSnippet.getSnippets(); 
      ArrayList<Object> articleOffsetPairs = new ArrayList<Object>();
      extractDocumentOffsetPairs(articleOffsetPairs, p.getText(), p.getOffsetInBeginSection(), p.getDocId());
      retrievedArticleOffsetPairs.add(articleOffsetPairs);
    }

    List<Object> goldArticleOffsetPairs = new ArrayList<Object>();
    for(Snippet s: goldSnippets) {
      extractDocumentOffsetPairs(goldArticleOffsetPairs, s.getText(), s.getOffsetInBeginSection(), s.getDocument());
    }

    //TODO: should they be in a set?
    ArrayList<Object> allRecalledArticleOffsetPairsList = new ArrayList<Object>();
    for(Object o: retrievedArticleOffsetPairs) {
      allRecalledArticleOffsetPairsList.addAll(((ArrayList<Object>)o));
    }
    double passagePrecision = getPrecision(allRecalledArticleOffsetPairsList, goldArticleOffsetPairs);
    double passageRecall = getRecall(allRecalledArticleOffsetPairsList, goldArticleOffsetPairs);
    double passageF = calcF(passagePrecision, passageRecall);
    double passageAP = calcAPForSnippets(goldArticleOffsetPairs, retrievedArticleOffsetPairs);

    averageSnippetPrecision.add(passageAP);
    
    try {
      printQueryStats(queryId, passagePrecision, passageRecall, passageF, passageAP, "snippet");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private static double calcAPForSnippets(List<Object> goldArticleOffsetPairs,
          List<ArrayList<Object>> retrievedArticleOffsetPairs) {
    ArrayList<Object> allList = new ArrayList<Object>();
    for(Object o: retrievedArticleOffsetPairs) {
      allList.addAll(((ArrayList<Object>)o));
    }
    int totalRelItemsInList = getNumTruePositives(allList, goldArticleOffsetPairs);
    if (totalRelItemsInList == 0) {
      return 0;
    }
    
    double averagePrecision = 0;
    for (int i = 0; i < retrievedArticleOffsetPairs.size(); i++) {
      // if nonzero overlap between golden snippets and the snippet at current index
      if (!CollectionUtils.intersection(new HashSet<Object>(goldArticleOffsetPairs), new HashSet<Object>(retrievedArticleOffsetPairs.get(i))).isEmpty()) {;
        ArrayList<Object> subList = new ArrayList<Object>();
        for(Object o: retrievedArticleOffsetPairs.subList(0, i+1)) {
          subList.addAll(((ArrayList<Object>)o));
        }
        double precisionAtR = getPrecision(subList, goldArticleOffsetPairs);
        averagePrecision += precisionAtR;
      }
    }

    averagePrecision = averagePrecision / totalRelItemsInList;
    return averagePrecision;
  }

  private static void extractDocumentOffsetPairs(List<Object> goldArticleOffsetPairs, String docText, int offsetBegin, String docId) {
    char[] passageChars = docText.toCharArray();
    for(int i = 0; i < passageChars.length; i++) {
      Pair<String, Integer> pair = new ImmutablePair<String, Integer>(docId, offsetBegin+i);
      goldArticleOffsetPairs.add(pair);
    }
  }

  private List<SnippetSearchResult> getProcessedSnippetsAsList(JCas aJCas) {
    FSIterator<TOP> snippets = aJCas.getJFSIndexRepository().getAllIndexedFS(SnippetSearchResult.type);
    List<SnippetSearchResult> snippetItems = new ArrayList<SnippetSearchResult>();
    while (snippets.hasNext()) {
      SnippetSearchResult passage = (SnippetSearchResult) snippets.next();
      snippetItems.add(passage);
    }
    return snippetItems;
  }

  private void calculateTriplesMetrics(JCas aJCas, List<Object> goldTriples, String queryId) {
    // calcualte metrics for triples
    List<Object> tripleItems = getProcessedTriplesAsList(aJCas);
    double triplePrecision = getPrecision(tripleItems, goldTriples);
    double tripleRecall = getRecall(tripleItems, goldTriples);
    double tripleF = calcF(triplePrecision, tripleRecall);
    double tripleAP = calcAP(goldTriples, tripleItems);
    averageTriplePrecision.add(tripleAP);
    try {
      printQueryStats(queryId, triplePrecision, tripleRecall, tripleF, tripleAP, "triple");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void calculateDocumentMetrics(JCas aJCas, List<Object> goldDocuments, String queryId) {
    // calculate metrics for documents
    List<Object> documentItems = getDocumentURIsAsList(aJCas);
    double documentPrecision = getPrecision(documentItems, goldDocuments);
    double documentRecall = getRecall(documentItems, goldDocuments);
    double documentF = calcF(documentPrecision, documentRecall);
    double documentAP = calcAP(goldDocuments, documentItems);
    averageDocumentPrecision.add(documentAP);
    try {
      printQueryStats(queryId, documentPrecision, documentRecall, documentF, documentAP, "document");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void calculateConceptsMetrics(JCas aJCas, List<Object> goldConcepts, String queryId) {
    // calculate metrics for concepts
    List<Object> conceptItems = getConceptURIsAsList(aJCas);
    double conceptPrecision = getPrecision(conceptItems, goldConcepts);
    double conceptRecall = getRecall(conceptItems, goldConcepts);
    double conceptF = calcF(conceptPrecision, conceptRecall);
    double conceptAP = calcAP(goldConcepts, conceptItems);
    averageConceptPrecision.add(conceptAP);
    try {
      printQueryStats(queryId, conceptPrecision, conceptRecall, conceptF, conceptAP, "concept");
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  private List<String> getJsonTriplesAsStringList(List<json.gson.Triple> triples) {
    List<String> tripleItems = new ArrayList<String>();
    if (triples != null) {
      for (json.gson.Triple triple : triples) {
        String tripleString = "o-" + triple.getO() + "-p-" + triple.getP() + "-s-" + triple.getS();
        tripleItems.add(tripleString);
      }
    }
    return tripleItems;
  }

  private List<Object> getProcessedTriplesAsList(JCas aJCas) {
    FSIterator<TOP> triples = aJCas.getJFSIndexRepository().getAllIndexedFS(TripleSearchResult.type);
    List<Object> tripleItems = new ArrayList<Object>();
    while (triples.hasNext()) {
      Triple triple = ((TripleSearchResult) triples.next()).getTriple();
      String tripleString = "o-" + triple.getObject() + "-p-" + triple.getPredicate() + "-s-"
              + triple.getSubject();
      tripleItems.add(tripleString);
    }
    return tripleItems;
  }

  /**
   * Prints precision, recall, fscore, and average precision for the given type.
   * 
   * @param precision
   * @param recall
   * @param fScore
   * @param ap
   * @param type
   * @throws IOException
   */
  private void printQueryStats(String queryId, double precision, double recall, double fScore, double ap,
          String type) throws IOException {
    evaluationWriter.write(String.format("Query id: %s\n", queryId));
    evaluationWriter.write(String.format("%s precision: %f\n", type, precision));
    evaluationWriter.write(String.format("%s recall: %f\n", type, recall));
    evaluationWriter.write(String.format("%s f score: %f\n", type, fScore));
    evaluationWriter.write(String.format("%s average precision: %f\n\n", type, ap));
  }

  /**
   * Calculates average precision for the given list of hypothesis items given the list of golden
   * items.
   * 
   * @param goldItems
   * @param hypothesisItems
   * @return
   */
  private double calcAP(List<Object> goldItems, List<Object> hypothesisItems) {
    int totalRelItemsInList = getNumTruePositives(hypothesisItems, goldItems);
    if (totalRelItemsInList == 0) {
      return 0;
    }
    
    double averagePrecision = 0;
    for (int i = 0; i < hypothesisItems.size(); i++) {
      if (goldItems.contains(hypothesisItems.get(i))) {
        double precisionAtR = getPrecision(hypothesisItems.subList(0, i+1), goldItems);
        averagePrecision += precisionAtR;
      }
    }

    averagePrecision = averagePrecision / totalRelItemsInList;
    return averagePrecision;
  }

  /**
   * Calculates f-score given the precision and recall.
   * 
   * @param precision
   * @param recall
   * @return
   */
  private double calcF(double precision, double recall) {
    if (precision + recall == 0) {
      return 0;
    }
    return 2 * (precision * recall) / (precision + recall);
  }

  /**
   * Returns a list of document URIs given the JCas.
   * 
   * @param aJcas
   * @return
   */
  private List<Object> getDocumentURIsAsList(JCas aJcas) {
    FSIterator<TOP> documents = aJcas.getJFSIndexRepository().getAllIndexedFS(
            edu.cmu.lti.oaqa.type.retrieval.Document.type);
    List<Object> documentItems = new ArrayList<Object>();
    while (documents.hasNext()) {
      edu.cmu.lti.oaqa.type.retrieval.Document document = (edu.cmu.lti.oaqa.type.retrieval.Document) documents
              .next();
      documentItems.add(document.getDocId());
    }
    return documentItems;
  }

  /**
   * Returns a list of concept URIs given the JCas.
   * 
   * @param aJCas
   * @return
   */
  private List<Object> getConceptURIsAsList(JCas aJCas) {
    FSIterator<TOP> conceptSearchResults = aJCas.getJFSIndexRepository().getAllIndexedFS(
            ConceptSearchResult.type);
    List<Object> conceptItems = new ArrayList<Object>();
    while (conceptSearchResults.hasNext()) {
      ConceptSearchResult conceptResult = (ConceptSearchResult) conceptSearchResults.next();
      conceptItems.add(conceptResult.getUri().replace("GO:", "").replace("2014", "2012"));
    }
    return conceptItems;
  }

  /**
   * Returns the precision given a list of hypotheses and gold standard list.
   * 
   * @param hypotheses
   * @param gold
   * @return
   */
  public static double getPrecision(List<Object> hypotheses, List<Object> gold) {
    if (hypotheses.size() == 0) {
      return 0;
    }
    return (double) getNumTruePositives(hypotheses, gold) / (hypotheses.size());
  }

  /**
   * Returns the recall given a list of hypotheses and gold standard list.
   * 
   * @param hypotheses
   * @param gold
   * @return
   */
  public double getRecall(List<Object> hypotheses, List<Object> gold) {
    if (gold.size() == 0) {
      return 0;
    }
    return (double) getNumTruePositives(hypotheses, gold) / (gold.size());
  }

  /**
   * Returns the true positives by getting intersection between the list of hypotheses and gold
   * standard list.
   * 
   * @param hypothesis
   * @param gold
   * @return
   */
  public static int getNumTruePositives(Collection<?> hypothesis, Collection<?> gold) {
    return CollectionUtils.intersection(new HashSet<Object>(hypothesis), new HashSet<Object>(gold))
            .size();
  }

  /**
   * Convenience method to calculate the arithmetic average of a list of values.
   * 
   * @param vals
   * @return
   */
  public double calcArithmeticAvg(ArrayList<Double> vals) {
    if (vals.size() == 0) {
      return 0;
    }
    double result = 0;

    for (Double val : vals) {
      result += val;
    }
    return result / vals.size();
  }

  /**
   * Convenience method to calculate the geometric average of a list of values.
   * 
   * @param vals
   * @return
   */
  public double calculateGeomAvg(ArrayList<Double> vals) {
    if (vals.size() == 0) {
      return 0;
    }
    double result = 1;
    double epsilon = 0.01;
    for (Double val : vals) {
      result *= (val + epsilon);
    }
    return Math.sqrt(result);
  }

  /**
   * Calculate and print the mean average precision and geometric mean average precision for the
   * queries processed in the collection.
   */
  public void collectionProcessComplete() {
    calcAndPrintFinalStatsForType("concept", averageConceptPrecision);
    calcAndPrintFinalStatsForType("document", averageDocumentPrecision);
    calcAndPrintFinalStatsForType("triple", averageTriplePrecision);
    calcAndPrintFinalStatsForType("snippet", averageSnippetPrecision);

    try {
      evaluationWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void calcAndPrintFinalStatsForType(String type, ArrayList<Double> typeVals) {
    double typeMap = calcArithmeticAvg(typeVals);
    double typeGmap = calculateGeomAvg(typeVals);
    try {
      printFinalStats(typeMap, typeGmap, type);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  /**
   * Prints final stats for the given type.
   * 
   * @param map
   * @param gmap
   * @param type
   * @throws IOException
   */
  private void printFinalStats(double map, double gmap, String type) throws IOException {
    evaluationWriter.write((String.format("%s MAP: %f\n", type, map)));
    evaluationWriter.write((String.format("%s GMAP: %f\n", type, gmap)));
  }

}
