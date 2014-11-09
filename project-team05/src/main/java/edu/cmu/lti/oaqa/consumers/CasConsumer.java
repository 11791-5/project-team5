/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.cmu.lti.oaqa.consumers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import edu.cmu.lti.oaqa.type.kb.ConceptMention;

/**
 * A simple CAS consumer that writes the CAS to XMI format.
 * <p>
 * This CAS Consumer takes one parameter:
 * <ul>
 * <li><code>OutputDirectory</code> - path to directory into which output files will be written</li>
 * </ul>
 */
public class CasConsumer extends CasConsumer_ImplBase {
  /**
   * Name of configuration parameter that must be set to the path of a directory into which the
   * output files will be written.
   */
  public static final String PARAM_OUTPUTDIR = "OutputFile";

  private File mOutputFile;

  public void initialize() throws ResourceInitializationException {
    mOutputFile = new File((String) getConfigParameterValue(PARAM_OUTPUTDIR));
  }

  /**
   * Processes the CAS which was populated by the TextAnalysisEngines. <br>
   * In this case, the CAS is converted to an offset representation .
   * 
   * @param aCAS
   *          a CAS which has been populated by the TAEs
   * 
   * @throws ResourceProcessException
   *           if there is an error in processing the Resource
   * 
   * @see org.apache.uima.collection.base_cpm.CasObjectProcessor#processCas(org.apache.uima.cas.CAS)
   */
  public void processCas(CAS aCAS) throws ResourceProcessException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(new FileOutputStream(mOutputFile, true));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } 
    
    FSIterator<Annotation> concepts = jcas.getAnnotationIndex(ConceptMention.type).iterator();
    while (concepts.hasNext()) {
      ConceptMention concetpMention = (ConceptMention) concepts.next();
      writer.println(concetpMention.getConcept().getName() +" | "+ concetpMention.getConcept().getUris());
    }
    writer.close();
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
