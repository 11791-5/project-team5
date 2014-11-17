package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.lti.oaqa.type.retrieval.Document;


public class SimilarityMeasures 
{

  
  public double getSimilarity(List<String> documentSentence, List<String> queryTerms)
  {
    Map<String,Integer> docVector = convertToTermVector(convertToLowerCase(documentSentence));
    Map<String,Integer> queryVector = convertToTermVector(queryTerms);
    return computeCosineSimilarity(queryVector,docVector);
  }
  
  
  private List<String> convertToLowerCase(List<String> tokens) {
    List<String> lowerCaseTokens = new ArrayList<String>();
    for(String tokenElem:tokens)
    {
      lowerCaseTokens.add(tokenElem.toLowerCase());
    }
    return lowerCaseTokens;
  }


  private Map<String,Integer> convertToTermVector(List<String> tokens)
  {
    Map<String,Integer> tokenMap = new HashMap<String,Integer>();
    for(String tokenElem:tokens)
    {
      if(tokenMap.containsKey(tokenElem))
      {
        int currentFreq = tokenMap.get(tokenElem);
        tokenMap.put(tokenElem, currentFreq + 1);
      }
      else
        tokenMap.put(tokenElem, 1);
    }
    return tokenMap;
  }
  /**
   * 
   * @return cosine_similarity
   */
  public static double computeCosineSimilarity(Map<String, Integer> queryVector,Map<String, Integer> docVector) {
    double cosine_similarity=0.0;

    double dotProduct = 0.0;
    double queryEucLen = 0.0;
    double docEucLen = 0.0;

    for(String query: queryVector.keySet())
    {
      if(docVector.containsKey(query))
        dotProduct += queryVector.get(query) * docVector.get(query);
    }
    for(Integer freq:queryVector.values())
      queryEucLen += freq*freq;
    for(Integer freq:docVector.values())
      docEucLen += freq*freq;

    queryEucLen = Math.sqrt(queryEucLen);
    docEucLen = Math.sqrt(docEucLen);

    cosine_similarity = dotProduct/(queryEucLen*docEucLen);

    return cosine_similarity;
  }
  /**
   * 
   * @return Jaccard Index
   */
  private double computeJaccardIndex(Map<String, Integer> queryVector,Map<String, Integer> docVector) 
  {
    double JaccardIndex=0.0;
    double Jmin = 0.0;
    double Jmax  = 0.0;

    for(String query: queryVector.keySet())
    {
      if(docVector.containsKey(query))
      {
        Jmin += Math.min(docVector.get(query),queryVector.get(query));
        Jmax += Math.max(docVector.get(query),queryVector.get(query));
      }
    }    
    JaccardIndex = Jmin/Jmax;

    return JaccardIndex;
  }

  /**
   * 
   * @return Sorrensen Dice Coefficient
   */
  private double computeSorrensenDiceCoefficient(Map<String, Integer> queryVector,Map<String, Integer> docVector) 
  {
    double SorrDiceCoef = 0.0;
    double tokenIntersection = 0.0;

    for(String query: queryVector.keySet())
    {
      if(docVector.containsKey(query))
        tokenIntersection++;
    }    
    SorrDiceCoef = (2*tokenIntersection)/(queryVector.size()+docVector.size());

    return SorrDiceCoef;
  }

  /**
   * 
   * @return Okapi BM 25
   */
  private double computeOkapiBm25(Map<String, Integer> queryVector,
          Map<String, Integer> docVector,double avgdl,Map<String,Integer> docFrequency,int docNum) 
  {
    double k1 = 1.5;
    double b = 0.75;
    double BM25 = 0.0;
    for(String queryToken:queryVector.keySet())
    {
      if(docVector.containsKey(queryToken))
      {
        double IDF = Math.log((docNum -docFrequency.get(queryToken)+0.5)/(docFrequency.get(queryToken)+0.5));
        BM25 +=  (IDF*docVector.get(queryToken)*(k1+1))/(docVector.get(queryToken) + k1*(1 - b + b*(docVector.size()/docNum)));
      }
    }
    return BM25;
  }

  /**
   * @param documents
   * @return Avg Length of the documents
   * Required for the BM25 similarity metric
   */
  private double getAvgDocumentLength(ArrayList<Document> documents)
  {
    double avgdl = 0.0;
    for(Document doc:documents)
     // avgdl += doc.getTokenList().size();
    return avgdl/documents.size();
    return avgdl;
  }

  /**
   * @param documents
   * @param queryVector
   * @return The number of documents that have the particular token from the query string
   * 
   * Required for the BM25 metric
   */
  private Map<String,Integer> getDocFrequency(ArrayList<Document> documents,Map<String, Integer> queryVector)
  {
    Map<String,Integer> docFrequency = new HashMap<String,Integer>(); 
    int count = 0;
    for(String queryToken:queryVector.keySet())
    {
      count = 0;
      for(Document doc:documents)
      {
       // if(doc.getTokenList().containsKey(queryToken))
          count++;
      }
      docFrequency.put(queryToken, count); 
    }
    return docFrequency;
  }


}
