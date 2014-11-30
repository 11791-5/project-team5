package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import util.GoPubMedServiceSingleton;
import util.Utils;
import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.SynSet;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;

public class TriplesAnnotator  extends JCasAnnotator_ImplBase
{

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    FSIterator<Annotation> questions = aJCas.getAnnotationIndex(ExpandedQuestion.type).iterator();
    while (questions.hasNext()) 
    {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();
      ArrayList<SynSet> synSets = Utils.fromFSListToCollection(question.getSynSets(), SynSet.class);
      String questionText = Utils.getQueryTokens(synSets);
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