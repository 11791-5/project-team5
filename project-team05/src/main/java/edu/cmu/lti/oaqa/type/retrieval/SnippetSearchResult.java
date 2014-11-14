

/* First created by JCasGen Fri Nov 14 14:19:41 EST 2014 */
package edu.cmu.lti.oaqa.type.retrieval;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Fri Nov 14 14:20:37 EST 2014
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
  //* Feature: snippet

  /** getter for snippet - gets 
   * @generated
   * @return value of the feature 
   */
  public Passage getSnippet() {
    if (SnippetSearchResult_Type.featOkTst && ((SnippetSearchResult_Type)jcasType).casFeat_snippet == null)
      jcasType.jcas.throwFeatMissing("snippet", "edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult");
    return (Passage)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((SnippetSearchResult_Type)jcasType).casFeatCode_snippet)));}
    
  /** setter for snippet - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSnippet(Passage v) {
    if (SnippetSearchResult_Type.featOkTst && ((SnippetSearchResult_Type)jcasType).casFeat_snippet == null)
      jcasType.jcas.throwFeatMissing("snippet", "edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult");
    jcasType.ll_cas.ll_setRefValue(addr, ((SnippetSearchResult_Type)jcasType).casFeatCode_snippet, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    