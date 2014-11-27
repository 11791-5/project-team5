package edu.cmu.lti.oaqa.evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import json.gson.Snippet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.jcas.JCas;

import util.Utils;
import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult;
import edu.stanford.nlp.util.CollectionUtils;

/**
 * Object for performing evaluation over all snippets.
 * @author root
 *
 */
public class EvaluatedSnippet extends EvaluatedItem {

  public EvaluatedSnippet(FileWriter writer) {
    super(writer);
    super.setItemType("snippet");
    super.setItemTypeId(SnippetSearchResult.type);
  }
  
  /**
   * Given the list to populate, the document text, the offset begin for the given text, 
   * and the document ID, add to the list pairs of document id - character offset.
   * @param listToPopulate
   * @param docText
   * @param offsetBegin
   * @param docId
   */
  private static void extractDocumentOffsetPairs(List<Object> listToPopulate, String docText, int offsetBegin, String docId) {
    char[] passageChars = docText.toCharArray();
    for(int i = 0; i < passageChars.length; i++) {
      Pair<String, Integer> pair = new ImmutablePair<String, Integer>(docId, offsetBegin+i);
      listToPopulate.add(pair);
    }
  }
  
  /**
   * Calculate all metrics for snippets.
   */
  @Override
  public void calculateItemMetrics(JCas aJCas, String queryId) {
    List<Object> goldItems = getGoldStandardItems(queryId);
    // calculate metrics for triples
    List<Object> passageItems = Utils.extractUIMATypeAsList(SnippetSearchResult.type, aJCas);
    List<ArrayList<Object>> retrievedArticleOffsetPairs = new ArrayList<ArrayList<Object>>();
    // put all predicted snippets into list,
    // where the list contains a list of article-offset pairs, one 
    // such list for each document
    for(Object currentSnippet: passageItems) {
      Passage p = ((SnippetSearchResult)currentSnippet).getSnippets(); 
      ArrayList<Object> articleOffsetPairs = new ArrayList<Object>();
      extractDocumentOffsetPairs(articleOffsetPairs, p.getText(), p.getOffsetInBeginSection(), p.getDocId());
      retrievedArticleOffsetPairs.add(articleOffsetPairs);
    }

    // put all gold standard snippets into a list
    List<Object> goldArticleOffsetPairs = new ArrayList<Object>();
    for(Object s: goldItems) {
      Snippet snippet = (Snippet)s;
      extractDocumentOffsetPairs(goldArticleOffsetPairs, snippet.getText(), snippet.getOffsetInBeginSection(), snippet.getDocument());
    }

    // put all predicted article-offset pairs into a master list as well
    ArrayList<Object> allRecalledArticleOffsetPairsList = new ArrayList<Object>();
    for(Object o: retrievedArticleOffsetPairs) {
      allRecalledArticleOffsetPairsList.addAll(((ArrayList<Object>)o));
    }
    
    double passagePrecision = getPrecision(allRecalledArticleOffsetPairsList, goldArticleOffsetPairs);
    double passageRecall = getRecall(allRecalledArticleOffsetPairsList, goldArticleOffsetPairs);
    double passageF = calcF(passagePrecision, passageRecall);
    double passageAP = calcAPForSnippets(goldArticleOffsetPairs, retrievedArticleOffsetPairs);

    if(!allRecalledArticleOffsetPairsList.isEmpty() || !goldArticleOffsetPairs.isEmpty()) super.getAveragePrecision().add(passageAP);
    
    try {
      super.printQueryStats(passagePrecision, passageRecall, passageF, passageAP, "snippet");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Given a list of lists of article-offset pairs (one such
   * list for each article) and a list of gold standard article-offset pairs,
   * calculate the average precision for snippets.
   * @param goldArticleOffsetPairs
   * @param retrievedArticleOffsetPairs
   * @return
   */
  private double calcAPForSnippets(List<Object> goldArticleOffsetPairs,
          List<ArrayList<Object>> retrievedArticleOffsetPairs) {
    ArrayList<Object> allList = new ArrayList<Object>();
    // also create a flat list of all predicted article-offset pairs
    // (not separated by article)
    for(Object o: retrievedArticleOffsetPairs) {
      allList.addAll(((ArrayList<Object>)o));
    }
    int totalRelItemsInList = 0;
    // look through each snippet
    for(ArrayList<Object> pair:  retrievedArticleOffsetPairs) {
      boolean isRelevant = false;
      // if there is any overlap with the gold standard,
      // the snippet is relevant
      for(Object o: pair) {
        if(goldArticleOffsetPairs.contains(o)) {
          isRelevant = true;
          break;
        }
      }
      // if the snippet was relevant, increment number of relevant items
      if(isRelevant) {
        totalRelItemsInList++;
      }
    }
    
    if (totalRelItemsInList == 0) {
      return 0;
    }
    
    double averagePrecision = 0;
    for (int i = 0; i < retrievedArticleOffsetPairs.size(); i++) {
      // if nonzero overlap between golden snippets and the snippet at current index
      if (!CollectionUtils.intersection(new HashSet<Object>(goldArticleOffsetPairs), new HashSet<Object>(retrievedArticleOffsetPairs.get(i))).isEmpty()) {;
        ArrayList<Object> subList = new ArrayList<Object>();
        // get the precision for the sublist up until snippets of the given rank
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

  @Override
  public List<Object> getGoldStandardItems(String questionId) {
    return new ArrayList<Object>(GoldStandardSingleton.getInstance().getGoldStandardAnswer()
            .get(questionId).getSnippets());
  }

  @Override
  public List<Object> getEvaluatedItemsAsList(List<Object> itemObjects) {
    return null;
  }

}
