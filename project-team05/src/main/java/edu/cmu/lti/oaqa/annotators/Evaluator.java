package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.util.FSCollectionFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.ConceptMention;
import edu.cmu.lti.oaqa.type.kb.DocumentP;
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

      List<String> documentItems = getDocumentURIsAsList(aJCas);
      double documentPrecision = getPrecision(documentItems, goldDocuments);
      double documentRecall = getRecall(documentItems, goldDocuments);
      double documentF = calcF(documentPrecision, documentRecall);
      double documentAP = calcAP(goldDocuments, documentItems);
      averageDocumentPrecision.add(documentAP);
    }
  }

  private double calcAP(List<String> goldConcepts, List<String> conceptItems) {
    int totalRelItemsInList = getNumTruePositives(conceptItems, goldConcepts);
    double averageConceptPrecision = 0;
    for (int i = 0; i < conceptItems.size(); i++) {
      if (goldConcepts.contains(conceptItems.get(i))) {
        double precisionAtR = getPrecision(conceptItems.subList(0, i), goldConcepts);
        averageConceptPrecision += precisionAtR;
      }
    }
    averageConceptPrecision = averageConceptPrecision / totalRelItemsInList;
    return averageConceptPrecision;
  }

  private double calcF(double precision, double recall) {
    return 2 * (precision * recall) / (precision + recall);
  }

  private List<String> getDocumentURIsAsList(JCas aJcas) {
    FSIterator<Annotation> documents = aJcas.getAnnotationIndex(DocumentP.type).iterator();
    List<String> documentItems = new ArrayList<String>();
    while (documents.hasNext()) {
      DocumentP document = (DocumentP) documents.next();
      documentItems.add(document.getURI());
    }
    return documentItems;
  }

  private List<String> getConceptURIsAsList(JCas aJCas) {
    FSIterator<Annotation> concepts = aJCas.getAnnotationIndex(ConceptMention.type).iterator();
    List<String> conceptItems = new ArrayList<String>();
    while (concepts.hasNext()) {
      ConceptMention conceptMention = (ConceptMention) concepts.next();
      StringList l = conceptMention.getConcept().getUris();
      Collection<String> listForConcept = FSCollectionFactory.create(l);
      conceptItems.addAll(listForConcept);
    }
    return conceptItems;
  }

  public double getPrecision(List<String> hypotheses, List<String> gold) {
    return getNumTruePositives(gold, hypotheses) / (hypotheses.size());
  }

  public double getRecall(List<String> hypotheses, List<String> gold) {
    return getNumTruePositives(gold, hypotheses) / (gold.size());
  }

  public int getNumTruePositives(List<String> hypothesis, List<String> gold) {
    return CollectionUtils.intersection(new HashSet<String>(hypothesis), new HashSet<String>(gold))
            .size();
  }

  public double calcAvg(ArrayList<Double> vals) {
    double result = 0;
    for (Double val : vals) {
      result += val;
    }
    return result / vals.size();
  }

  public double calculateGeomAvg(ArrayList<Double> vals) {
    double result = 1;
    double epsilon = 0.01;
    for (Double val : vals) {
      result *= (val + epsilon);
    }
    return Math.sqrt(result);
  }

  public void collectionProcessComplete() {
    double conceptMap = calcAvg(averageConceptPrecision);
    double documentMap = calcAvg(averageDocumentPrecision);
    double conceptGmap = calculateGeomAvg(averageConceptPrecision);
    double documentGmap = calculateGeomAvg(averageDocumentPrecision);
  }

}
