package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunker;
import com.aliasi.chunk.Chunking;
import com.aliasi.chunk.ConfidenceChunker;

/**
 * @author niloygupta
 * 
 * Runs the LingPipe package trained on the GENETag dataset using a HMM model.
 */
public class LingPipeGeneEntityRecognizer {

  /**
   * @param geneText Data point i.e. a sentence in the input file
   * @return List of recognized genes**/
  
  public List<Chunk> geneTag(String geneText)
  {
    
    List<Chunk> chunks = new ArrayList<Chunk>();
    Chunker chunker = GeneChunker.getInstance().getChunker();
   
    Chunking chunking = chunker.chunk(geneText);
    for(Chunk chunk:chunking.chunkSet())
      chunks.add(chunk);

    return chunks;
  }
  /**
   * @param geneText Data point i.e. a sentence in the input file
   * @return List of recognized genes
   * Uses the nBestChunk API which returns the top N tags with their confidences.
   */
  public List<Chunk> nBestGeneTags(String geneText, int N_BEST)
 {

    List<Chunk> chunks = new ArrayList<Chunk>();
    ConfidenceChunker chunker = NBestGeneChunker.getInstance().getChunker();

    Iterator<Chunk> chunkIter = chunker.nBestChunks(geneText.toCharArray(), 0,geneText.length(), N_BEST);
   
    while(chunkIter.hasNext()) 
      chunks.add(chunkIter.next());

    return chunks;
  }
  
}
