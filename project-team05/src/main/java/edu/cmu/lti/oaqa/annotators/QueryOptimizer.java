package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;

import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;

public class QueryOptimizer 
{
  
  public static List<String> optimizeQuery(JCas aJCas)
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
