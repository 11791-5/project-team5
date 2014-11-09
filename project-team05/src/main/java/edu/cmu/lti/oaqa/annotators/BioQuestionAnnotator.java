package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.fit.util.FSCollectionFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import util.GoPubMedServiceSingleton;
import util.StanfordLemmatizer;
import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse.Document;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse.MeshAnnotation;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.kb.ConceptMention;
import edu.cmu.lti.oaqa.type.kb.DocumentP;
import edu.cmu.lti.oaqa.type.kb.RDFTriples;



/**
 * @author niloyg
 *
 *Makes a call to the Entrez Gene database to check whether the entity 
 *is present in the gene database. Increases the confidence of the entity if it is present in the gene DB.
 */

public class BioQuestionAnnotator extends JCasAnnotator_ImplBase {
  
  @Override
  public void process( JCas jcas ) throws AnalysisEngineProcessException {  
    FSIterator<Annotation> questions = jcas.getAnnotationIndex(Question.type).iterator();
    while (questions.hasNext()) 
    {
      Question question = (Question) questions.next();
      //System.out.println(question.getQuestionType() + " "+question.getText());
      String questionText = StanfordLemmatizer.stemText(question.getText());
      System.out.println(question.getQuestionType() + " "+questionText);
      HashMap<String,List<String>> conceptMap = new HashMap<String,List<String>>();
      ConceptMention conceptMention; 
      List<OntologyServiceResponse.Result> resultList = GoPubMedServiceSingleton.getService().getConcepts(questionText);
      for(OntologyServiceResponse.Result resultResponse:resultList)
      {
        for (OntologyServiceResponse.Finding finding : resultResponse.getFindings())
        {
          if(!conceptMap.containsKey(finding.getConcept().getLabel()))
            conceptMap.put(finding.getConcept().getLabel(), new ArrayList<String>());
          conceptMap.get(finding.getConcept().getLabel()).add(finding.getConcept().getUri());

        }
      }
      for(String conceptLabel:conceptMap.keySet())
      {
        conceptMention =  new ConceptMention(jcas);
        Concept concept = new Concept(jcas);
        concept.setName(conceptLabel);
        concept.setUris(FSCollectionFactory.createStringList(jcas, conceptMap.get(conceptLabel)));
        conceptMention.setConcept(concept);
        //conceptMention.addToIndexes();
        jcas.addFsToIndexes(conceptMention);
      }
     // SearchResult searchResult = new SearchResult(jcas);
      DocumentP doc; 
      PubMedSearchServiceResponse.Result documents = GoPubMedServiceSingleton.getService().getDocuments(questionText);
      if(documents!=null && documents.getDocuments()!=null && !documents.getDocuments().isEmpty())
      {
        for(Document document:documents.getDocuments())
        {
          doc = new DocumentP(jcas);
          List<MeshAnnotation> meshAnnotations = document.getMeshAnnotations();
          System.out.println(document.getTitle());
          for(MeshAnnotation annotation:meshAnnotations)
          {
            System.out.println(annotation.getTermLabel());
            System.out.println(annotation.getUri());
          }
          doc.setPmid(document.getPmid());
          jcas.addFsToIndexes(doc);
        }
      }
      if("YES_NO".equals(question.getQuestionType()))
      {
        LinkedLifeDataServiceResponse.Result linkedLifeDataResult = GoPubMedServiceSingleton.getService().getTriples(questionText);
        if(linkedLifeDataResult != null)
        {
          RDFTriples triple;
          for (LinkedLifeDataServiceResponse.Entity entity : linkedLifeDataResult.getEntities()) {
            for (LinkedLifeDataServiceResponse.Relation relation : entity.getRelations()) {
              triple = new RDFTriples(jcas);
              triple.setObject(relation.getObj());
              triple.setSubject(relation.getSubj());
              triple.setPredicate(relation.getPred());
              System.out.println("   - pred: " + relation.getPred());
              System.out.println("   - sub: " + relation.getSubj());
              System.out.println("   - obj: " + relation.getObj());
              jcas.addFsToIndexes(triple);
            }
          }
        }

      }
      
    }
    
  }

}
