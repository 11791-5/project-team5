package edu.cmu.lti.oaqa.ranker;

import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.cmu.lti.oaqa.type.nlp.Token;
import util.DocumentSimilarity;
import util.Utils;

public class CosineSimilarity {

  public class RetrievalEvaluator extends CasConsumer_ImplBase {

    /**
     * For each query id, save the cosine similarity, relevance and text of the relevant document
     * {qid:[cosineSimilarity,relevance,text]}
     **/
    public HashMap<Integer, List<DocumentSimilarity>> sim = new HashMap<Integer, List<DocumentSimilarity>>();

    /** Array of all the ranks of the relevant documents **/
    public ArrayList<Integer> all_ranks = new ArrayList<Integer>();

    public void initialize() throws ResourceInitializationException {

    }

    /**
     * 
     * Compute, for each document, the cosine similarity with its query and persist the result in the
     * global variable "sim"
     */
    @Override
    public void processCas(CAS aCas) throws ResourceProcessException {

      JCas jcas;
      try {
        jcas = aCas.getJCas();
      } catch (CASException e) {
        throw new ResourceProcessException(e);
      }

      FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();

      if (it.hasNext()) {
        Document doc = (Document) it.next();

        // Make sure that your previous annotators have populated this in CAS
        FSList fsTokenList = doc.getTokenList();
        FSList queryFSTokenList = doc.getQueryTokenList();

        // Convert the document and query tokens from FS list to array list
        ArrayList<Token> tokenList = Utils.fromFSListToCollection(fsTokenList, Token.class);
        ArrayList<Token> queryTokenList = Utils.fromFSListToCollection(queryFSTokenList, Token.class);
        
        int qID = doc.getQueryID();
        int rel = doc.getRelevanceValue();
        String text = doc.getText();

        // create the query vector and the document vector
        HashMap<String, Integer> queryVector = new HashMap<String, Integer>();
        HashMap<String, Integer> documentVector = new HashMap<String, Integer>();

        // queryVector contains, for each token in the query ,
        // the associated string and the frequency of this token
        for (Token t : queryTokenList) {
          queryVector.put(t.getText(), t.getFrequency());
        }

        // documentVector contains, for each token in the document,
        // the associated string and the frequency of this token
        for (Token t : tokenList) {
          documentVector.put(t.getText(), t.getFrequency());
        }

        // make the queryVector and documentVector have the same words
        // (complete with zero frequencies whenever necessary)
        for (String s : documentVector.keySet()) {
          if (!((queryVector.keySet()).contains(s))) {
            queryVector.put(s, 0);
          }
        }

        for (String s : queryVector.keySet()) {
          if (!((documentVector.keySet()).contains(s))) {
            documentVector.put(s, 0);
          }
        }

        // compute the cosine similarity between them
        double cosSim = computeCosineSimilarity(queryVector, documentVector);
        System.out.println(qID + "\t" + cosSim + "\t" + rel + "\t" + text);

        // append it to array in {qid:[cosinusSimilarities,rel,text]}
        DocumentSimilarity docS = new DocumentSimilarity(cosSim, qID, rel, text);
        if (sim.containsKey(qID)) {
          List<DocumentSimilarity> l = (List<DocumentSimilarity>) sim.get(qID);
          if (docS.getRelevance() != 99) {
            l.add(docS);
            sim.put(qID, l);
          }
        } else {
          List<DocumentSimilarity> l = new ArrayList<DocumentSimilarity>();
          if (docS.getRelevance() != 99) {
            l.add(docS);
            sim.put(qID, l);
          }
        }
      }
    }

    /**
     * Compute the cosine similarities between the documents and their related query. Find the rank of
     * the relevant document Compute the MRR
     **/
    @Override
    public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
            IOException {

      super.collectionProcessComplete(arg0);

      File f = null;
      boolean bool = false;

      // check if 'report.txt' already exists and delete it if so
      delete_file(f, bool);

      PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("report.txt", true)));

      // for each query id,
      // sort the list of documents from bigger cosine similarity to smallest
      int Q = 0;
      for (int k : sim.keySet()) {
        if (k > Q) {
          Q = k;
        }
      }

      for (int q = 1; q <= Q; q++) {
        List<DocumentSimilarity> ds = sim.get(q);
        Collections.sort(ds);

        // find the rank of the first relevant document
        int rank = 0;
        for (int i = 0; i < ds.size(); i++) {
          DocumentSimilarity doc = ds.get(i);
          if (doc.getRelevance() == 1) {
            rank = i + 1;
            // "if you have ties, you need to rank relevant documents higher than not
            // relevant"
            if (i > 0){
              for (int j = i - 1; j > 0; j--){
                if (ds.get(j).getCosineSimilarity() == ds.get(i).getCosineSimilarity()){
                  rank --;
                }
              }  
            }
            
            doc.setRank(rank);
            all_ranks.add(rank);
            String to_write = String.format("cosine=%1.4f\trank=%d\tqid=%d\trel=1\t%s",
                    doc.getCosineSimilarity(), rank, q, doc.getText());
            // write to report.txt
            writer.println(to_write);
            break;
          }
        }
      }

      // compute the MRR
      double mrr = 0;
      for (int r : all_ranks) {
        mrr += (double) 1 / r;
      }
      mrr /= (double) Q;
      String MRR = String.format("MRR=%1.4f", mrr);

      // write MRR to file
      writer.println(MRR);
      writer.close();

    }

    /**
     * Compute the cosine similarity between the query vector and the document vector
     * 
     * @param queryVector
     * @param docVector
     * @return
     */
    private double computeCosineSimilarity(Map<String, Integer> queryVector,
            Map<String, Integer> docVector) {
      double cosine_similarity = 0.0;

      if (queryVector.size() != docVector.size()) {
        System.err.println("queryVector and docVector should have the same size");
        return 0;
      }

      cosine_similarity = dot(queryVector, docVector);
      if (norm(queryVector) * norm(docVector) != 0) {
        cosine_similarity /= (norm(queryVector) * norm(docVector));
      } else {
        System.err.println("ERROR: norm(queryVector)*norm(docVector) = 0");
      }

      return cosine_similarity;
    }

    /**
     * Compute the dot product of two vectors
     * 
     * @param vector1
     * @param vector2
     * @return
     */
    private double dot(Map<String, Integer> vector1, Map<String, Integer> vector2) {
      if (vector1.size() != vector2.size()) {
        System.err.println("vector1 and vector2 should have the same size");
        return 0;
      }

      double sum = 0;
      for (String w : vector1.keySet()) {
        sum += vector1.get(w) * vector2.get(w);
      }

      return sum;
    }

    /**
     * Compute the Euclidian norm of a vector
     * 
     * @param vector
     * @return
     */
    private double norm(Map<String, Integer> vector) {

      return Math.sqrt(dot(vector, vector));
    }

    /**
     * Delete 'report.txt' if it already exists
     * 
     * @param f
     * @param bool
     */
    void delete_file(File f, boolean bool) {
      try {
        // create new files
        f = new File("report.txt");

        // create new file in the system
        f.createNewFile();

        // tests if file exists
        bool = f.exists();

        if (bool == true) {
          // delete() invoked
          f.delete();
        }

      } catch (Exception e) {
        // if any error occurs
        e.printStackTrace();
      }

    }
  }
}
