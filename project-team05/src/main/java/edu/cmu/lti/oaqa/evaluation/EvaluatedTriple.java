package edu.cmu.lti.oaqa.evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.jcas.JCas;

import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;

/**
 * Object for performing evaluation over all triples.
 * @author root
 *
 */

public class EvaluatedTriple extends EvaluatedItem {

  private ArrayList<Double> objectAP = new ArrayList<Double>();

  private ArrayList<Double> predicateAP = new ArrayList<Double>();

  private ArrayList<Double> subjectAP = new ArrayList<Double>();

  private boolean isCalculatingSeparatePrecisions;

  public EvaluatedTriple(FileWriter writer) {
    super(writer);
    super.setItemType("triple");
    super.setItemTypeId(TripleSearchResult.type);
  }

  /**
   * Calculate all metrics for the given predicted and gold standard triples.
   * After calculating the usual metrics, also calculate "soft" metrics in terms
   * of predictes, subjects, and objects.
   */
  public void calculateItemMetrics(JCas aJCas, String queryId) throws IOException {
    super.calculateItemMetrics(aJCas, queryId);
    isCalculatingSeparatePrecisions = true;
    List<Object> currentToBeEvaluated = this.getToBeEvaluated();
    List<Object> currentGoldStandard = this.getGoldStandard();
    List<Object> tripleObjects = new ArrayList<Object>();
    List<Object> tripleSubjects = new ArrayList<Object>();
    List<Object> triplePredicates = new ArrayList<Object>();
    List<Object> goldStandardObjects = new ArrayList<Object>();
    List<Object> goldStandardSubjects = new ArrayList<Object>();
    List<Object> goldStandardPredicates = new ArrayList<Object>();
    for (Object o : currentToBeEvaluated) {
      Triple triple = (Triple) o;
      if (triple.getObject() != null) {
        tripleObjects.add(triple.getObject());
      }
      if (triple.getSubject() != null) {
        tripleSubjects.add(triple.getSubject());
      }
      if (triple.getPredicate() != null) {
        triplePredicates.add(triple.getPredicate());
      }
    }

    for (Object o : currentGoldStandard) {
      json.gson.Triple triple = (json.gson.Triple) o;
      goldStandardObjects.add(triple.getO());
      goldStandardSubjects.add(triple.getS());
      goldStandardPredicates.add(triple.getP());
    }
    this.objectAP.add(calculateIndividualParts(tripleObjects, goldStandardObjects, "object"));
    this.predicateAP.add(calculateIndividualParts(triplePredicates, goldStandardPredicates,
            "predicate"));
    this.subjectAP.add(calculateIndividualParts(tripleSubjects, goldStandardSubjects, "subject"));
    isCalculatingSeparatePrecisions = false;
  }

  /**
   * Calculate all metrics for the given component of a triple.
   * @param hypothesis
   * @param gold
   * @param componentName
   * @return
   * @throws IOException
   */
  private double calculateIndividualParts(List<Object> hypothesis, List<Object> gold,
          String componentName) throws IOException {
    double componentPrecision = super.getPrecision(hypothesis, gold);
    double componentRecall = super.getRecall(hypothesis, gold);
    double componentF = super.calcF(componentPrecision, componentRecall);
    double componentAP = super.calcAP(hypothesis, gold);
    super.getWriter().write(
            String.format("triple %s precision: %f\n", componentName, componentPrecision));
    super.getWriter()
            .write(String.format("triple %s recall: %f\n", componentName, componentRecall));
    super.getWriter().write(String.format("triple %s f: %f\n", componentName, componentF));
    return componentAP;
  }

  @Override
  public List<Object> getEvaluatedItemsAsList(List<Object> itemObjects) {
    List<Object> triples = new ArrayList<Object>();
    for (Object triple : itemObjects) {
      triples.add(((TripleSearchResult) triple).getTriple());
    }
    return triples;
  }

  @Override
  public List<Object> getGoldStandardItems(String questionId) {
    List<Object> goldTriples = new ArrayList<Object>();
    List<json.gson.Triple> goldTriplesReturned = GoldStandardSingleton.getInstance()
            .getGoldStandardAnswer().get(questionId).getTriples();
    if (goldTriplesReturned != null) {
      goldTriples.addAll(goldTriplesReturned);
    }
    return goldTriples;
  }

  public ArrayList<Double> getObjectAP() {
    return objectAP;
  }

  /**
   * Calculate precision for triples.
   */
  @Override
  public double getPrecision(List<Object> tripleItems, List<Object> goldTriples) {
    if (isCalculatingSeparatePrecisions) {
      return super.getPrecision(tripleItems, tripleItems);
    }
    int numTruePos = 0;
    // in order to increment number of true positives, all parts of the triple must match.
    for (Object tripleObj : tripleItems) {
      Triple triple = (Triple) tripleObj;
      for (Object goldTripleObj : goldTriples) {
        json.gson.Triple goldTriple = (json.gson.Triple) goldTripleObj;
        if (triple.getObject() != null && triple.getObject().equalsIgnoreCase(goldTriple.getO())
                && triple.getSubject() != null && triple.getSubject().equalsIgnoreCase(goldTriple.getS())
                && triple.getPredicate() != null
                && triple.getPredicate().equalsIgnoreCase(goldTriple.getP())) {
          numTruePos++;
        }
      }
    }
    return (double) numTruePos / tripleItems.size();
  }

  public ArrayList<Double> getPredicateAP() {
    return predicateAP;
  }

  public ArrayList<Double> getSubjectAP() {
    return subjectAP;
  }

  public void setObjectAP(ArrayList<Double> objectAP) {
    this.objectAP = objectAP;
  }

  public void setPredicateAP(ArrayList<Double> predicateAP) {
    this.predicateAP = predicateAP;
  }

  public void setSubjectAP(ArrayList<Double> subjectAP) {
    this.subjectAP = subjectAP;
  }
}
