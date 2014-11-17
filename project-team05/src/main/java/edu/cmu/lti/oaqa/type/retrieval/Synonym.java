

/* First created by JCasGen Fri Nov 14 15:30:40 EST 2014 */
package edu.cmu.lti.oaqa.type.retrieval;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Contains a synonym, its source, and its confidence.
 * Updated by JCasGen Fri Nov 14 15:30:40 EST 2014
 * XML source: /root/git/project-team5/project-team05/src/main/resources/type/OAQATypes.xml
 * @generated */
public class Synonym extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Synonym.class);
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
  protected Synonym() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Synonym(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Synonym(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Synonym(JCas jcas, int begin, int end) {
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
  //* Feature: synonym

  /** getter for synonym - gets Synonym text.
   * @generated
   * @return value of the feature 
   */
  public String getSynonym() {
    if (Synonym_Type.featOkTst && ((Synonym_Type)jcasType).casFeat_synonym == null)
      jcasType.jcas.throwFeatMissing("synonym", "edu.cmu.lti.oaqa.type.retrieval.Synonym");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Synonym_Type)jcasType).casFeatCode_synonym);}
    
  /** setter for synonym - sets Synonym text. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSynonym(String v) {
    if (Synonym_Type.featOkTst && ((Synonym_Type)jcasType).casFeat_synonym == null)
      jcasType.jcas.throwFeatMissing("synonym", "edu.cmu.lti.oaqa.type.retrieval.Synonym");
    jcasType.ll_cas.ll_setStringValue(addr, ((Synonym_Type)jcasType).casFeatCode_synonym, v);}    
   
    
  //*--------------*
  //* Feature: confidence

  /** getter for confidence - gets Synonym confidence.
   * @generated
   * @return value of the feature 
   */
  public double getConfidence() {
    if (Synonym_Type.featOkTst && ((Synonym_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "edu.cmu.lti.oaqa.type.retrieval.Synonym");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Synonym_Type)jcasType).casFeatCode_confidence);}
    
  /** setter for confidence - sets Synonym confidence. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setConfidence(double v) {
    if (Synonym_Type.featOkTst && ((Synonym_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "edu.cmu.lti.oaqa.type.retrieval.Synonym");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Synonym_Type)jcasType).casFeatCode_confidence, v);}    
   
    
  //*--------------*
  //* Feature: source

  /** getter for source - gets Synonym source.
   * @generated
   * @return value of the feature 
   */
  public String getSource() {
    if (Synonym_Type.featOkTst && ((Synonym_Type)jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "edu.cmu.lti.oaqa.type.retrieval.Synonym");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Synonym_Type)jcasType).casFeatCode_source);}
    
  /** setter for source - sets Synonym source. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSource(String v) {
    if (Synonym_Type.featOkTst && ((Synonym_Type)jcasType).casFeat_source == null)
      jcasType.jcas.throwFeatMissing("source", "edu.cmu.lti.oaqa.type.retrieval.Synonym");
    jcasType.ll_cas.ll_setStringValue(addr, ((Synonym_Type)jcasType).casFeatCode_source, v);}    
  }

    