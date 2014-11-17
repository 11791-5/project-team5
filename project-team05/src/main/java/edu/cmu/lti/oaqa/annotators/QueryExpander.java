package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.component.JCasAnnotator_ImplBase;

import util.StanfordLemmatizer;
import util.Utils;
import edu.cmu.lti.oaqa.bio.umls_wrapper.TermRelationship;
import edu.cmu.lti.oaqa.resources.StanfordAnnotatorSingleton;
import edu.cmu.lti.oaqa.resources.StopWordSingleton;
import edu.cmu.lti.oaqa.resources.UmlsSingleton;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.SynSet;
import edu.cmu.lti.oaqa.type.retrieval.Synonym;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;

public class QueryExpander extends JCasAnnotator_ImplBase {


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
        if (!StopWordSingleton.getInstance().isStopWord(term.originalText())) {
          try {
            HashSet<TermRelationship> rels = new HashSet<TermRelationship>(
                    UmlsSingleton.getInstance().getUmlsService().getTermSynonyms(term.originalText()));
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
