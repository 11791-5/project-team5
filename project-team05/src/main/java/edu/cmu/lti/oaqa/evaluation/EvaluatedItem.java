package edu.cmu.lti.oaqa.evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.jcas.JCas;

import util.Utils;
import edu.stanford.nlp.util.CollectionUtils;

public abstract class EvaluatedItem {
  private ArrayList<Double> averagePrecision = new ArrayList<Double>();
  private FileWriter writer;

  private String itemType;

  private int itemTypeId;
  private List<Object> goldStandard;

  public List<Object> getToBeEvaluated() {
    return toBeEvaluated;
  }

  public void setToBeEvaluated(List<Object> toBeEvaluated) {
    this.toBeEvaluated = toBeEvaluated;
  }

  public List<Object> getGoldStandard() {
    return goldStandard;
  }

  public void setGoldStandard(List<Object> goldStandard) {
    this.goldStandard = goldStandard;
  }

  private List<Object> toBeEvaluated;

  public EvaluatedItem(FileWriter writer) {
    this.setWriter(writer);
  }
  
  /**
   * Method to take the source object as input and return a list of
   * items to be evaluated on.
   * @param itemObjects
   * @return
   */
  public abstract List<Object> getEvaluatedItemsAsList(List<Object> itemObjects);

  public abstract List<Object> getGoldStandardItems(String questionId);
  
  public void calculateItemMetrics(JCas aJCas, String queryId) throws IOException {
    goldStandard = getGoldStandardItems(queryId);
    // calculate metrics for documents
    List<Object> itemObjects = Utils.extractUIMATypeAsList(
            this.getItemTypeId(), aJCas);
    toBeEvaluated = getEvaluatedItemsAsList(itemObjects);
    double precision = getPrecision(toBeEvaluated, goldStandard);
    double recall = getRecall(toBeEvaluated, goldStandard);
    double fScore = calcF(precision, recall);
    double itemAveragePrecision = calcAP(goldStandard, toBeEvaluated);
    if((!goldStandard.isEmpty()) || !itemObjects.isEmpty()) getAveragePrecision().add(itemAveragePrecision);
    try {
      printQueryStats(queryId, precision, recall, fScore, itemAveragePrecision, this.getItemType());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
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
   * @throws IOException
   */
  protected void printQueryStats(String queryId, double precision, double recall, double fScore,
          double ap, String type) throws IOException {
    getWriter().write(String.format("%s precision: %f\n", type, precision));
    getWriter().write(String.format("%s recall: %f\n", type, recall));
    getWriter().write(String.format("%s f score: %f\n", type, fScore));
    getWriter().write(String.format("%s average precision: %f\n\n", type, ap));
  }

  public double getPrecision(List<Object> hypotheses, List<Object> gold) {
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
   * Calculates average precision for the given list of hypothesis items given the list of golden
   * items.
   * 
   * @param goldItems
   * @param hypothesisItems
   * @return
   */
  protected double calcAP(List<Object> goldItems, List<Object> hypothesisItems) {
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
  public double calcF(double precision, double recall) {
    if (precision + recall == 0) {
      return 0;
    }
    return 2 * (precision * recall) / (precision + recall);
  }

  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public void setItemTypeId(int type) {
    this.itemTypeId = type;
  }

  public ArrayList<Double> getAveragePrecision() {
    return averagePrecision;
  }

  public void setAveragePrecision(ArrayList<Double> averagePrecision) {
    this.averagePrecision = averagePrecision;
  }

  public int getItemTypeId() {
    return itemTypeId;
  }

  public FileWriter getWriter() {
    return writer;
  }

  public void setWriter(FileWriter writer) {
    this.writer = writer;
  }
}
