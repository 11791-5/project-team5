package edu.cmu.lti.oaqa.evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import json.gson.TestListQuestion;
import util.Utils;
import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;
import edu.stanford.nlp.util.CollectionUtils;

/**
 * Object for performing evaluation over all exact answers.
 * @author root
 *
 */

public class EvaluatedExactAnswer extends EvaluatedItem {

  /**
   * Set evaluated exact answer
   * @param writer
   * @throws IOException
   */
  public EvaluatedExactAnswer(FileWriter writer) throws IOException {
    super(writer);
    super.setItemType("Exact Answer");
    super.setItemTypeId(edu.cmu.lti.oaqa.type.answer.Answer.type);
  }

  /**
   * Get list of evaluated items
   */
  @Override
  public List<Object> getEvaluatedItemsAsList(List<Object> itemObjects) {
    List<Object> answers = new ArrayList<Object>();
    for(Object answer: itemObjects) {
      List<String> tokenListForAnswer = new ArrayList<String>();
      tokenListForAnswer.add(((edu.cmu.lti.oaqa.type.answer.Answer) answer).getText());
      tokenListForAnswer.addAll(Utils.createListFromStringList(((edu.cmu.lti.oaqa.type.answer.Answer) answer).getVariants()));
      // add list to answers
      answers.add(tokenListForAnswer);
    }
    return answers;
  }

  /**
   * Get list of Gold standard answers
   */
  @Override
  public List<Object> getGoldStandardItems(String questionId) {
    ArrayList<Object> goldStandardAnswers = new ArrayList<Object>();
    // get the current question by questionID
    TestListQuestion currentQuestion = GoldStandardSingleton.getInstance().getGoldStandardAnswer()
            .get(questionId);
    // add it to the gold standard answers
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
  @Override
  public int getNumTruePositives(Collection<?> hypothesis, Collection<?> gold) {
    int numTruePositives = 0;
    // for each list of synonymous exact answer hypotheses
    for(Object item: hypothesis) {
      List<String> synonyms = (List<String>) item;
      // for each list of synonymous exact gold standard answers
      for(Object goldObj: gold) {
        List<String> synonymsForGold = (List<String>) goldObj;

        // if at least one common element between hypothesis and gold standard
        // then increment the number of true positives
        if(!CollectionUtils.intersection(new HashSet<Object>(synonyms), new HashSet<Object>(synonymsForGold)).isEmpty()) {
          numTruePositives++;
        }
      }
    }
    return numTruePositives;
  }

}
