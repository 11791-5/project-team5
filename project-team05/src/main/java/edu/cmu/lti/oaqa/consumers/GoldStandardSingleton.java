package edu.cmu.lti.oaqa.consumers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    Pattern p = Pattern.compile("[0-9]+");
    for(Question answer:stdAnswers)
    {
      for(String document:answer.getDocuments())
      {
        Matcher matcher = p.matcher(document);
        if(matcher.find())
          document = matcher.group();
      }
      this.goldStandardAnswer.put(answer.getId(), answer);
    }
  }
}
