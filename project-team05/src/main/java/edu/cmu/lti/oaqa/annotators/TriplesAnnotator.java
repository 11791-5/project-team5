package edu.cmu.lti.oaqa.annotators;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import util.GoPubMedServiceSingleton;
import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;

public class TriplesAnnotator  extends JCasAnnotator_ImplBase
{

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    edu.cmu.lti.oaqa.type.retrieval.Document doc; 
    FSIterator<Annotation> questions = aJCas.getAnnotationIndex(ExpandedQuestion.type).iterator();
    while (questions.hasNext()) 
    {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();
      String questionText = question.getText();
      if("YES_NO".equals(question.getQuestionType()))
      {
        LinkedLifeDataServiceResponse.Result linkedLifeDataResult = GoPubMedServiceSingleton.getService().getTriples(questionText);
        if(linkedLifeDataResult != null)
        {
          TripleSearchResult tripleSearchResult;
          int rank = 0;
          for (LinkedLifeDataServiceResponse.Entity entity : linkedLifeDataResult.getEntities()) {
            for (LinkedLifeDataServiceResponse.Relation relation : entity.getRelations()) {
              tripleSearchResult = new TripleSearchResult(aJCas);
              tripleSearchResult.setRank(rank++);
              Triple triple = new Triple(aJCas);              
              triple.setObject(relation.getObj());
              triple.setSubject(relation.getSubj());
              triple.setPredicate(relation.getPred());
              tripleSearchResult.setTriple(triple);
              tripleSearchResult.addToIndexes();
            }
          }
        }
      }
    }
  }
}