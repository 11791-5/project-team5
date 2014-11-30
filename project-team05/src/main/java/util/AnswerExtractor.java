package util;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class AnswerExtractor {

  public static Map<Integer, String> getAnswers(List<String> snippets, List<String> queryTerms) {
    Map<String, Integer> potentialAnswers = new HashMap<String, Integer>();

    for (String snippet : snippets) {

      HashSet<String> questionTerms = BioTermExtractor.getBioTerms(snippet);
      for(String token:questionTerms)
      {
        if(queryTerms.contains(token))
          continue;
        if (!potentialAnswers.containsKey(token))
          potentialAnswers.put(token, 0);
        potentialAnswers.put(token, potentialAnswers.get(token) + 1);
      }
    }

    TreeMap<String, Integer> rankedPotentialAnswers = sortMapByValue(potentialAnswers);
    return getTopAnswers(rankedPotentialAnswers);

  }

  private static Map<Integer, String> getTopAnswers(TreeMap<String, Integer> rankedPotentialAnswers) {
    Map<Integer, String> topAnswers = new HashMap<Integer, String>();

    int rank = 1;
    for (Entry<String, Integer> answer : rankedPotentialAnswers.entrySet()) {
      String answerString = answer.getKey();
      Integer value = answer.getValue();
      if (value > 1)
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
