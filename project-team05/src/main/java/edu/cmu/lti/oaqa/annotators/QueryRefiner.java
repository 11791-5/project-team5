package edu.cmu.lti.oaqa.annotators;

import edu.cmu.lti.oaqa.type.retrieval.SynSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyStringList;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyStringList;
import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;

import util.AnswerExtractor;
import util.Utils;
import edu.cmu.lti.oaqa.bio.umls_wrapper.TermRelationship;
import edu.cmu.lti.oaqa.resources.UmlsSingleton;
import edu.cmu.lti.oaqa.type.answer.Answer;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult;

public class QueryRefiner extends JCasAnnotator_ImplBase {

  /**
   * For the given question, add the top exact answer to the
   * question and remove all previous document/snippet/exact answer
   * results.
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    FSIterator questions = aJCas.getAnnotationIndex(ExpandedQuestion.type).iterator();
    while (questions.hasNext()) {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();
      FSIterator<TOP> answers = aJCas.getJFSIndexRepository().getAllIndexedFS(Answer.type);
      FSList synsets = question.getSynSets();
      Collection<SynSet> synSetCollection = Utils.fromFSListToCollection(synsets, SynSet.class);
      if (answers.hasNext()) {
        Answer nextAnswer = (Answer) answers.next();
        SynSet additionalSynSet = new SynSet(aJCas);
        additionalSynSet.setOriginalToken(nextAnswer.getText());
        List<String> variantList = Utils.createListFromStringList(nextAnswer.getVariants());
        additionalSynSet.setSynonyms(Utils.fromCollectionToFSList(aJCas, (Collection) variantList));
        synSetCollection.add(additionalSynSet);
      }
      FSList newSynsets = Utils.fromCollectionToFSList(aJCas, synSetCollection);
      question.setSynSets(newSynsets);

      FSIterator<TOP> documentIterator = aJCas.getJFSIndexRepository().getAllIndexedFS(
              edu.cmu.lti.oaqa.type.retrieval.Document.type);
      util.Utils.removeTypeFromIndeces(documentIterator);
      
      FSIterator<TOP> snippetIterator = aJCas.getJFSIndexRepository().getAllIndexedFS(
              SnippetSearchResult.type);
      util.Utils.removeTypeFromIndeces(snippetIterator);

      FSIterator<TOP> exactAnswerIterator = aJCas.getJFSIndexRepository().getAllIndexedFS(
              Answer.type);

      util.Utils.removeTypeFromIndeces(exactAnswerIterator);
    }
  }
}
