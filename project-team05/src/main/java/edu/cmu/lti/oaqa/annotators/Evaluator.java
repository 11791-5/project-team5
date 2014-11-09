package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.stanford.nlp.util.CollectionUtils;

public class Evaluator extends JCasAnnotator_ImplBase {

  private ArrayList<Double> averageConceptPrecision = new ArrayList<Double>();

  private ArrayList<Double> averageDocumentPrecision = new ArrayList<Double>();

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> questions = aJCas.getAnnotationIndex(Question.type).iterator();
    while (questions.hasNext()) {
      Question question = (Question) questions.next();
      String questionid = question.getId();
      List<String> goldConcepts = GoldStandardSingleton.getInstance().getGoldStandardAnswer()
              .get(questionid).getConcepts();
      List<String> goldDocuments = GoldStandardSingleton.getInstance().getGoldStandardAnswer()
              .get(questionid).getDocuments();

      List<String> conceptItems = getConceptURIsAsList(aJCas);

      double conceptPrecision = getPrecision(conceptItems, goldConcepts);
      double conceptRecall = getRecall(conceptItems, goldConcepts);
      double conceptF = calcF(conceptPrecision, conceptRecall);
      double conceptAP = calcAP(goldConcepts, conceptItems);
      averageConceptPrecision.add(conceptAP);
      printQueryStats(conceptPrecision, conceptRecall, conceptF, conceptAP, "concept");

      List<String> documentItems = getDocumentURIsAsList(aJCas);
      double documentPrecision = getPrecision(documentItems, goldDocuments);
      double documentRecall = getRecall(documentItems, goldDocuments);
      double documentF = calcF(documentPrecision, documentRecall);
      double documentAP = calcAP(goldDocuments, documentItems);
      averageDocumentPrecision.add(documentAP);
      printQueryStats(documentPrecision, documentRecall, documentF, documentAP, "document");
    }
  }

  /**
   * Prints precision, recall, fscore, and average precision for the given type.
   * 
   * @param precision
   * @param recall
   * @param fScore
   * @param ap
   * @param type
   */
  private void printQueryStats(double precision, double recall, double fScore, double ap,
          String type) {
    System.out.println(String.format("%s precision: %f", type, precision));
    System.out.println(String.format("%s recall: %f", type, recall));
    System.out.println(String.format("%s f score: %f", type, fScore));
    System.out.println(String.format("%s average precision: %f", type, ap));
  }

  /**
   * Calculates average precision for the given list of hypothesis items given the list of golden
   * items.
   * 
   * @param goldItems
   * @param hypothesisItems
   * @return
   */
  private double calcAP(List<String> goldItems, List<String> hypothesisItems) {
    int totalRelItemsInList = getNumTruePositives(hypothesisItems, goldItems);
    double averagePrecision = 0;
    for (int i = 0; i < hypothesisItems.size(); i++) {
      if (goldItems.contains(hypothesisItems.get(i))) {
        double precisionAtR = getPrecision(hypothesisItems.subList(0, i), goldItems);
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
    return 2 * (precision * recall) / (precision + recall);
  }

  /**
   * Returns a list of document URIs given the JCas.
   * 
   * @param aJcas
   * @return
   */
  private List<String> getDocumentURIsAsList(JCas aJcas) {
    FSIterator<TOP> documents = aJcas.getJFSIndexRepository().getAllIndexedFS(edu.cmu.lti.oaqa.type.retrieval.Document.type);
    List<String> documentItems = new ArrayList<String>();
    while (documents.hasNext()) {
      edu.cmu.lti.oaqa.type.retrieval.Document document = (edu.cmu.lti.oaqa.type.retrieval.Document) documents.next();
      documentItems.add(document.getUri());
    }
    return documentItems;
  }

  /**
   * Returns a list of concept URIs given the JCas.
   * 
   * @param aJCas
   * @return
   */
  private List<String> getConceptURIsAsList(JCas aJCas) {
    FSIterator<TOP> conceptSearchResults = aJCas.getJFSIndexRepository().getAllIndexedFS(ConceptSearchResult.type);
    List<String> conceptItems = new ArrayList<String>();
    while(conceptSearchResults.hasNext())
    {
      ConceptSearchResult conceptResult = (ConceptSearchResult)conceptSearchResults.next();
      conceptItems.add(conceptResult.getUri());
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
  public double getPrecision(List<String> hypotheses, List<String> gold) {
    return getNumTruePositives(gold, hypotheses) / (hypotheses.size());
  }

  /**
   * Returns the recall given a list of hypotheses and gold standard list.
   * 
   * @param hypotheses
   * @param gold
   * @return
   */
  public double getRecall(List<String> hypotheses, List<String> gold) {
    return getNumTruePositives(gold, hypotheses) / (gold.size());
  }

  /**
   * Returns the true positives by getting intersection between the list of hypotheses and gold
   * standard list.
   * 
   * @param hypothesis
   * @param gold
   * @return
   */
  public int getNumTruePositives(List<String> hypothesis, List<String> gold) {
    return CollectionUtils.intersection(new HashSet<String>(hypothesis), new HashSet<String>(gold))
            .size();
  }

  /**
   * Convenience method to calculate the arithmetic average of a list of values.
   * 
   * @param vals
   * @return
   */
  public double calcArithmeticAvg(ArrayList<Double> vals) {
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
    double conceptMap = calcArithmeticAvg(averageConceptPrecision);
    double documentMap = calcArithmeticAvg(averageDocumentPrecision);
    double conceptGmap = calculateGeomAvg(averageConceptPrecision);
    double documentGmap = calculateGeomAvg(averageDocumentPrecision);
    printFinalStats(conceptMap, conceptGmap, "concept");
    printFinalStats(documentMap, documentGmap, "document");

  }

  /**
   * Prints final stats for the given type.
   * @param map
   * @param gmap
   * @param type
   */
  private void printFinalStats(double map, double gmap, String type) {
    System.out.println(String.format("%s MAP: %f", type, map));
    System.out.println(String.format("%s GMAP: %f", type, gmap));
  }

}
