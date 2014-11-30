package util;


/**
 * A class used in the RetrievalEvaluator to save information about 
 * a document (queryID, relevance, text, rank)
 * @author larbi
 *
 */
public class DocumentSimilarity implements Comparable<DocumentSimilarity> {
  private Double cosineSimilarity;
  String text;
  int relevance;
  int qid;
  int rank;
  
  public DocumentSimilarity(double cosSim, int qid, int rel, String text2) {
     this.cosineSimilarity = cosSim;
     this.relevance = rel;
     this.text = text2;
     this.qid = qid;
     this.rank = -1;
  }

  public double getCosineSimilarity(){
    return this.cosineSimilarity;
  }
  
  public String getText(){
    return this.text;
  }
  
  public int getRelevance(){
    return this.relevance;
  }

  public int getQid(){
    return this.qid;
  }

  public int getRank(){
    return this.rank;
  }

  public void setQid(int qid){
    this.qid = qid;
  }
  
  public void setRank(int rank){
    this.rank = rank;
  }
  
  public void setCosineSimilarity(double cosSim){
    this.cosineSimilarity = cosSim;
  }
  
  public void setText(String t){
    this.text = t;
  }
  
  public void setRelevance(int rel){
    this.relevance = rel;
  }

  public static int sign(double f) {
    if (f == 0){
      return 0;
    }
    
    if (f > 0){
      return 1;
    }
    
    return -1;
    
  }


/**
 * Compare two cosine similarities
 * return the sign of the difference
 */
  @Override
  public int compareTo(DocumentSimilarity arg0) {
    // rank from bigger to smaller
    double cosinusSimilarity1 = this.getCosineSimilarity();
    double cosinusSimilarity2 = arg0.getCosineSimilarity();

    return sign(cosinusSimilarity2 - cosinusSimilarity1);

  }
  
}
