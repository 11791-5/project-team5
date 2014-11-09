import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

/**
 * 
 * Simple AE to test the Collection Reader
 * @author larbi
 *
 */
public class TestCR extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    System.out.println("Processing cas");
    String doctext = aJCas.getDocumentText();
    System.out.println(doctext);
  }

}
