package edu.cmu.lti.oaqa.evaluation;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.oaqa.consumers.GoldStandardSingleton;
/**
 * Object for performing evaluation over all documents.
 * @author root
 *
 */

public class EvaluatedDocument extends EvaluatedItem{

  public EvaluatedDocument(FileWriter writer) {
    super(writer);
    super.setItemType("document");
    super.setItemTypeId(edu.cmu.lti.oaqa.type.retrieval.Document.type);
    // TODO Auto-generated constructor stub
  }

  @Override
  public List<Object> getEvaluatedItemsAsList(List<Object> itemObjects) {
    List<Object> documentIds = new ArrayList<Object>();
    for (Object docObj : itemObjects) {
      documentIds.add(((edu.cmu.lti.oaqa.type.retrieval.Document) docObj).getDocId());
    }
    return documentIds;
  }

  @Override
  public List<Object> getGoldStandardItems(String questionId) {
    return new ArrayList<Object>(GoldStandardSingleton.getInstance().getGoldStandardAnswer()
            .get(questionId).getDocuments());
  }

}
