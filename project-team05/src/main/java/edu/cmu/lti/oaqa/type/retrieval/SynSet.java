

/* First created by JCasGen Fri Nov 14 15:30:40 EST 2014 */
package edu.cmu.lti.oaqa.type.retrieval;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.tcas.Annotation;


/** Holds a word and a list of synonyms, where each synonym contains its source and confidence.
 * Updated by JCasGen Fri Nov 14 15:30:40 EST 2014
 * XML source: /root/git/project-team5/project-team05/src/main/resources/type/OAQATypes.xml
 * @generated */
public class SynSet extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SynSet.class);
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
  protected SynSet() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public SynSet(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public SynSet(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public SynSet(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
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
  //* Feature: originalToken

  /** getter for originalToken - gets The original token from the query.
   * @generated
   * @return value of the feature 
   */
  public String getOriginalToken() {
    if (SynSet_Type.featOkTst && ((SynSet_Type)jcasType).casFeat_originalToken == null)
      jcasType.jcas.throwFeatMissing("originalToken", "edu.cmu.lti.oaqa.type.retrieval.SynSet");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SynSet_Type)jcasType).casFeatCode_originalToken);}
    
  /** setter for originalToken - sets The original token from the query. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setOriginalToken(String v) {
    if (SynSet_Type.featOkTst && ((SynSet_Type)jcasType).casFeat_originalToken == null)
      jcasType.jcas.throwFeatMissing("originalToken", "edu.cmu.lti.oaqa.type.retrieval.SynSet");
    jcasType.ll_cas.ll_setStringValue(addr, ((SynSet_Type)jcasType).casFeatCode_originalToken, v);}    
   
    
  //*--------------*
  //* Feature: synonyms

  /** getter for synonyms - gets Holds the list of synonyms for the original token.
   * @generated
   * @return value of the feature 
   */
  public FSList getSynonyms() {
    if (SynSet_Type.featOkTst && ((SynSet_Type)jcasType).casFeat_synonyms == null)
      jcasType.jcas.throwFeatMissing("synonyms", "edu.cmu.lti.oaqa.type.retrieval.SynSet");
    return (FSList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((SynSet_Type)jcasType).casFeatCode_synonyms)));}
    
  /** setter for synonyms - sets Holds the list of synonyms for the original token. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSynonyms(FSList v) {
    if (SynSet_Type.featOkTst && ((SynSet_Type)jcasType).casFeat_synonyms == null)
      jcasType.jcas.throwFeatMissing("synonyms", "edu.cmu.lti.oaqa.type.retrieval.SynSet");
    jcasType.ll_cas.ll_setRefValue(addr, ((SynSet_Type)jcasType).casFeatCode_synonyms, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    