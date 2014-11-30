package util;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.uima.fit.util.FSCollectionFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.google.common.collect.Range;
 
import edu.cmu.lti.oaqa.type.answer.Answer;
import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.nlp.Token;
import edu.cmu.lti.oaqa.type.retrieval.AbstractQuery;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;
import edu.cmu.lti.oaqa.type.retrieval.Document;
import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.QueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.SearchResult;
import edu.cmu.lti.oaqa.type.retrieval.TripleSearchResult;

public class TypeUtil {

  /**
   * Get question
   * @param jcas
   * @return
   */
  public static Question getQuestion(JCas jcas) {
    return JCasUtil.selectSingle(jcas, Question.class);
  }

  /**
   * Get ordered tokens
   * @param jcas
   * @return
   */
  public static List<Token> getOrderedTokens(JCas jcas) {
    return JCasUtil.select(jcas, Token.class).stream()
            .sorted(Comparator.comparing(Token::getBegin)).collect(toList());
  }

  /**
   * Get abstract queries
   * @param jcas
   * @return
   */
  public static Collection<AbstractQuery> getAbstractQueries(JCas jcas) {
    return JCasUtil.select(jcas, AbstractQuery.class);
  }

  /**
   * Get Abstract queries combined
   * @param jcas
   * @return
   */
  public static AbstractQuery getAbstractQueriesCombined(JCas jcas) {
    List<QueryConcept> conceptsCombined = getAbstractQueries(jcas).stream()
            .flatMap(a -> FSCollectionFactory.create(a.getConcepts(), QueryConcept.class).stream())
            .collect(toList());
    return TypeFactory.createAbstractQuery(jcas, conceptsCombined);
  }

  public static final Comparator<SearchResult> SEARCH_RESULT_SCORE_COMPARATOR = Comparator
          .comparing(SearchResult::getScore).reversed();

  /**
   * Rank search results by score
   * @param results
   * @param hitSize
   * @return
   */
  public static <T extends SearchResult> List<T> rankedSearchResultsByScore(Collection<T> results,
          int hitSize) {
    List<T> sorted = results.stream().sorted(SEARCH_RESULT_SCORE_COMPARATOR).limit(hitSize)
            .collect(toList());
    IntStream.range(0, sorted.size()).forEach(rank -> sorted.get(rank).setRank(rank));
    return sorted;
  }

  public static final Comparator<SearchResult> SEARCH_RESULT_RANK_COMPARATOR = Comparator
          .comparing(SearchResult::getRank);

  /**
   * Rank search results by rank
   * @param results
   * @return
   */
  private static <T extends SearchResult> Collection<T> rankedSearchResultsByRank(
          Collection<T> results) {
    return results.stream().sorted(SEARCH_RESULT_RANK_COMPARATOR)
            .collect(toCollection(ArrayList::new));
  }

  /**
   * Get ranked ConceptSearchResults
   * @param jcas
   * @return
   */
  public static Collection<ConceptSearchResult> getRankedConceptSearchResults(JCas jcas) {
    return rankedSearchResultsByRank(JCasUtil.select(jcas, ConceptSearchResult.class));
  }

  /**
   * Get ranked triples search results
   * @param jcas
   * @return
   */
  public static Collection<TripleSearchResult> getRankedTripleSearchResults(JCas jcas) {
    return rankedSearchResultsByRank(JCasUtil.select(jcas, TripleSearchResult.class));
  }

  /**
   * Get ranked documents
   * @param jcas
   * @return
   */
  public static Collection<Document> getRankedDocuments(JCas jcas) {
    return rankedSearchResultsByRank(JCasUtil.select(jcas, Document.class));
  }

  /**
   * Get ranked passages
   * @param jcas
   * @return
   */
  public static Collection<Passage> getRankedPassages(JCas jcas) {
    return rankedSearchResultsByRank(JCasUtil.select(jcas, Passage.class));
  }

  /**
   * get answers
   * @param jcas
   * @return
   */
  public static Collection<Answer> getAnswers(JCas jcas) {
    return JCasUtil.select(jcas, Answer.class);
  }

  /**
   * Span range
   * @param annotation
   * @return
   */
  public static Range<Integer> spanRange(Annotation annotation) {
    return Range.closedOpen(annotation.getBegin(), annotation.getEnd());
  }

  /**
   * Span range in section
   * @param passage
   * @return
   */
  public static Range<Integer> spanRangeInSection(Passage passage) {
    return Range.closedOpen(passage.getOffsetInBeginSection(), passage.getOffsetInEndSection());
  }

}
