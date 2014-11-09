package edu.cmu.lti.oaqa.type.input;

import java.util.List;

public class GoldStandardAnswer 
{
  
  String question;
  List<String> conepts;
  List<String> documents;
  
  public String getQuestion() {
    return question;
  }
  public void setQuestion(String question) {
    this.question = question;
  }
  public List<String> getConepts() {
    return conepts;
  }
  public void setConepts(List<String> conepts) {
    this.conepts = conepts;
  }
  public List<String> getDocuments() {
    return documents;
  }
  public void setDocuments(List<String> documents) {
    this.documents = documents;
  }

}
