package edu.cmu.lti.oaqa.consumers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import json.gson.Question;

public class GoldStandardSingleton 
{
  private static GoldStandardSingleton goldStandardSingleton;
  private Map<String,Question> goldStandardAnswer;

  private GoldStandardSingleton()
  {
    goldStandardAnswer = new HashMap<String,Question>();
  }
  public static synchronized GoldStandardSingleton getInstance()
  {
    if (goldStandardSingleton == null)
      goldStandardSingleton = new GoldStandardSingleton();
    return goldStandardSingleton;
  }
  
  public Map<String,Question> getGoldStandardAnswer() {
    return goldStandardAnswer;
  }

  public void setGoldStandardAnswer(List<Question> stdAnswers) 
  {
    for(Question answer:stdAnswers)
    {
      this.goldStandardAnswer.put(answer.getId(), answer);
    }
  }
}
