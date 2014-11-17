package edu.cmu.lti.oaqa.annotators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.JCasAnnotator_ImplBase;

import de.tudarmstadt.ukp.dkpro.core.stopwordremover.StopWordRemover;
import util.StanfordLemmatizer;
import util.Utils;
import edu.cmu.lti.oaqa.bio.umls_wrapper.TermRelationship;
import edu.cmu.lti.oaqa.bio.umls_wrapper.UmlsTermsDAO;
import edu.cmu.lti.oaqa.bio.umls_wrapper.UmlsTest;
import edu.cmu.lti.oaqa.resources.StanfordAnnotatorSingleton;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.SynSet;
import edu.cmu.lti.oaqa.type.retrieval.Synonym;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class QueryExpander extends JCasAnnotator_ImplBase {

  UmlsTermsDAO test = null;

  HashSet<String> stopWordSet = new HashSet<String>();

  public static final String PARAM_STOPWORD_LIST = "StopWordList";

  private File stopWordList;

  /**
   * 
   */
  public void initialize(UimaContext u) {
    stopWordList = new File((String) u.getConfigParameterValue(PARAM_STOPWORD_LIST));

    Scanner reader = null;
    try {
      reader = new Scanner(stopWordList);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    while (reader.hasNext()) {
      String line = reader.nextLine();
      if (!line.isEmpty()) {
        stopWordSet.add(line.trim());
      }
    }
    test = new UmlsTermsDAO();
  }

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> questions = jcas.getAnnotationIndex(Question.type).iterator();
    while (questions.hasNext()) {
      Question question = (Question) questions.next();
      ExpandedQuestion expandedQuestion = new ExpandedQuestion(jcas);
      expandedQuestion.setText(question.getText());
      expandedQuestion.setQuestionType(question.getQuestionType());
      expandedQuestion.setId(question.getId());
      // System.out.println(question.getQuestionType() + " "+question.getText());
      String questionText = StanfordLemmatizer.stemText(question.getText());
      edu.stanford.nlp.pipeline.Annotation ann = new edu.stanford.nlp.pipeline.Annotation(
              questionText);
      StanfordAnnotatorSingleton.getInstance().getPipeline().annotate(ann);
      ArrayList<SynSet> synSets = new ArrayList<SynSet>();
      for (CoreLabel term : ann.get(TokensAnnotation.class)) {
        SynSet sSet = new SynSet(jcas);
        sSet.setOriginalToken(term.originalText());
        HashMap<String, Double> synToConfidence = new HashMap<String, Double>();
        if (!stopWordSet.contains(term.originalText())) {
          try {
            HashSet<TermRelationship> rels = new HashSet<TermRelationship>(
                    test.getTermSynonyms(term.originalText()));
            for (TermRelationship rel : rels) {
              if (!synToConfidence.containsKey(rel.getToTerm())) {
                synToConfidence.put(rel.getToTerm(), rel.getConfidence());
              } else {
                if (rel.getConfidence() > synToConfidence.get(rel.getToTerm())) {
                  synToConfidence.put(rel.getToTerm(), rel.getConfidence());
                }
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        HashSet<Synonym> synList = new HashSet<Synonym>();
        for (String synonym : synToConfidence.keySet()) {
          Synonym syn = new Synonym(jcas);
          syn.setSynonym(synonym);
          syn.setConfidence(synToConfidence.get(synonym));
          syn.setSource("UMLS");
          synList.add(syn);
        }
        sSet.setSynonyms(Utils.fromCollectionToFSList(jcas, synList));
        synSets.add(sSet);
      }
      expandedQuestion.setSynSets(Utils.fromCollectionToFSList(jcas, synSets));
      expandedQuestion.addToIndexes();
    }
  }
}
