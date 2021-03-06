package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
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
import edu.cmu.lti.oaqa.type.kb.Triple;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;

/**
 * @author niloyg
 *
 *         Makes a call to the Entrez Gene database to check whether the entity is present in the
 *         gene database. Increases the confidence of the entity if it is present in the gene DB.
 */

public class BioQuestionAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> questions = jcas.getAnnotationIndex(Question.type).iterator();
    while (questions.hasNext()) {
      Question question = (Question) questions.next();
      String questionText = StanfordLemmatizer.stemText(question.getText());
      ConceptSearchResult conceptSearchResult;
      List<Integer> ontologies = new ArrayList<Integer>();
      ontologies.add(1);
      List<OntologyServiceResponse.Result> resultList = GoPubMedServiceSingleton.getService()
              .getConcepts(questionText, ontologies);
      int rank = 0;
      for (OntologyServiceResponse.Result resultResponse : resultList) {
        for (OntologyServiceResponse.Finding finding : resultResponse.getFindings()) {
          conceptSearchResult = new ConceptSearchResult(jcas);
          Concept concept = new Concept(jcas);
          concept.setName(finding.getConcept().getLabel());
          conceptSearchResult.setConcept(concept);
          conceptSearchResult.setUri(finding.getConcept().getUri());
          conceptSearchResult.setRank(rank++);
          conceptSearchResult.addToIndexes();
        }
      }
      edu.cmu.lti.oaqa.type.retrieval.Document doc;
      PubMedSearchServiceResponse.Result documents = GoPubMedServiceSingleton.getService()
              .getDocuments(questionText);
      if (documents != null && documents.getDocuments() != null
              && !documents.getDocuments().isEmpty()) {
        rank = 0;
        for (Document document : documents.getDocuments()) {
          doc = new edu.cmu.lti.oaqa.type.retrieval.Document(jcas);
          doc.setDocId(document.getPmid());
          doc.setUri(document.getPmid());
          doc.setRank(rank++);
          doc.addToIndexes();
        }
      }
      if ("YES_NO".equals(question.getQuestionType())) {
        LinkedLifeDataServiceResponse.Result linkedLifeDataResult = GoPubMedServiceSingleton
                .getService().getTriples(questionText);
        if (linkedLifeDataResult != null) {
          TripleSearchResult tripleSearchResult;
          rank = 0;
          for (LinkedLifeDataServiceResponse.Entity entity : linkedLifeDataResult.getEntities()) {
            for (LinkedLifeDataServiceResponse.Relation relation : entity.getRelations()) {
              tripleSearchResult = new TripleSearchResult(jcas);
              tripleSearchResult.setRank(rank++);
              Triple triple = new Triple(jcas);
              triple.setObject(relation.getObj());
              triple.setSubject(relation.getSubj());
              triple.setPredicate(relation.getPred());
              tripleSearchResult.setTriple(triple);
              tripleSearchResult.addToIndexes();
            }
          }
        }
      }
    }
  }
}
