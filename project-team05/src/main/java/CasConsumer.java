import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.resource.ResourceProcessException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import edu.cmu.lti.oaqa.type.answer.Answer;


public class CasConsumer extends CasConsumer_ImplBase {

  @Override
  public void processCas(CAS aCAS) throws ResourceProcessException {
    FSIterator it = null;
    try {
      it = aCAS.getJCas().getAnnotationIndex(Answer.type).iterator();
    } catch (CASRuntimeException | CASException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    while(it.hasNext()) {
      
    }
  }
  
  public static void printDocumentAnswerToJson(String question, String type, String id, ArrayList<String> candidates) throws IOException {
    printListAnswerToJson(question, type, id, candidates, "documents");
  }

  public static void printListAnswerToJson(String question, String type, String id, ArrayList<String> candidates, String listAnswerType) throws IOException {
    JsonObject output = initJson(question, type, id);
    JsonArray resultArray = new JsonArray();
    for(String candidate: candidates) {
      resultArray.add(new JsonPrimitive(candidate));
    }
    output.add(listAnswerType, resultArray);
    writeJson(output);
  }
  
  public static void printConceptAnswerToJson(String question, String type, String id, ArrayList<String> candidates) throws IOException {
    printListAnswerToJson(question, type, id, candidates, "concepts");
  }
  
  private static JsonObject initJson(String question, String type, String id) {
    JsonObject output = new JsonObject();
    output.addProperty("body", question);
    output.addProperty("type", type);
    output.addProperty("id", id);
    return output;
  }

  private static void writeJson(JsonObject output) throws IOException {
    Gson gson = new Gson();
    FileWriter testWriter = new FileWriter(new File("Output_test.json"));
    testWriter.write(gson.toJson(output));
    testWriter.close();
  }
  
  public static void printTripleAnswerToJSON(String question, String type,  String id, ArrayList<ArrayList<String>> triples) throws IOException {
    JsonObject output = initJson(question, type, id);
    JsonArray resultArray = new JsonArray();
    for(ArrayList<String> candidate: triples) {
      JsonObject objObject = new JsonObject();
      objObject.addProperty("o", candidate.get(0));
      JsonObject predObj = new JsonObject();
      predObj.addProperty("p", candidate.get(1));
      JsonObject subjObj = new JsonObject();
      subjObj.addProperty("s", candidate.get(2));
      resultArray.add(objObject);
      resultArray.add(predObj);
      resultArray.add(subjObj);
    }
    output.add("triples", resultArray);
    writeJson(output);
  }
  
  public static void main(String args[]) throws IOException {
    String question = "Test question..?";
    String type = "factoid";
    String id = "100";
    ArrayList<String> documents = new ArrayList<String>();
    documents.add("http://www.ncbi.nlm.nih.gov/pubmed/23420787");
    documents.add("http://www.ncbi.nlm.nih.gov/pubmed/23397482");
    documents.add("http://www.ncbi.nlm.nih.gov/pubmed/23298766");
    ArrayList<ArrayList<String>> triples = new ArrayList<ArrayList<String>>();
    ArrayList<String> triple1 = new ArrayList<String>();
    triple1.add("o1");
    triple1.add("p1");
    triple1.add("s1");
    ArrayList<String> triple2 = new ArrayList<String>();
    triple2.add("o2");
    triple2.add("p2");
    triple2.add("s2");
    triples.add(triple1);
    triples.add(triple2);
    printTripleAnswerToJSON(question, type, id,  triples);
  }

}
