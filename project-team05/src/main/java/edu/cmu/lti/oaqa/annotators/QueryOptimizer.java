package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import util.Utils;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.SynSet;
import edu.cmu.lti.oaqa.type.retrieval.Synonym;

public class QueryOptimizer 
{
  
  public static List<String> optimizeQuery(JCas aJCas, ExpandedQuestion question)
  {
    List<String> conceptQuery = new ArrayList<String>();
    FSIterator<TOP> conceptIterator = aJCas.getJFSIndexRepository().getAllIndexedFS(ConceptSearchResult.type);
    int threshold = 0;
    while (conceptIterator.hasNext() && threshold<5) {
      ConceptSearchResult concept = (ConceptSearchResult) conceptIterator.next(); 
      conceptQuery.add(concept.getConcept().getName());
      threshold++;
    }
    return conceptQuery;
  }

}
