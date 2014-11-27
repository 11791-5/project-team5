package edu.cmu.lti.oaqa.annotators;

import java.io.File;
import java.io.IOException;

import com.aliasi.chunk.ConfidenceChunker;
import com.aliasi.util.AbstractExternalizable;

/**
 * @author niloygupta
 * Singleton class which loads the HMM Trained Model during initialization
 */
public class NBestGeneChunker {

  private String trainedGeneERModel; 
  private static NBestGeneChunker geneChunker;
  private ConfidenceChunker chunker;
  
  private NBestGeneChunker(){}

  public static NBestGeneChunker getInstance()
  {
    if (geneChunker == null)
      geneChunker = new NBestGeneChunker();

    return geneChunker;
  }
 
  /**
   * @return Returns existing chunker. If chunker is null initializes it.
   */
  public ConfidenceChunker getChunker()
  {
    if(chunker==null)
      intializeChunker();
    return chunker;
  }
  
  /**
   * @param ERModelFile File path to the HMM Model
   */
  public void setTrainedGeneERModel(ConfidenceChunker ERModelFile)
  {
    chunker = ERModelFile;
  }
  
  /**
   * Initializes the HMM Model for LingPipe
   */
  public void intializeChunker()
  {
    File modelFile = new File(trainedGeneERModel);
    try {
      chunker = (ConfidenceChunker) AbstractExternalizable.readObject(modelFile);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public void setModelFile(String modelFile) {
    trainedGeneERModel = modelFile;
    
  }
}
