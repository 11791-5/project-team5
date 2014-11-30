package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import util.GoPubMedServiceSingleton;
import util.Utils;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse.Document;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.retrieval.SynSet;
import edu.cmu.lti.oaqa.type.retrieval.Synonym;

public class DocumentAnnotator extends JCasAnnotator_ImplBase
{

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    edu.cmu.lti.oaqa.type.retrieval.Document doc; 
    FSIterator<Annotation> questions = aJCas.getAnnotationIndex(ExpandedQuestion.type).iterator();
    while (questions.hasNext()) 
    {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();
      /*
       * Uses the brute force technique of querying the PubMed database
       * 
       */
      List<String> questionTerms = getQuestionTerms(question);
      Map<String,Integer> documentIds = new HashMap<String,Integer>();
      Map<String,Document> documentMap = new HashMap<String,Document>();
      int nGrams = 0;
      while(!hasDocThreshold(documentIds,4)&&nGrams<questionTerms.size())
      {
        List<String> queryGrams = getQueryGrams(questionTerms,nGrams++);
        for(String queryGram:queryGrams)
        {
          PubMedSearchServiceResponse.Result documents = GoPubMedServiceSingleton.getService().getDocuments(queryGram);
            if(documents!=null && documents.getDocuments()!=null && !documents.getDocuments().isEmpty())
            {
              for(Document document:documents.getDocuments())
              {
                if(!documentIds.containsKey(document.getPmid()))
                {
                  documentMap.put(document.getPmid(), document);
                  documentIds.put(document.getPmid(),0);
                }
                documentIds.put(document.getPmid(),documentIds.get(document.getPmid())+1);
              }
            }
        }
      }
      int rank =0;
      for(String docId: documentIds.keySet())
      {
        if(documentIds.get(docId)>1)
        {
          Document document = documentMap.get(docId);
          doc = new edu.cmu.lti.oaqa.type.retrieval.Document(aJCas);
          doc.setDocId(document.getPmid());
          doc.setText(document.getDocumentAbstract());
          doc.setUri(document.getPmid());
          doc.setRank(rank++);
          doc.addToIndexes();
        }
      }
    }
  }

  private boolean hasDocThreshold(Map<String, Integer> documentIds, int threshold) {
    
    int relDocs = 0;
    for(Integer docCount:documentIds.values())
    {
      if(docCount>=2)
        relDocs++;
    }
    if(relDocs<threshold)
      return false;
    return true;
  }

  /**
   * return query terms with n-grams and combined them with AND operators 
   * @param questionTerms
   * @param nGrams
   * @return
   */
  private List<String> getQueryGrams(List<String> questionTerms, int nGrams) {
    List<String> queryGrams = new ArrayList<String>();
    int minIndex = 0;
    int maxIndex = nGrams;
    for(int i=0;i<questionTerms.size();i++,minIndex++,maxIndex++)
    {
      String query = "";
      for(int j=0;j<questionTerms.size();j++)
      {
        if(nGrams!=0 && j>=minIndex && j<maxIndex)
          continue;
        query += questionTerms.get(j)+" AND ";
      }
      queryGrams.add(query.substring(0,query.length()-5));
      if(nGrams==0)
        break;
    }
    return queryGrams;
  }

 /**
  * Retrieved all synsets and synonyms from the question 
  * @param question
  * @return
  */
  private List<String> getQuestionTerms(ExpandedQuestion question) 
  {
    ArrayList<SynSet> as = Utils.fromFSListToCollection(question.getSynSets(), SynSet.class);
    List<String> queryTokens = new ArrayList<String>();
    for (SynSet syns : as) 
    {
      ArrayList<Synonym> synonyms = Utils.fromFSListToCollection(syns.getSynonyms(),Synonym.class);
      String query = "(";
      for (Synonym synonym : synonyms)
        query+= synonym.getSynonym() + " OR ";
      query += syns.getOriginalToken() + ")";
      queryTokens.add(query);
    }
    return queryTokens;
  }
}
