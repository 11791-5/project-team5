package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import util.GoPubMedServiceSingleton;
import util.Utils;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.SynSet;

public class ConceptAnnotator extends JCasAnnotator_ImplBase{

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> questions = aJCas.getAnnotationIndex(ExpandedQuestion.type).iterator();
    while (questions.hasNext()) 
    {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();
      ArrayList<SynSet> synSets = Utils.fromFSListToCollection(question.getSynSets(), SynSet.class);
      String questionText = Utils.getQueryTokens(synSets);
      //System.out.println(question.getQuestionType() + " "+questionText);
      ConceptSearchResult conceptSearchResult;
      List<Integer> ontologies = new ArrayList<Integer>();
      //ontologies.add(GoPubMedServiceSingleton.ALL_ONTOLOGIES);

      ontologies.add(GoPubMedServiceSingleton.DISEASE_ONTOLOGY);
      ontologies.add(GoPubMedServiceSingleton.UNIT_PRO_ONTOLOGY);
      ontologies.add(GoPubMedServiceSingleton.JOCHEM_ONTOLOGY);
      ontologies.add(GoPubMedServiceSingleton.MESH_ONTOLOGY);
      List<OntologyServiceResponse.Result> resultList = GoPubMedServiceSingleton.getService().getConcepts(questionText,ontologies);
      int rank = 0;
      int threshold = 1;
      for(OntologyServiceResponse.Result resultResponse:resultList)
      {
        int concept_per_ontotlogy = 0;
        for (OntologyServiceResponse.Finding finding : resultResponse.getFindings())
        {
          if(concept_per_ontotlogy>=threshold)
            break;
          conceptSearchResult = new ConceptSearchResult(aJCas);
          Concept concept = new Concept(aJCas);
          concept.setName(finding.getConcept().getLabel());
          conceptSearchResult.setConcept(concept);
          conceptSearchResult.setUri(finding.getConcept().getUri());
          conceptSearchResult.setRank(rank++);
          conceptSearchResult.addToIndexes();
          concept_per_ontotlogy++;
        }
      }

    }
  }
}
