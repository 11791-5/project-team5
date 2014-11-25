package edu.cmu.lti.oaqa.evaluation;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import json.gson.Question;
import json.gson.TestListQuestion;
import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.stanford.nlp.util.CollectionUtils;

public class EvaluatedExactAnswer extends EvaluatedItem {

  public EvaluatedExactAnswer(FileWriter writer) {
    super(writer);
    super.setItemType("exact answer");
    super.setItemTypeId(edu.cmu.lti.oaqa.type.answer.Answer.type);
  }

  @Override
  public List<Object> getEvaluatedItemsAsList(List<Object> itemObjects) {
    List<Object> answers = new ArrayList<Object>();
    for(Object answer: itemObjects) {
      List<String> tokenListForAnswer = new ArrayList<String>();
      tokenListForAnswer.add(((edu.cmu.lti.oaqa.type.answer.Answer) answer).getText());
      answers.add(tokenListForAnswer);
    }
    return answers;
  }

  @Override
  public List<Object> getGoldStandardItems(String questionId) {
    ArrayList<Object> goldStandardAnswers = new ArrayList<Object>();
    TestListQuestion currentQuestion = GoldStandardSingleton.getInstance().getGoldStandardAnswer()
            .get(questionId);
      goldStandardAnswers.addAll((currentQuestion).getExactAnswer());
    return goldStandardAnswers;
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
    int numTruePositives = 0;
    for(Object item: hypothesis) {
      String itemString = (String)item;
      for(Object goldObj: gold) {
        List<String> syns = (List<String>)goldObj;
        if(syns.contains(itemString)) {
          numTruePositives++;
        }
      }
    }
    return numTruePositives;
  }

}