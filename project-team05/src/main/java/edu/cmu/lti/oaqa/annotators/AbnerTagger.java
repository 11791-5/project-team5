package edu.cmu.lti.oaqa.annotators;

import abner.Tagger;

/**
 * @author niloygupta
 * Singleton class which loads the ABNER trained model during initialization
 */
public class AbnerTagger {

  private static AbnerTagger abnerTagger;
  private Tagger geneTagger;
  public static final int BIOCREATIVE  = 1;
  public static final int NLPBA = 0;
  
  private AbnerTagger(){}

  public static AbnerTagger getInstance()
  {
    if (abnerTagger == null)
      abnerTagger = new AbnerTagger();

    return abnerTagger;
  }
 
  /**
   * @return Returns existing chunker. If chunker is null initializes it.
   */
  public Tagger getTagger()
  {
    if(geneTagger==null)
      intializeGeneTagger();
    return geneTagger;
  }
  
  /**
   * Initializes the HMM Model for LingPipe
   */
  public void intializeGeneTagger()
  {
      geneTagger = new Tagger(AbnerTagger.BIOCREATIVE);
  }
}
