package edu.cmu.lti.oaqa.evaluation;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;
/**
 * Object for performing evaluation over all documents.
 * @author root and root
 *
 */

public class EvaluatedDocument extends EvaluatedItem{

  /**
   * Evaluate document
   * @param writer
   */
  public EvaluatedDocument(FileWriter writer) {
    super(writer);
    super.setItemType("document");
    super.setItemTypeId(edu.cmu.lti.oaqa.type.retrieval.Document.type);
  }

  /**
   * Get evaluated items as a list
   */
  @Override
  public List<Object> getEvaluatedItemsAsList(List<Object> itemObjects) {
    List<Object> documentIds = new ArrayList<Object>();
    for (Object docObj : itemObjects) {
      documentIds.add(((edu.cmu.lti.oaqa.type.retrieval.Document) docObj).getDocId());
    }
    return documentIds;
  }

  /**
   * Get gold standard items for question defined by questionID
   */
  @Override
  public List<Object> getGoldStandardItems(String questionId) {
    return new ArrayList<Object>(GoldStandardSingleton.getInstance().getGoldStandardAnswer()
            .get(questionId).getDocuments());
  }

}
