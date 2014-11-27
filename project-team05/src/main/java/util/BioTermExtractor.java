package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.aliasi.chunk.Chunk;

import edu.cmu.lti.oaqa.annotators.AbnerTagger;
import edu.cmu.lti.oaqa.annotators.LingPipeGeneEntityRecognizer;
import edu.cmu.lti.oaqa.resources.StanfordAnnotatorSingleton;
import edu.cmu.lti.oaqa.resources.StopWordSingleton;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;

public class BioTermExtractor 
{
  static LingPipeGeneEntityRecognizer entityR = new LingPipeGeneEntityRecognizer();
  
  public static HashSet<String> getBioTerms(String text)
  {
    /*//String stemmedText = "";
    HashSet<String> bioTerms = new HashSet<String>();//populateMap(questionTerms);
    String stemmedText = StanfordLemmatizer.stemText(text);
    edu.stanford.nlp.pipeline.Annotation ann = new edu.stanford.nlp.pipeline.Annotation(stemmedText);
    StanfordAnnotatorSingleton.getInstance().getPipeline().annotate(ann);
    for (CoreLabel term : ann.get(TokensAnnotation.class)) {
      String pos = term.get(PartOfSpeechAnnotation.class);
      String token = term.originalText().toLowerCase();//pos.contains("NN") && 
      if (!StopWordSingleton.getInstance().isStopWord(token)) {
          //stemmedText +=token;
        bioTerms.add(token);
      }
    }*/
    
    String[][] result = AbnerTagger.getInstance().getTagger().getEntities(text);
    List<String> questionTerms = new ArrayList<String>();
    questionTerms.addAll(Arrays.asList(result[0]));
    HashSet<String> bioTerms = populateMap(questionTerms);
    String stemmedText = StanfordLemmatizer.stemText(text);
    List<Chunk> chunks = entityR.geneTag(stemmedText);
    
    for(Chunk geneChunk:chunks)
    {
      String geneToken = stemmedText.substring(geneChunk.start(), geneChunk.end());
      if(!StopWordSingleton.getInstance().isStopWord(geneToken))
        bioTerms.add(geneToken); 
    }
    
    edu.stanford.nlp.pipeline.Annotation ann = new edu.stanford.nlp.pipeline.Annotation(stemmedText);
    StanfordAnnotatorSingleton.getInstance().getPipeline().annotate(ann);
    for (CoreLabel term : ann.get(TokensAnnotation.class)) {
      String pos = term.get(PartOfSpeechAnnotation.class);
      String token = term.originalText();//pos.contains("NN") && 
      if (pos.contains("NN") &&!StopWordSingleton.getInstance().isStopWord(token)) {
          //stemmedText +=token;
        bioTerms.add(token);
      }
    }
    
    return bioTerms;
  }
  private static HashSet<String> populateMap(List<String> questionTerms) {
    HashSet<String> bioTerms = new HashSet<String>();
    for(String term:questionTerms)
        bioTerms.add(term);
    return bioTerms;
  }
 
}
