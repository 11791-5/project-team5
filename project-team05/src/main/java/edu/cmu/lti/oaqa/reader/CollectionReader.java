package edu.cmu.lti.oaqa.reader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import json.JsonCollectionReaderHelper;
import json.gson.Question;
import json.gson.QuestionType;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;

import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;

public class CollectionReader extends CollectionReader_ImplBase {

  /**
   * Name of configuration parameter that must be set to the path of the input file.
   */
  public static final String PARAM_INPUT_FILE = "InputFile";
  public static final String PARAM_GOLD_STANDARD_FILE = "GoldStdFile";
  public static final String PARAM_STOPWORD_LIST = "StopWordList";
  public static final String PARAM_STOPSYNONYM_LIST = "StopSynonymList";
  public static final String PARAM_MODEL_FILE = "LingpipeModelFile";
  
//  String filePath = "/BioASQ-SampleData1B.json";
  //String filePath = "src/main/resources/question.json";
  private File inputFile;
  private File goldStandard;
  List<Question> inputs;
  List<Question> stdAnswers;
  JsonCollectionReaderHelper jsonReader;
  int currentIndex = 0;
  int numberOfQuestions = 0;

  public static String STOP_WORD_FILE_NAME;
  public static String STOP_SYNONYM_FILE_NAME;
  
  public void initialize() throws ResourceInitializationException {
 
    inputFile = new File(((String) getConfigParameterValue(PARAM_INPUT_FILE)).trim());
    if (!inputFile.exists()) {
      throw new ResourceInitializationException("File Not Found",
              new Object[] { PARAM_INPUT_FILE, this.getMetaData().getName(), inputFile.getPath() });
    }

    jsonReader = new JsonCollectionReaderHelper();
    inputs = jsonReader.getQuestionsList("/"+inputFile.getName());
    
    goldStandard = new File(((String) getConfigParameterValue(PARAM_GOLD_STANDARD_FILE)).trim());
    stdAnswers = jsonReader.getQuestionsList("/"+goldStandard.getName());
    try {
      // set gold standard answers
      GoldStandardSingleton.getInstance().setGoldStandardAnswer(stdAnswers);
    } catch (Exception e) {
      e.printStackTrace();
    } 
    // get the number of questions
    numberOfQuestions = inputs.size();
    // list of stop words
    STOP_WORD_FILE_NAME = (String) getConfigParameterValue(PARAM_STOPWORD_LIST);
    // list of synonyms
    STOP_SYNONYM_FILE_NAME = (String) getConfigParameterValue(PARAM_STOPSYNONYM_LIST);
  }  
  @Override
  public void getNext(CAS aCAS) throws IOException, CollectionException {

    if(hasNext() && !QuestionType.list.equals(inputs.get(currentIndex).getType()))
      currentIndex ++;
    if(!hasNext())
      return;
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }
    // put question from data file in CAS
      jsonReader.addQuestionToIndex(inputs.get(currentIndex++), CollectionReader.PARAM_INPUT_FILE, jcas);
  }

  @Override
  public void close() throws IOException {
  
  }

  @Override
  public Progress[] getProgress() {
    return null;
  }

  @Override
  public boolean hasNext() throws IOException, CollectionException {
    return (currentIndex < numberOfQuestions);
  }


}

