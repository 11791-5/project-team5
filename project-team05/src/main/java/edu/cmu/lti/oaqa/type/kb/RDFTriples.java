

/* First created by JCasGen Sun Nov 09 14:55:39 EST 2014 */
package edu.cmu.lti.oaqa.type.kb;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Class for storing the RDF Triples for label
 * Updated by JCasGen Fri Nov 14 15:30:39 EST 2014
 * XML source: /root/git/project-team5/project-team05/src/main/resources/type/OAQATypes.xml
 * @generated */
public class RDFTriples extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(RDFTriples.class);
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
  protected RDFTriples() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public RDFTriples(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public RDFTriples(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public RDFTriples(JCas jcas, int begin, int end) {
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
  //* Feature: label

  /** getter for label - gets Search Label
   * @generated
   * @return value of the feature 
   */
  public String getLabel() {
    if (RDFTriples_Type.featOkTst && ((RDFTriples_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "edu.cmu.lti.oaqa.type.kb.RDFTriples");
    return jcasType.ll_cas.ll_getStringValue(addr, ((RDFTriples_Type)jcasType).casFeatCode_label);}
    
  /** setter for label - sets Search Label 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLabel(String v) {
    if (RDFTriples_Type.featOkTst && ((RDFTriples_Type)jcasType).casFeat_label == null)
      jcasType.jcas.throwFeatMissing("label", "edu.cmu.lti.oaqa.type.kb.RDFTriples");
    jcasType.ll_cas.ll_setStringValue(addr, ((RDFTriples_Type)jcasType).casFeatCode_label, v);}    
   
    
  //*--------------*
  //* Feature: predicate

  /** getter for predicate - gets Predicate
   * @generated
   * @return value of the feature 
   */
  public String getPredicate() {
    if (RDFTriples_Type.featOkTst && ((RDFTriples_Type)jcasType).casFeat_predicate == null)
      jcasType.jcas.throwFeatMissing("predicate", "edu.cmu.lti.oaqa.type.kb.RDFTriples");
    return jcasType.ll_cas.ll_getStringValue(addr, ((RDFTriples_Type)jcasType).casFeatCode_predicate);}
    
  /** setter for predicate - sets Predicate 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPredicate(String v) {
    if (RDFTriples_Type.featOkTst && ((RDFTriples_Type)jcasType).casFeat_predicate == null)
      jcasType.jcas.throwFeatMissing("predicate", "edu.cmu.lti.oaqa.type.kb.RDFTriples");
    jcasType.ll_cas.ll_setStringValue(addr, ((RDFTriples_Type)jcasType).casFeatCode_predicate, v);}    
   
    
  //*--------------*
  //* Feature: subject

  /** getter for subject - gets Subject
   * @generated
   * @return value of the feature 
   */
  public String getSubject() {
    if (RDFTriples_Type.featOkTst && ((RDFTriples_Type)jcasType).casFeat_subject == null)
      jcasType.jcas.throwFeatMissing("subject", "edu.cmu.lti.oaqa.type.kb.RDFTriples");
    return jcasType.ll_cas.ll_getStringValue(addr, ((RDFTriples_Type)jcasType).casFeatCode_subject);}
    
  /** setter for subject - sets Subject 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSubject(String v) {
    if (RDFTriples_Type.featOkTst && ((RDFTriples_Type)jcasType).casFeat_subject == null)
      jcasType.jcas.throwFeatMissing("subject", "edu.cmu.lti.oaqa.type.kb.RDFTriples");
    jcasType.ll_cas.ll_setStringValue(addr, ((RDFTriples_Type)jcasType).casFeatCode_subject, v);}    
   
    
  //*--------------*
  //* Feature: object

  /** getter for object - gets Object
   * @generated
   * @return value of the feature 
   */
  public String getObject() {
    if (RDFTriples_Type.featOkTst && ((RDFTriples_Type)jcasType).casFeat_object == null)
      jcasType.jcas.throwFeatMissing("object", "edu.cmu.lti.oaqa.type.kb.RDFTriples");
    return jcasType.ll_cas.ll_getStringValue(addr, ((RDFTriples_Type)jcasType).casFeatCode_object);}
    
  /** setter for object - sets Object 
   * @generated
   * @param v value to set into the feature 
   */
  public void setObject(String v) {
    if (RDFTriples_Type.featOkTst && ((RDFTriples_Type)jcasType).casFeat_object == null)
      jcasType.jcas.throwFeatMissing("object", "edu.cmu.lti.oaqa.type.kb.RDFTriples");
    jcasType.ll_cas.ll_setStringValue(addr, ((RDFTriples_Type)jcasType).casFeatCode_object, v);}    
  }

    