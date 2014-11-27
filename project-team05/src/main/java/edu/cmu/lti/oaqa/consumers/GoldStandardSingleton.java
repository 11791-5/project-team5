package edu.cmu.lti.oaqa.consumers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.oaqa.bio.umls_wrapper.TermRelationship;
import edu.cmu.lti.oaqa.resources.UmlsSingleton;
import json.gson.Question;

/**
 * Gold Standard Singleton
 * 
 */
public class GoldStandardSingleton {
  private static GoldStandardSingleton goldStandardSingleton;

  // Create a Map for the Gold Standard answers
  private Map<String, json.gson.TestListQuestion> goldStandardAnswer;

  /**
   * Constructor for gold standard singleton
   */
  private GoldStandardSingleton() {
    goldStandardAnswer = new HashMap<String, json.gson.TestListQuestion>();
  }

  /**
   * Get instance of gold standard singleton
   * @return
   */
  public static synchronized GoldStandardSingleton getInstance() {
    // if singleton doesn't exist, create it
    if (goldStandardSingleton == null)
      goldStandardSingleton = new GoldStandardSingleton();
    return goldStandardSingleton;
  }

  /**
   * Get gold standard answer
   * @return
   */
  public Map<String, json.gson.TestListQuestion> getGoldStandardAnswer() {
    return goldStandardAnswer;
  }

  /**
   * Set Gold standard answer
   * @param stdAnswers
   * @throws Exception
   */
  public void setGoldStandardAnswer(List<Question> stdAnswers) throws Exception {
    Pattern p = Pattern.compile("[0-9]+");
    for (Question stdAnswer : stdAnswers) {
      if (stdAnswer instanceof json.gson.TestListQuestion) {
        json.gson.TestListQuestion answer = (json.gson.TestListQuestion) stdAnswer;

        if (!answer.getExactAnswer().isEmpty()) {
          List<List<String>> replacementAnswerList = new ArrayList<List<String>>();
          for (List<String> synList : answer.getExactAnswer()) {
            ArrayList<String> replacementTermList = new ArrayList<String>();
            replacementTermList.addAll(synList);
            for (String term : synList) {
              HashSet<TermRelationship> rels = new HashSet<TermRelationship>(UmlsSingleton
                      .getInstance().getUmlsService().getTermSynonyms(term));
              HashSet<String> finalSet = new HashSet<String>();
              for (TermRelationship tr : rels) {
                finalSet.add(tr.getToTerm().toLowerCase());
              }
              replacementTermList.addAll(finalSet);
            }
            replacementAnswerList.add(replacementTermList);
          }
          answer.setExactAnswer(replacementAnswerList);
        }
        addAnswerToGoldStandard(p, answer);
      }

    }
  }

  /**
   * Add answer to gold standard
   * 
   * @param p
   * @param answer
   */
  private void addAnswerToGoldStandard(Pattern p, json.gson.TestListQuestion answer) {
    for (int i = 0; i < answer.getDocuments().size(); i++) {
      String document = answer.getDocuments().get(i);
      Matcher matcher = p.matcher(document);
      if (matcher.find())
        answer.getDocuments().set(i, matcher.group());
    }
    this.goldStandardAnswer.put(answer.getId(), answer);
  }
}
