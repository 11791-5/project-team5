package edu.cmu.lti.oaqa.annotators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import json.gson.Snippet;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.tcas.Annotation;

import util.Utils;
import edu.cmu.lti.oaqa.type.answer.Answer;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult;

public class AnswerAnnotator extends JCasAnnotator_ImplBase{

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    // TODO Auto-generated method stub
    FSIterator<Annotation> questions = aJCas.getAnnotationIndex(ExpandedQuestion.type).iterator();
    while (questions.hasNext()) {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();
      String questionid = question.getId();
      List<Object> passageItems = Utils.extractUIMATypeAsList(SnippetSearchResult.type, aJCas);
      List<ArrayList<Object>> retrievedArticleOffsetPairs = new ArrayList<ArrayList<Object>>();
      
      
      ArrayList<String> snippets = new  ArrayList<String>();
      StringList strlist = null;
      for(Object currentSnippet: passageItems) {
        Passage p = ((SnippetSearchResult)currentSnippet).getSnippets(); 
        if (strlist!=null)
          strlist = ((SnippetSearchResult)currentSnippet).getQuestionsSyn();

        snippets.add(p.getText());
        
      }
      ArrayList<String> synonym = new ArrayList<String>();
      int i=0;
      while (strlist.getNthElement(i) !=null )
      {
        synonym.add(strlist.getNthElement(i));
        System.out.println(strlist.getNthElement(i));
        i++;
      }
//      getAnswer(snippets,question.);
      Map<Integer,String> hmap= getAnswer(snippets,synonym);
      
//      HashMap<String,Integer> map = new HashMap<String,Integer>();
//      ValueComparator bvc =  new ValueComparator(map);
//      TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);

//      sorted_map.putAll(map);
      
      int rank=1;
      for (Entry<Integer, String> entry : hmap.entrySet())
      { 
        Answer ans = new Answer(aJCas);
        ans.setText(entry.getValue());
        ans.setRank(entry.getKey());        
        ans.addToIndexes();
      }
      
    }
  }

}
