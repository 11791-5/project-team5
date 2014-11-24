package util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.cmu.lti.oaqa.resources.StanfordAnnotatorSingleton;
import edu.cmu.lti.oaqa.resources.StopWordSingleton;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;

public class AnswerExtractor {

  public static Map<Integer, String> getAnswers(List<String> snippets, List<String> queryTerms) {
    Map<String, Integer> potentialAnswers = new HashMap<String, Integer>();

    for (String snippet : snippets) {
      String snippetText = StanfordLemmatizer.stemText(snippet);
      edu.stanford.nlp.pipeline.Annotation ann = new edu.stanford.nlp.pipeline.Annotation(
              snippetText);
      StanfordAnnotatorSingleton.getInstance().getPipeline().annotate(ann);
      for (CoreLabel term : ann.get(TokensAnnotation.class)) {
        String pos = term.get(PartOfSpeechAnnotation.class);
        String token = term.originalText().toLowerCase();
        if (pos.contains("NN") && !StopWordSingleton.getInstance().isStopWord(token)
                && !queryTerms.contains(token)) {
          if (!potentialAnswers.containsKey(token))
            potentialAnswers.put(token, 0);
          potentialAnswers.put(token, potentialAnswers.get(token) + 1);
        }
      }
    }

    TreeMap<String, Integer> rankedPotentialAnswers = sortMapByValue(potentialAnswers);
    return getTopAnswers(rankedPotentialAnswers);

  }

  private static Map<Integer, String> getTopAnswers(TreeMap<String, Integer> rankedPotentialAnswers) {
    Map<Integer, String> topAnswers = new HashMap<Integer, String>();
    double sum = 0;
    for (Integer freq : rankedPotentialAnswers.values())
      sum += freq;
    double threhold = sum / 2;
    int rank = 1;
    for (Entry<String, Integer> answer : rankedPotentialAnswers.entrySet()) {
      String answerString = answer.getKey();
      Integer value = answer.getValue();
      if (value > threhold)
        topAnswers.put(rank++, answerString);
    }
    return topAnswers;
  }

  public static TreeMap<String, Integer> sortMapByValue(Map<String, Integer> map) {
    ValueComparator vc = new ValueComparator(map);
    TreeMap<String, Integer> sortedMapByValue = new TreeMap<String, Integer>(vc);
    sortedMapByValue.putAll(map);
    return sortedMapByValue;
  }
}

class ValueComparator implements Comparator<String> {

  Map<String, Integer> map;

  public ValueComparator(Map<String, Integer> base) {
    this.map = base;
  }

  public int compare(String a, String b) {
    if (map.get(a) >= map.get(b)) {
      return 1;
    } else {
      return -1;
    }
  }
}
