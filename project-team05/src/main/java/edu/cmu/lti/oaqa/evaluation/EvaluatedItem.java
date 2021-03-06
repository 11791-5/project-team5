package edu.cmu.lti.oaqa.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.uima.jcas.JCas;

import util.Utils;
import edu.stanford.nlp.util.CollectionUtils;

/**
 * Abstract class representing a type of retrieved object
 * being evaluated throughout the pipeline for a series of
 * questions. Accumulates the average precision over all
 * questions for the retrieved object type.
 * @author root
 *
 */
public abstract class EvaluatedItem {
  private ArrayList<Double> averagePrecision = new ArrayList<Double>();
  private ArrayList<Double> allPrecisions = new ArrayList<Double>();
  private ArrayList<Double> allRecalls = new ArrayList<Double>();
  private ArrayList<Double> allFScores = new ArrayList<Double>();
  private FileWriter writer;
  private FileWriter exactAnswerWriter = null;

  private String itemType;

  private int itemTypeId;
  private List<Object> goldStandard;

  /**
   * Get objects to be evaluated
   * @return
   */
  public List<Object> getToBeEvaluated() {
    return toBeEvaluated;
  }

  /**
   * Set object to be evaluated
   * @param toBeEvaluated
   */
  public void setToBeEvaluated(List<Object> toBeEvaluated) {
    this.toBeEvaluated = toBeEvaluated;
  }

  /**
   * Getter for gold standard
   * @return
   */
  public List<Object> getGoldStandard() {
    return goldStandard;
  }

  /**
   * Setter for gold standard
   * @param goldStandard
   */
  public void setGoldStandard(List<Object> goldStandard) {
    this.goldStandard = goldStandard;
  }

  private List<Object> toBeEvaluated;

  /**
   * 
   * @param writer
   */
  public EvaluatedItem(FileWriter writer) {
    try {
      this.setExactAnswerWriter(new FileWriter(new File("exactAnswerResults.txt")));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    this.setWriter(writer);
  }
  
  /**
   * Method to take the source object as input and return a list of
   * items to be evaluated on.
   * @param itemObjects
   * @return
   */
  public abstract List<Object> getEvaluatedItemsAsList(List<Object> itemObjects);

  /**
   * Method to take in the question ID and return the list of 
   * gold standard items needed for comparison.
   * @param questionId
   * @return
   */
  public abstract List<Object> getGoldStandardItems(String questionId);
  
  /**
   * Given the item currently being processed and the query ID,
   * retrieve the gold standard and the list of items to be
   * evaluated and calculate metrics.
   * @param aJCas
   * @param queryId
   * @throws IOException
   */
  public void calculateItemMetrics(JCas aJCas, String queryId) throws IOException {
    goldStandard = getGoldStandardItems(queryId);
    // calculate metrics for documents
    List<Object> itemObjects = Utils.extractUIMATypeAsList(
            this.getItemTypeId(), aJCas);
    toBeEvaluated = getEvaluatedItemsAsList(itemObjects);
    if(EvaluatedItem.this instanceof EvaluatedExactAnswer) {
      ((EvaluatedExactAnswer)this).getExactAnswerWriter().write("Gold standard for question "+queryId + ":"+ goldStandard+"\n");
      ((EvaluatedExactAnswer)this).getExactAnswerWriter().write("Hypothesis for question "+queryId + ":"+ toBeEvaluated+"\n");
      ((EvaluatedExactAnswer)this).getExactAnswerWriter().flush();
    }
    double precision = getPrecision(toBeEvaluated, goldStandard);
    double recall = getRecall(toBeEvaluated, goldStandard);
    double fScore = calcF(precision, recall);
    double itemAveragePrecision = calcAP(goldStandard, toBeEvaluated);
    if((!goldStandard.isEmpty()) || !itemObjects.isEmpty()) {
      getAveragePrecision().add(itemAveragePrecision);
      this.getAllPrecisions().add(precision);
      this.getAllRecalls().add(recall);
      this.getAllFScores().add(fScore);
    }
    try {
      printQueryStats(precision, recall, fScore, itemAveragePrecision, this.getItemType());
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
  protected void printQueryStats(double precision, double recall, double fScore,
          double ap, String type) throws IOException {
    getWriter().write(String.format("%s precision: %f\n", type, precision));
    getWriter().write(String.format("%s recall: %f\n", type, recall));
    getWriter().write(String.format("%s f score: %f\n", type, fScore));
    getWriter().write(String.format("%s average precision: %f\n\n", type, ap));
    getWriter().flush();
  }

  public double getPrecision(List<Object> hypotheses, List<Object> gold) {
    if (hypotheses.size() == 0) {
      return 0;
    }
    return (double) getNumTruePositives(hypotheses, gold) / (hypotheses.size());
  }

  /**
   * Return the recall given a list of hypotheses and gold standard list.
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
   * Return the true positives by getting intersection between the list of hypotheses and gold
   * standard list.
   * 
   * @param hypothesis
   * @param gold
   * @return
   */
  public int getNumTruePositives(Collection<?> hypothesis, Collection<?> gold) {
    return CollectionUtils.intersection(new HashSet<Object>(hypothesis), new HashSet<Object>(gold))
            .size();
  }
  
  /**
   * Calculate average precision for the given list of hypothesis items given the list of golden
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

  public ArrayList<Double> getAllPrecisions() {
    return allPrecisions;
  }

  public void setAllPrecisions(ArrayList<Double> allPrecisions) {
    this.allPrecisions = allPrecisions;
  }

  public ArrayList<Double> getAllRecalls() {
    return allRecalls;
  }

  public void setAllRecalls(ArrayList<Double> allRecalls) {
    this.allRecalls = allRecalls;
  }

  public ArrayList<Double> getAllFScores() {
    return allFScores;
  }

  public void setAllFScores(ArrayList<Double> allFScores) {
    this.allFScores = allFScores;
  }

  public FileWriter getExactAnswerWriter() {
    return exactAnswerWriter;
  }

  public void setExactAnswerWriter(FileWriter exactAnswerWriter) {
    this.exactAnswerWriter = exactAnswerWriter;
  }
}
