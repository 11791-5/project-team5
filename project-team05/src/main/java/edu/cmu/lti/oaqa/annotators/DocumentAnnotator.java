package edu.cmu.lti.oaqa.annotators;

import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import util.GoPubMedServiceSingleton;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse.Document;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse.MeshAnnotation;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;

public class DocumentAnnotator extends JCasAnnotator_ImplBase
{

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    edu.cmu.lti.oaqa.type.retrieval.Document doc; 
    FSIterator<Annotation> questions = aJCas.getAnnotationIndex(ExpandedQuestion.type).iterator();
    while (questions.hasNext()) 
    {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();
      //ArrayList<SynSet> synSets = Utils.fromFSListToCollection(question.getSynSets(), SynSet.class);
      String questionText = QueryOptimizer.optimizeQuery(question);
      PubMedSearchServiceResponse.Result documents = GoPubMedServiceSingleton.getService().getDocuments(questionText);
      if(documents!=null && documents.getDocuments()!=null && !documents.getDocuments().isEmpty())
      {
        int rank = 0;
        for(Document document:documents.getDocuments())
        {
          doc = new edu.cmu.lti.oaqa.type.retrieval.Document(aJCas);
          List<MeshAnnotation> meshAnnotations = document.getMeshAnnotations();
          System.out.println(document.getTitle());
          /*for(MeshAnnotation annotation:meshAnnotations)
          {
            System.out.println(annotation.getTermLabel());
            System.out.println(annotation.getUri());
          }*/
          doc.setDocId(document.getPmid());
          doc.setText(document.getDocumentAbstract());
          doc.setUri(document.getPmid());
          doc.setRank(rank++);
          doc.addToIndexes();
        }
      }
    }
  }
  

}
