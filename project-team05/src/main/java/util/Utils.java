package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.EmptyFSList;
import org.apache.uima.jcas.cas.EmptyStringList;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.NonEmptyFSList;
import org.apache.uima.jcas.cas.NonEmptyStringList;
import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.JCasUtil;

import edu.cmu.lti.oaqa.type.retrieval.Passage;
import edu.cmu.lti.oaqa.type.retrieval.SynSet;

/**
 * Class providing helper methods for information retrieval tasks.
 * 
 * @author mtydykov
 *
 */
public class Utils {
  public static <T extends TOP> ArrayList<T> fromFSListToCollection(FSList list, Class<T> classType) {

    Collection<T> myCollection = JCasUtil.select(list, classType);
    /*
     * for(T element:myCollection){ System.out.println(.getText()); }
     */

    return new ArrayList<T>(myCollection);
  }

  /**
   * Create StringList, given a Collection of Strings.
   * 
   * @param aJCas
   * @param aCollection
   * @return
   */
  public static StringList createStringList(JCas aJCas, Collection<String> aCollection) {
    if (aCollection.size() == 0) {
      return new EmptyStringList(aJCas);
    }

    NonEmptyStringList head = new NonEmptyStringList(aJCas);
    NonEmptyStringList list = head;
    Iterator<String> i = aCollection.iterator();
    while (i.hasNext()) {
      head.setHead(i.next());
      if (i.hasNext()) {
        head.setTail(new NonEmptyStringList(aJCas));
        head = (NonEmptyStringList) head.getTail();
      } else {
        head.setTail(new EmptyStringList(aJCas));
      }
    }

    return list;
  }

  /**
   * Convert collection to FSList.
   * 
   * @param aJCas
   * @param aCollection
   * @return
   */
  public static <T extends Annotation> FSList fromCollectionToFSList(JCas aJCas,
          Collection<T> aCollection) {
    if (aCollection.size() == 0) {
      return new EmptyFSList(aJCas);
    }

    NonEmptyFSList head = new NonEmptyFSList(aJCas);
    NonEmptyFSList list = head;
    Iterator<T> i = aCollection.iterator();
    while (i.hasNext()) {
      head.setHead(i.next());
      if (i.hasNext()) {
        head.setTail(new NonEmptyFSList(aJCas));
        head = (NonEmptyFSList) head.getTail();
      } else {
        head.setTail(new EmptyFSList(aJCas));
      }
    }

    return list;
  }

  public static String getQueryTokens(ArrayList<SynSet> synSets) {
    StringBuffer queryString = new StringBuffer();
    for (SynSet synSet : synSets)
      queryString.append(synSet.getOriginalToken() + " ");
    // ArrayList<Synonym> synonyms =
    // Utils.fromFSListToCollection(synSet.getSynonyms(),Synonym.class);
    return queryString.toString();
  }

  /**
   * Returns a list of document URIs given the JCas.
   * 
   * @param aJcas
   * @return
   */
  public static List<Object> extractUIMATypeAsList(int type, JCas aJcas) {
    FSIterator<TOP> items = aJcas.getJFSIndexRepository().getAllIndexedFS(type);
    List<Object> itemList = new ArrayList<Object>();
    while (items.hasNext()) {
      itemList.add(items.next());
    }
    return itemList;
  }

}
