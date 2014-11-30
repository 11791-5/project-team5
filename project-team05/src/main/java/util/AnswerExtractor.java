package util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Answer extractor class
 */
public class AnswerExtractor {

  /**
   * Get answers given snippets and query terms
   * 
   * @param snippets
   * @param queryTerms
   * @return
   */
  public static Map<Integer, String> getAnswers(List<String> snippets, List<String> queryTerms) {
    Map<String, Integer> potentialAnswers = new HashMap<String, Integer>();

    for (String snippet : snippets) {
      // Get question terms from bioTermExtractor
      HashSet<String> questionTerms = BioTermExtractor.getBioTerms(snippet);
      for (String token : questionTerms) {
        if (queryTerms.contains(token))
          continue;
        if (!potentialAnswers.containsKey(token))
          // if the potential answers do not contain
          // the token, then add it
          potentialAnswers.put(token, 0);
        potentialAnswers.put(token, potentialAnswers.get(token) + 1);
      }
    }

    TreeMap<String, Integer> rankedPotentialAnswers = sortMapByValue(potentialAnswers);
    return getTopAnswers(rankedPotentialAnswers);
  }

  /**
   * Get the top answers
   * 
   * @param rankedPotentialAnswers
   * @return
   */
  private static Map<Integer, String> getTopAnswers(TreeMap<String, Integer> rankedPotentialAnswers) {
    Map<Integer, String> topAnswers = new HashMap<Integer, String>();

    int rank = 1;
    for (Entry<String, Integer> answer : rankedPotentialAnswers.entrySet()) {
      String answerString = answer.getKey();
      Integer value = answer.getValue();

      // Get the answers for qhich the freq is at least 2
      if (value > 1 && value < Collections.max(rankedPotentialAnswers.values()))
        topAnswers.put(rank++, answerString);
    }
    return topAnswers;
  }

  /**
   * Sort MAP by value
   * 
   * @param map
   * @return
   */
  public static TreeMap<String, Integer> sortMapByValue(Map<String, Integer> map) {
    ValueComparator vc = new ValueComparator(map);
    TreeMap<String, Integer> sortedMapByValue = new TreeMap<String, Integer>(vc);
    sortedMapByValue.putAll(map);
    return sortedMapByValue;
  }
}

/**
 *
 */
class ValueComparator implements Comparator<String> {

  Map<String, Integer> map;

  public ValueComparator(Map<String, Integer> base) {
    this.map = base;
  }

  /**
   *
   */
  public int compare(String a, String b) {
    if (map.get(a) >= map.get(b)) {
      return 1;
    }
    return -1;
  }
}
