package edu.cmu.lti.oaqa.annotators;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.cmu.lti.oaqa.evaluation.EvaluatedConcept;
import edu.cmu.lti.oaqa.evaluation.EvaluatedDocument;
import edu.cmu.lti.oaqa.evaluation.EvaluatedItem;
import edu.cmu.lti.oaqa.evaluation.EvaluatedSnippet;
import edu.cmu.lti.oaqa.evaluation.EvaluatedTriple;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;

public class Evaluator extends JCasAnnotator_ImplBase {

  private EvaluatedItem evaluatedTriple;
  private EvaluatedItem evaluatedConcept;
  private EvaluatedItem evaluatedDocument;
  private EvaluatedItem evaluatedSnippet;
  private FileWriter evaluationWriter;


  File evaluation = new File("evaluation.txt");

  
  public void initialize(UimaContext u) {
    try {
      evaluationWriter = new FileWriter(evaluation);
      evaluatedTriple = new EvaluatedTriple(evaluationWriter);
      evaluatedConcept = new EvaluatedConcept(evaluationWriter);
      evaluatedDocument = new EvaluatedDocument(evaluationWriter);
      evaluatedSnippet = new EvaluatedSnippet(evaluationWriter);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> questions = aJCas.getAnnotationIndex(ExpandedQuestion.type).iterator();
    while (questions.hasNext()) {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();
      String questionid = question.getId();
      try {
        evaluatedTriple.calculateItemMetrics(aJCas, questionid);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      try {
        evaluatedConcept.calculateItemMetrics(aJCas, questionid);
      } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      try {
        evaluatedDocument.calculateItemMetrics(aJCas, questionid);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      try {
        evaluatedSnippet.calculateItemMetrics(aJCas, questionid);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }


  /**
   * Convenience method to calculate the arithmetic average of a list of values.
   * 
   * @param vals
   * @return
   */
  public double calcArithmeticAvg(ArrayList<Double> vals) {
    if (vals.size() == 0) {
      return 0;
    }
    double result = 0;

    for (Double val : vals) {
      result += val;
    }
    return result / vals.size();
  }

  /**
   * Convenience method to calculate the geometric average of a list of values.
   * 
   * @param vals
   * @return
   */
  public double calculateGeomAvg(ArrayList<Double> vals) {
    if (vals.size() == 0) {
      return 0;
    }
    double result = 1;
    double epsilon = 0.01;
    for (Double val : vals) {
      result *= (val + epsilon);
    }
    return Math.pow(result, 1/(double)vals.size());
  }

  /**
   * Calculate and print the mean average precision and geometric mean average precision for the
   * queries processed in the collection.
   */
  public void collectionProcessComplete() {
    calcAndPrintFinalStatsForType("concept", evaluatedConcept.getAveragePrecision());
    calcAndPrintFinalStatsForType("document", evaluatedDocument.getAveragePrecision());
    calcAndPrintFinalStatsForType("triple", evaluatedTriple.getAveragePrecision());
    calcAndPrintFinalStatsForType("snippet", evaluatedSnippet.getAveragePrecision());

    try {
      evaluationWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void calcAndPrintFinalStatsForType(String type, ArrayList<Double> typeVals) {
    double typeMap = calcArithmeticAvg(typeVals);
    double typeGmap = calculateGeomAvg(typeVals);
    try {
      printFinalStats(typeMap, typeGmap, type);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  /**
   * Prints final stats for the given type.
   * 
   * @param map
   * @param gmap
   * @param type
   * @throws IOException
   */
  private void printFinalStats(double map, double gmap, String type) throws IOException {
    evaluationWriter.write((String.format("%s MAP: %f\n", type, map)));
    evaluationWriter.write((String.format("%s GMAP: %f\n", type, gmap)));
  }

}
