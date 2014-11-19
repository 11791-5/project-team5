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

public class EvaluatedSnippet extends EvaluatedItem {

  public EvaluatedSnippet(FileWriter writer) {
    super(writer);
    super.setItemType("snippet");
    super.setItemTypeId(SnippetSearchResult.type);
  }

  @Override
  public List<Object> getEvaluatedItemsAsList(List<Object> itemObjects) {
    // TODO Auto-generated method stub
    return null;
  }
  
  private static void extractDocumentOffsetPairs(List<Object> goldArticleOffsetPairs, String docText, int offsetBegin, String docId) {
    char[] passageChars = docText.toCharArray();
    for(int i = 0; i < passageChars.length; i++) {
      Pair<String, Integer> pair = new ImmutablePair<String, Integer>(docId, offsetBegin+i);
      goldArticleOffsetPairs.add(pair);
    }
  }
  
  @Override
  public void calculateItemMetrics(JCas aJCas, String queryId) {
    List<Object> goldItems = getGoldStandardItems(queryId);
    // calcualte metrics for triples
    List<Object> passageItems = Utils.extractUIMATypeAsList(SnippetSearchResult.type, aJCas);
    List<ArrayList<Object>> retrievedArticleOffsetPairs = new ArrayList<ArrayList<Object>>();
    for(Object currentSnippet: passageItems) {
      Passage p = ((SnippetSearchResult)currentSnippet).getSnippets(); 
      ArrayList<Object> articleOffsetPairs = new ArrayList<Object>();
      extractDocumentOffsetPairs(articleOffsetPairs, p.getText(), p.getOffsetInBeginSection(), p.getDocId());
      retrievedArticleOffsetPairs.add(articleOffsetPairs);
    }

    List<Object> goldArticleOffsetPairs = new ArrayList<Object>();
    for(Object s: goldItems) {
      Snippet snippet = (Snippet)s;
      extractDocumentOffsetPairs(goldArticleOffsetPairs, snippet.getText(), snippet.getOffsetInBeginSection(), snippet.getDocument());
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

    super.getAveragePrecision().add(passageAP);
    
    try {
      super.printQueryStats(queryId, passagePrecision, passageRecall, passageF, passageAP, "snippet");
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

  @Override
  public List<Object> getGoldStandardItems(String questionId) {
    return new ArrayList<Object>(GoldStandardSingleton.getInstance().getGoldStandardAnswer()
            .get(questionId).getSnippets());
  }

}
