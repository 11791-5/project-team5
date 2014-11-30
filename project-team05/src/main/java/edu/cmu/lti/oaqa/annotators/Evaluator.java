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
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;

import edu.cmu.lti.oaqa.evaluation.EvaluatedConcept;
import edu.cmu.lti.oaqa.evaluation.EvaluatedDocument;
import edu.cmu.lti.oaqa.evaluation.EvaluatedExactAnswer;
import edu.cmu.lti.oaqa.evaluation.EvaluatedItem;
import edu.cmu.lti.oaqa.evaluation.EvaluatedSnippet;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;

/**
 * This annotator is used to evaluate the system's output for each question and to accumulate
 * results across questions.
 * 
 * @author Maya Tydykov
 *
 */
public class Evaluator extends JCasAnnotator_ImplBase {

  private EvaluatedItem evaluatedConcept;

  private EvaluatedItem evaluatedDocument;

  private EvaluatedItem evaluatedSnippet;

  private EvaluatedItem evaluatedExactAnswer;

  private FileWriter evaluationWriter;

  private final static String EVAL_FILE_PARAM = "EvaluationFileName";

  File evaluation = new File("evaluation.txt");

  /**
   * Initialize the evaluation file used to store all results and the individual evaluators used in
   * the pipeline.
   */
  public void initialize(UimaContext u) throws ResourceInitializationException {
    super.initialize(u);
    try {
      evaluationWriter = new FileWriter(new File(
              ((String) u.getConfigParameterValue(EVAL_FILE_PARAM))));
      evaluatedConcept = new EvaluatedConcept(evaluationWriter);
      evaluatedDocument = new EvaluatedDocument(evaluationWriter);
      evaluatedSnippet = new EvaluatedSnippet(evaluationWriter);
      evaluatedExactAnswer = new EvaluatedExactAnswer(evaluationWriter);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * For a given question (there should only be 1 at any given time), evaluate all system output.
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> questions = aJCas.getAnnotationIndex(ExpandedQuestion.type).iterator();
    while (questions.hasNext()) {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();
      if (question.getQuestionType().equals("LIST")) {
        String questionid = question.getId();
        // write the query ID and then proceed to calculate
        // and store results for each type of metric
        try {
          evaluationWriter.write(String.format("\n\nQuery id: %s\n", questionid));
        } catch (IOException e2) {
          // TODO Auto-generated catch block
          e2.printStackTrace();
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
        try {
          this.evaluatedExactAnswer.calculateItemMetrics(aJCas, questionid);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

    }
  }

  /**
   * Calculate the arithmetic average of a list of values.
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
   * Calculate the geometric average of a list of values.
   * 
   * @param vals
   * @return
   */
  public double calculateGeomAvg(ArrayList<Double> vals) {
    if (vals.size() == 0) {
      return 0;
    }
    double result = 1;
    double epsilon = 0.0000000001;
    for (Double val : vals) {
      result *= (val + epsilon);
    }
    return Math.pow(result, 1 / (double) vals.size());
  }

  /**
   * Calculate and print the mean average precision and geometric mean average precision for the
   * queries processed in the collection.
   */
  @Override
  public void collectionProcessComplete() {
    try {
      calcAndPrintAverages("concept", evaluatedConcept.getAllPrecisions(),
              evaluatedConcept.getAllRecalls(), evaluatedConcept.getAllFScores(),
              evaluatedConcept.getAveragePrecision());
      calcAndPrintAverages("document", evaluatedDocument.getAllPrecisions(),
              evaluatedDocument.getAllRecalls(), evaluatedDocument.getAllFScores(),
              evaluatedDocument.getAveragePrecision());
      calcAndPrintAverages("snippet", evaluatedSnippet.getAllPrecisions(),
              evaluatedSnippet.getAllRecalls(), evaluatedSnippet.getAllFScores(),
              evaluatedSnippet.getAveragePrecision());
      calcAndPrintAverages("exact answer", evaluatedExactAnswer.getAllPrecisions(),
              evaluatedExactAnswer.getAllRecalls(), evaluatedExactAnswer.getAllFScores(),
              evaluatedExactAnswer.getAveragePrecision());
      evaluationWriter.close();
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  private void calcAndPrintAverages(String type, ArrayList<Double> averagePrecisions,
          ArrayList<Double> averageRecalls, ArrayList<Double> averageFScores, ArrayList<Double> aPs)
          throws IOException {
    double meanOfPrecisions = calcArithmeticAvg(averagePrecisions);
    evaluationWriter.write((String.format("%s Mean of Precisions: %f\n", type, meanOfPrecisions)));
    double meanOfRecalls = calcArithmeticAvg(averageRecalls);
    evaluationWriter.write((String.format("%s Mean of Recalls: %f\n", type, meanOfRecalls)));
    double meanOfFScores = calcArithmeticAvg(averageFScores);
    evaluationWriter.write((String.format("%s Mean of FScores: %f\n", type, meanOfFScores)));

    double typeMap = calcArithmeticAvg(aPs);
    double typeGmap = calculateGeomAvg(aPs);
    evaluationWriter.write((String.format("%s MAP: %f\n", type, typeMap)));
    evaluationWriter.write((String.format("%s GMAP: %f\n", type, typeGmap)));
  }

}
