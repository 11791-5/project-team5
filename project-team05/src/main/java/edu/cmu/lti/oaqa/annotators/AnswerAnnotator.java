package edu.cmu.lti.oaqa.annotators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.tcas.Annotation;

import util.AnswerExtractor;
import util.Utils;
import edu.cmu.lti.oaqa.bio.umls_wrapper.TermRelationship;
import edu.cmu.lti.oaqa.resources.UmlsSingleton;
import edu.cmu.lti.oaqa.type.answer.Answer;
import edu.cmu.lti.oaqa.type.input.ExpandedQuestion;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult;

public class AnswerAnnotator extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> questions = aJCas.getAnnotationIndex(ExpandedQuestion.type).iterator();
    // read each question
    while (questions.hasNext()) {
      ExpandedQuestion question = (ExpandedQuestion) questions.next();
      question.getId();
      List<Object> passageItems = Utils.extractUIMATypeAsList(SnippetSearchResult.type, aJCas);

      ArrayList<String> snippets = new ArrayList<String>();
      StringList strlist = null;
      
      
      for (Object currentSnippet : passageItems) {
        Passage p = ((SnippetSearchResult) currentSnippet).getSnippets();
        strlist = ((SnippetSearchResult) currentSnippet).getQuestionsSyn();

        snippets.add(p.getText());

      }
      List<String> synonym = Utils.createListFromStringList(strlist);

      //get the rank and candidate string of the answer
      Map<Integer, String> hmap = AnswerExtractor.getAnswers(snippets, synonym);

      for (Entry<Integer, String> entry : hmap.entrySet()) {
        Answer ans = new Answer(aJCas);
        ans.setText(entry.getValue().toLowerCase());
        ans.setRank(entry.getKey());
        StringList variants = getAnswerSynonyms(entry.getValue(), aJCas);
        if (variants != null) {
          ans.setVariants(variants);
        }

        ans.addToIndexes();
      }
    }
  }

  /**
   * 
   * Given a string, retrieving the stringlist of synonyms
   * 
   * @param ans
   * @param aJCas
   * @return
   */
  public StringList getAnswerSynonyms(String ans, JCas aJCas) {
    HashSet<String> ansSynonyms = new HashSet<String>();
    try {
      ArrayList<TermRelationship> termRels = UmlsSingleton.getInstance().getUmlsService()
              .getTermSynonyms(ans);
      if (termRels == null)
        return null;
      HashSet<TermRelationship> rels = new HashSet<TermRelationship>(termRels);
      for (TermRelationship rel : rels) {
        ansSynonyms.add(rel.getToTerm().toLowerCase());
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return Utils.createStringList(aJCas, ansSynonyms);
  }

}
