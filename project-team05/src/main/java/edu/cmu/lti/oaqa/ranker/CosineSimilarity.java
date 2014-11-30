package edu.cmu.lti.oaqa.ranker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import edu.cmu.lti.oaqa.type.retrieval.Document;

public class CosineSimilarity {

  public List<Document> rankDocuments(List<String> queryTokens, List<Document> docs) {
    int corpusSize = docs.size();
    Map<String, Double> query = getFreq(queryTokens);

    for (Document doc : docs) {
      List<String> docText = new ArrayList<String>();
      for (String s: doc.getText().split("\\s+")){
        docText.add(s);
      }
      Map<String, Double> docTokens = getFreq(docText);
      
      double score = computeCosSim(query, docTokens);
      
      doc.setScore(score);
    }
    
    docs.sort((doc1, doc2) -> Double.compare(doc1.getScore(), doc2.getScore()));
    Collections.reverse(docs);
    
    for (int i=0; i < corpusSize; i++){
      Document doc = docs.get(0);
      doc.setRank(i+1);
    }
    
   return docs;
  }



  private double computeCosSim(Map<String, Double> queryVector,
          Map<String, Double> documentVector) {
    double cosSim = 0.0;
    double normalizationFactor = norm(queryVector.values()) * norm(documentVector.values());

    HashSet<String> commonWords = new HashSet<String>(queryVector.keySet());
    commonWords.retainAll(documentVector.keySet());
    
    for (String k : commonWords)
    {
      cosSim += (queryVector.get(k) * documentVector.get(k))/normalizationFactor;
    }
    return cosSim;
  }

  private Map<String, Double> getFreq(List<String> tokens) {
    HashMap<String, Double> freq = new HashMap<>();

    for (String s: tokens){
      freq.put(s, freq.get(s) + 1);
    }
    
    return freq;
  }
  
  private double norm(Iterable<Double> queryVector){
    double result = 0;
    for (double d: queryVector){
      result += d*d;
    }
    return Math.sqrt(result);
  }

}