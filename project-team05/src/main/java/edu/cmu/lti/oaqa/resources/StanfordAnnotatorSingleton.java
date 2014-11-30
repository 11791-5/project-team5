package edu.cmu.lti.oaqa.resources;
import java.util.Properties;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

/**
 * Singleton for the StanfordAnnotator 
 *
 */
public class StanfordAnnotatorSingleton {
  StanfordCoreNLP pipeline;

  public StanfordCoreNLP getPipeline() {
    return pipeline;
  }

  public void setPipeline(StanfordCoreNLP pipeline) {
    this.pipeline = pipeline;
  }

  private static StanfordAnnotatorSingleton singleton = null;

  private StanfordAnnotatorSingleton() {
    Properties props = new Properties();
    props.put("annotators", "tokenize, ssplit, pos");
    pipeline = new StanfordCoreNLP(props);
  }

  public static synchronized StanfordAnnotatorSingleton getInstance() {
    if (singleton == null)
      singleton = new StanfordAnnotatorSingleton();
    return singleton;
  }
}
