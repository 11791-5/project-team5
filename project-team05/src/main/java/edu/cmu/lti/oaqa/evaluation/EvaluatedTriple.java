package edu.cmu.lti.oaqa.evaluation;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;

public class EvaluatedTriple extends EvaluatedItem{

  public EvaluatedTriple(FileWriter writer) {
    super(writer);
    super.setItemType("triple");
    super.setItemTypeId(TripleSearchResult.type);
  }

  @Override
  public List<Object> getEvaluatedItemsAsList(List<Object> itemObjects) {
    List<Object> triples = new ArrayList<Object>();
    for (Object triple : itemObjects) {
      triples.add((triple));
    }
    return triples;
  }
  
  public static double getPrecision(List<Object> tripleItems, List<Object> goldTriples) {
    int numTruePos = 0;
    for(Object tripleObj: tripleItems) {
      Triple triple = (Triple)tripleObj;
      for(Object goldTripleObj: goldTriples) {
        Triple goldTriple = (Triple) goldTripleObj;
        if(triple.getObject().equals(goldTriple.getObject())
                && triple.getSubject().equals(goldTriple.getSubject())
                &&
                triple.getPredicate().equals(goldTriple.getPredicate())) {
          numTruePos++;
        }
      }
    }
    return (double)numTruePos/tripleItems.size();
  }


  @Override
  public List<Object> getGoldStandardItems(String questionId) {
    List<Object> goldTriples = new ArrayList<Object>();
    List<json.gson.Triple> goldTriplesReturned = GoldStandardSingleton.getInstance()
            .getGoldStandardAnswer().get(questionId).getTriples();
    if(goldTriplesReturned != null) {
      goldTriples.addAll(goldTriplesReturned);
    }
    return goldTriples;
  }
}
