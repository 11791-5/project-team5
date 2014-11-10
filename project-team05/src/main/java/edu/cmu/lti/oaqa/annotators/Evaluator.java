package edu.cmu.lti.oaqa.annotators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.stanford.nlp.util.CollectionUtils;

public class Evaluator extends JCasAnnotator_ImplBase {

  private ArrayList<Double> averageConceptPrecision = new ArrayList<Double>();

  private ArrayList<Double> averageDocumentPrecision = new ArrayList<Double>();

  private ArrayList<Double> averageTriplePrecision = new ArrayList<Double>();

  File evaluation = new File("evaluation.txt");

  FileWriter evaluationWriter = null;

  public void initialize(UimaContext u) {
    try {
      evaluationWriter = new FileWriter(evaluation);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

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
      List<String> goldTriples = getJsonTriplesAsStringList(GoldStandardSingleton.getInstance()
              .getGoldStandardAnswer().get(questionid).getTriples());

      // calculate metrics for concepts
      List<String> conceptItems = getConceptURIsAsList(aJCas);
      double conceptPrecision = getPrecision(conceptItems, goldConcepts);
      double conceptRecall = getRecall(conceptItems, goldConcepts);
      double conceptF = calcF(conceptPrecision, conceptRecall);
      double conceptAP = calcAP(goldConcepts, conceptItems);
      averageConceptPrecision.add(conceptAP);
      try {
        printQueryStats(conceptPrecision, conceptRecall, conceptF, conceptAP, "concept");
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }

      // calculate metrics for documents
      List<String> documentItems = getDocumentURIsAsList(aJCas);
      double documentPrecision = getPrecision(documentItems, goldDocuments);
      double documentRecall = getRecall(documentItems, goldDocuments);
      double documentF = calcF(documentPrecision, documentRecall);
      double documentAP = calcAP(goldDocuments, documentItems);
      averageDocumentPrecision.add(documentAP);
      try {
        printQueryStats(documentPrecision, documentRecall, documentF, documentAP, "document");
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      // calcualte metrics for triples
      List<String> tripleItems = getProcessedTriplesAsList(aJCas);
      double triplePrecision = getPrecision(tripleItems, goldTriples);
      double tripleRecall = getRecall(tripleItems, goldTriples);
      double tripleF = calcF(triplePrecision, tripleRecall);
      double tripleAP = calcAP(goldTriples, tripleItems);
      averageTriplePrecision.add(tripleAP);
      try {
        printQueryStats(triplePrecision, tripleRecall, tripleF, tripleAP, "triple");
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  private List<String> getJsonTriplesAsStringList(List<json.gson.Triple> triples) {
    List<String> tripleItems = new ArrayList<String>();
    if(triples!=null)
    {
      for(json.gson.Triple triple: triples) {
        String tripleString = "o-"+triple.getO()+"-p-"+triple.getP()+"-s-"+triple.getS();
        tripleItems.add(tripleString);
      }
    }
    return tripleItems;
  }

  private List<String> getProcessedTriplesAsList(JCas aJCas) {
    FSIterator<TOP> triples = aJCas.getJFSIndexRepository().getAllIndexedFS(Triple.type);
    List<String> tripleItems = new ArrayList<String>();
    while (triples.hasNext()) {
      Triple triple = (Triple) triples
              .next();
      String tripleString = "o-"+triple.getObject()+"-p-"+triple.getPredicate()+"-s-"+triple.getSubject();
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
  private void printQueryStats(double precision, double recall, double fScore, double ap,
          String type) throws IOException {
    evaluationWriter.write(String.format("%s precision: %f\n", type, precision));
    evaluationWriter.write(String.format("%s recall: %f\n", type, recall));
    evaluationWriter.write(String.format("%s f score: %f\n", type, fScore));
    evaluationWriter.write(String.format("%s average precision: %f\n", type, ap));
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
    if (totalRelItemsInList == 0) {
      return 0;
    }
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
  private List<String> getDocumentURIsAsList(JCas aJcas) {
    FSIterator<TOP> documents = aJcas.getJFSIndexRepository().getAllIndexedFS(
            edu.cmu.lti.oaqa.type.retrieval.Document.type);
    List<String> documentItems = new ArrayList<String>();
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
  private List<String> getConceptURIsAsList(JCas aJCas) {
    FSIterator<TOP> conceptSearchResults = aJCas.getJFSIndexRepository().getAllIndexedFS(
            ConceptSearchResult.type);
    List<String> conceptItems = new ArrayList<String>();
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
  public double getPrecision(List<String> hypotheses, List<String> gold) {
    if (hypotheses.size() == 0) {
      return 0;
    }
    return (double) getNumTruePositives(gold, hypotheses) / (hypotheses.size());
  }

  /**
   * Returns the recall given a list of hypotheses and gold standard list.
   * 
   * @param hypotheses
   * @param gold
   * @return
   */
  public double getRecall(List<String> hypotheses, List<String> gold) {
    if (gold.size() == 0) {
      return 0;
    }
    return (double) getNumTruePositives(gold, hypotheses) / (gold.size());
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
