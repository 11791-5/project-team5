package edu.cmu.lti.oaqa.evaluation;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;

/**
 * Object for performing evaluation over all concepts.
 * @author root
 *
 */

public class EvaluatedConcept extends EvaluatedItem {

  /**
   * Evaluate concept
   * @param writer
   */
  public EvaluatedConcept(FileWriter writer) {
    super(writer);
    super.setItemType("concept");
    super.setItemTypeId(ConceptSearchResult.type);
  }

  /**
   * Get evaluated items as a list
   */
  @Override
  public List<Object> getEvaluatedItemsAsList(List<Object> itemObjects) {
    List<Object> conceptURIs = new ArrayList<Object>();
    for(Object conceptItem: itemObjects) {
      conceptURIs.add(((ConceptSearchResult) conceptItem).getUri().replace("GO:", "").replace("2014", "2012"));
    }
    return conceptURIs;
  }

  /**
   * Get gold standard items for question defined by question ID
   */
  @Override
  public List<Object> getGoldStandardItems(String questionId) {
    return new ArrayList<Object>(GoldStandardSingleton.getInstance().getGoldStandardAnswer()
            .get(questionId).getConcepts());
  }

}
