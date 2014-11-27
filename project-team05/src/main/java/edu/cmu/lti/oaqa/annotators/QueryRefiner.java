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

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    // TODO Auto-generated method stub
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
      List<edu.cmu.lti.oaqa.type.retrieval.Document> docsToRemove = new ArrayList<edu.cmu.lti.oaqa.type.retrieval.Document>();
      while (documentIterator.hasNext()) {
        edu.cmu.lti.oaqa.type.retrieval.Document currentDoc = (edu.cmu.lti.oaqa.type.retrieval.Document) documentIterator
                .next();
        docsToRemove.add(currentDoc);
      }
      for (edu.cmu.lti.oaqa.type.retrieval.Document doc : docsToRemove) {
        doc.removeFromIndexes();
      }

      FSIterator<TOP> snippetIterator = aJCas.getJFSIndexRepository().getAllIndexedFS(
              SnippetSearchResult.type);
      List<SnippetSearchResult> snippetsToRemove = new ArrayList<SnippetSearchResult>();
      while (snippetIterator.hasNext()) {
        SnippetSearchResult currentSnippet = (SnippetSearchResult) snippetIterator.next();
        snippetsToRemove.add(currentSnippet);
      }

      for (SnippetSearchResult snippet : snippetsToRemove) {
        snippet.removeFromIndexes();
      }

      FSIterator<TOP> exactAnswerIterator = aJCas.getJFSIndexRepository().getAllIndexedFS(
              Answer.type);

      List<Answer> answersToRemove = new ArrayList<Answer>();
      while (exactAnswerIterator.hasNext()) {
        Answer currentAnswer = (Answer) exactAnswerIterator.next();
        answersToRemove.add(currentAnswer);
      }

      for (Answer answer : answersToRemove) {
        answer.removeFromIndexes();
      }
    }
  }
}
