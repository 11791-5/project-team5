

/* First created by JCasGen Tue Nov 18 13:59:08 EST 2014 */
package edu.cmu.lti.oaqa.type.retrieval;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Tue Nov 18 13:59:08 EST 2014
 * XML source: /Users/chaohunc/git/project-team5dd/project-team05/src/main/resources/type/OAQATypes.xml
 * @generated */
public class SnippetSearchResult extends AnswerSearchResult {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SnippetSearchResult.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected SnippetSearchResult() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public SnippetSearchResult(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public SnippetSearchResult(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: snippets

  /** getter for snippets - gets 
   * @generated
   * @return value of the feature 
   */
  public Passage getSnippets() {
    if (SnippetSearchResult_Type.featOkTst && ((SnippetSearchResult_Type)jcasType).casFeat_snippets == null)
      jcasType.jcas.throwFeatMissing("snippets", "edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult");
    return (Passage)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((SnippetSearchResult_Type)jcasType).casFeatCode_snippets)));}
    
  /** setter for snippets - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSnippets(Passage v) {
    if (SnippetSearchResult_Type.featOkTst && ((SnippetSearchResult_Type)jcasType).casFeat_snippets == null)
      jcasType.jcas.throwFeatMissing("snippets", "edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult");
    jcasType.ll_cas.ll_setRefValue(addr, ((SnippetSearchResult_Type)jcasType).casFeatCode_snippets, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    