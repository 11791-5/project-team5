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
<<<<<<< HEAD

    // tried to split the synonyms and original query term and keep only NN and NNP
    // -> decreases performance
=======
>>>>>>> master
    ArrayList<SynSet> as = Utils.fromFSListToCollection(question.getSynSets(), SynSet.class);
    edu.cmu.lti.oaqa.type.retrieval.SynSet synset;

    ArrayList<String> synonymList = new ArrayList<String>();

    String query = "(";
<<<<<<< HEAD

    for (SynSet syns : as) {
      ArrayList<Synonym> synonyms = Utils.fromFSListToCollection(syns.getSynonyms(), Synonym.class);

      // Filtering NN and NNP
      for (Synonym synonym : synonyms) {
        String synonymText = StanfordLemmatizer.stemText(synonym.getSynonym());

        edu.stanford.nlp.pipeline.Annotation ann = new edu.stanford.nlp.pipeline.Annotation(
                synonymText);

=======
    for (SynSet syns : as) {
      ArrayList<Synonym> synonyms = Utils.fromFSListToCollection(syns.getSynonyms(), Synonym.class);
//      System.out.println("syns.getSynonyms() " +  syns.getOriginalToken());
      
//      System.out.println("number of synonyms found: " + synonyms.size());
      for (Synonym synonym : synonyms){
        String synonymText = StanfordLemmatizer.stemText(synonym.getSynonym());
//        System.out.println("lemmatized synonym " + synonymText);
        edu.stanford.nlp.pipeline.Annotation ann = new edu.stanford.nlp.pipeline.Annotation(
                synonymText);
>>>>>>> master
        StanfordAnnotatorSingleton.getInstance().getPipeline().annotate(ann);
        for (CoreLabel term : ann.get(TokensAnnotation.class)) {
          String pos = term.get(PartOfSpeechAnnotation.class);
          String token = term.originalText().toLowerCase();

<<<<<<< HEAD
          if ((pos.contains("NN") || pos.contains("NNP"))
                  && !StopWordSingleton.getInstance().isStopWord(token) && !query.contains(token)) {
            // filter out 'small' queries
            if (synonym.getSynonym().length() > 1) {
              // splitting synonyms
              String[] splited = synonym.getSynonym().split("\\s+");
              
              for (String s : splited) {
                // filter out the non NN or NNP
                String sText = StanfordLemmatizer.stemText(s);
                
                System.out.println("testing synonym " + s);
                
                edu.stanford.nlp.pipeline.Annotation annS = new edu.stanford.nlp.pipeline.Annotation(
                        sText);
                StanfordAnnotatorSingleton.getInstance().getPipeline().annotate(annS);

                for (CoreLabel termS : annS.get(TokensAnnotation.class)) {
                  String posS = termS.get(PartOfSpeechAnnotation.class);
                  String tokenS = termS.originalText().toLowerCase();

                  if ((posS.contains("NN") || pos.contains("NNP"))
                          && !StopWordSingleton.getInstance().isStopWord(tokenS) && !query.contains(tokenS)) {
                    if (s.length() > 1) {
                      System.out.println("adding synonym " + s);
                      query += s + " OR ";
                    }
                  }
                }
//                if (s.length() > 1) {
  //                query += s + " OR ";
    //            }
              }
              // query += synonym.getSynonym() + " OR ";
            }
          }
        }
      }
/**
      // filter out the non NN or NNP
      String originalText = StanfordLemmatizer.stemText(syns.getOriginalToken());
      System.out.println("testing original query term " + syns.getOriginalToken());
      edu.stanford.nlp.pipeline.Annotation annOriginal = new edu.stanford.nlp.pipeline.Annotation(
              originalText);
      StanfordAnnotatorSingleton.getInstance().getPipeline().annotate(annOriginal);
      for (CoreLabel termOriginal : annOriginal.get(TokensAnnotation.class)) {
        String posOriginal = termOriginal.get(PartOfSpeechAnnotation.class);
        String tokenOriginal = termOriginal.originalText().toLowerCase();

        if ((posOriginal.contains("NN") || posOriginal.contains("NNP"))
                && !StopWordSingleton.getInstance().isStopWord(tokenOriginal) && !query.contains(tokenOriginal)) {
          if (syns.getOriginalToken().length() > 1) {
            System.out.println("adding original query term " + syns.getOriginalToken());
            query += syns.getOriginalToken() + " )AND( ";
          }
        }
      }
   */   
      
      
      System.out.println("original token " + syns.getOriginalToken());
=======
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
>>>>>>> master
      query += syns.getOriginalToken() + " )AND( ";

    }
<<<<<<< HEAD

    query = query.substring(0, query.length() - 5);
    System.out.println("final query sent " + query);
    return query;

  }
}
=======
    query = query.substring(0, query.length() - 5);
    return query;
  }
}
>>>>>>> master
