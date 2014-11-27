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
    
   /* ArrayList<SynSet> as = Utils.fromFSListToCollection(question.getSynSets(), SynSet.class);
    edu.cmu.lti.oaqa.type.retrieval.SynSet synset;
    ArrayList<String> queryTokens = new ArrayList<String>();
    List<String> conceptQuery = new ArrayList<String>()
    String query = "(";
    String optimizedQuery = ""; 
    for (SynSet syns : as) 
    {
      ArrayList<Synonym> synonyms = Utils.fromFSListToCollection(syns.getSynonyms(),Synonym.class);
      /*for (Synonym synonym : synonyms)
        query+= synonym.getSynonym() + " OR ";
      query += syns.getOriginalToken() + " )AND( ";
      queryTokens.add(syns.getOriginalToken());
    }*/
    //query = query.substring(0, query.length()-5);    
   // return optimizedQuery;
    List<String> conceptQuery = new ArrayList<String>();
    FSIterator<TOP> conceptIterator = aJCas.getJFSIndexRepository().getAllIndexedFS(ConceptSearchResult.type);
    int threshold = 0;
    while (conceptIterator.hasNext() && threshold<5) {
      ConceptSearchResult concept = (ConceptSearchResult) conceptIterator.next(); 
      conceptQuery.add(concept.getConcept().getName());
      threshold++;
    }
    return conceptQuery;
    //return null;
  }

}
