package edu.cmu.lti.oaqa.consumers;

import edu.cmu.lti.oaqa.type.input.GoldStandardAnswer;

public class GoldStandardSingleton 
{
  private static GoldStandardSingleton goldStandardSingleton;
  private GoldStandardAnswer goldStandardAnswer;

  private GoldStandardSingleton()
  {
    goldStandardAnswer = new GoldStandardAnswer();
  }
  public static synchronized GoldStandardSingleton getInstance()
  {
    if (goldStandardSingleton == null)
      goldStandardSingleton = new GoldStandardSingleton();
    return goldStandardSingleton;
  }
  
  public GoldStandardAnswer getGoldStandardAnswer() {
    return goldStandardAnswer;
  }

  public void setGoldStandardAnswer(GoldStandardAnswer goldStandardAnswer) {
    this.goldStandardAnswer = goldStandardAnswer;
  }
}
