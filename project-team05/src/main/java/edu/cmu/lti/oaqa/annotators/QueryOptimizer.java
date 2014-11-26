package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;

import util.StanfordLemmatizer;
import util.Utils;
import edu.cmu.lti.oaqa.resources.StanfordAnnotatorSingleton;
import edu.cmu.lti.oaqa.resources.StopWordSingleton;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.retrieval.SynSet;
import edu.cmu.lti.oaqa.type.retrieval.Synonym;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreLabel;

public class QueryOptimizer {
  public static String optimizeQuery(ExpandedQuestion question) {
    ArrayList<SynSet> as = Utils.fromFSListToCollection(question.getSynSets(), SynSet.class);
    edu.cmu.lti.oaqa.type.retrieval.SynSet synset;
    ArrayList<String> synonymList = new ArrayList<String>();
    String query = "(";
    for (SynSet syns : as) {
      ArrayList<Synonym> synonyms = Utils.fromFSListToCollection(syns.getSynonyms(), Synonym.class);
//      System.out.println("syns.getSynonyms() " +  syns.getOriginalToken());
      
//      System.out.println("number of synonyms found: " + synonyms.size());
      for (Synonym synonym : synonyms){
        String synonymText = StanfordLemmatizer.stemText(synonym.getSynonym());
//        System.out.println("lemmatized synonym " + synonymText);
        edu.stanford.nlp.pipeline.Annotation ann = new edu.stanford.nlp.pipeline.Annotation(
                synonymText);
        StanfordAnnotatorSingleton.getInstance().getPipeline().annotate(ann);
        for (CoreLabel term : ann.get(TokensAnnotation.class)) {
          String pos = term.get(PartOfSpeechAnnotation.class);
          String token = term.originalText().toLowerCase();

          if ((pos.contains("NN") || pos.contains("NNP")) && !StopWordSingleton.getInstance().isStopWord(token)
                  && !query.contains(token)) {
      //      System.out.println("adding " + synonym.getSynonym() + " of pos " + pos + " to query");
            query += synonym.getSynonym() + " OR ";              
          }

//          else{
  //          System.out.println("didn't add " + synonym.getSynonym() + " of pos " + pos + " to query");
    //      }
        }
      }
      query += syns.getOriginalToken() + " )AND( ";
    }
    query = query.substring(0, query.length() - 5);
    return query;
  }
}