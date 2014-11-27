package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;
import util.Utils;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.retrieval.SynSet;
import edu.cmu.lti.oaqa.type.retrieval.Synonym;

public class QueryOptimizer {
  public static String optimizeQuery(ExpandedQuestion question) {
    ArrayList<SynSet> as = Utils.fromFSListToCollection(question.getSynSets(), SynSet.class);
    edu.cmu.lti.oaqa.type.retrieval.SynSet synset;
    ArrayList<String> synonymList = new ArrayList<String>();
    // Form a query as (term1 OR its synonyms)AND(term2 OR its synonyms)etc
    String query = "(";
    for (SynSet syns : as) {
      ArrayList<Synonym> synonyms = Utils.fromFSListToCollection(syns.getSynonyms(), Synonym.class);
      for (Synonym synonym : synonyms)
        // OR each term with its synonyms
        query += synonym.getSynonym() + " OR ";
      // AND each group of synonyms
      query += syns.getOriginalToken() + " )AND( ";
    }
    query = query.substring(0, query.length() - 5);
    return query;
  }
}