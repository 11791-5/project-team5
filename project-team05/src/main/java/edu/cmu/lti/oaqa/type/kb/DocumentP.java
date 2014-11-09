

/* First created by JCasGen Sun Nov 09 14:50:05 EST 2014 */
package edu.cmu.lti.oaqa.type.kb;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Class for storing the Document Pmid and URI
 * Updated by JCasGen Sun Nov 09 14:55:39 EST 2014
 * XML source: /home/niloygupta/git/project-team5/project-team05/src/main/resources/type/OAQATypes.xml
 * @generated */
public class DocumentP extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DocumentP.class);
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
  protected DocumentP() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public DocumentP(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public DocumentP(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public DocumentP(JCas jcas, int begin, int end) {
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
  //* Feature: Pmid

  /** getter for Pmid - gets Pmid of the document
   * @generated
   * @return value of the feature 
   */
  public String getPmid() {
    if (DocumentP_Type.featOkTst && ((DocumentP_Type)jcasType).casFeat_Pmid == null)
      jcasType.jcas.throwFeatMissing("Pmid", "edu.cmu.lti.oaqa.type.kb.DocumentP");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentP_Type)jcasType).casFeatCode_Pmid);}
    
  /** setter for Pmid - sets Pmid of the document 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPmid(String v) {
    if (DocumentP_Type.featOkTst && ((DocumentP_Type)jcasType).casFeat_Pmid == null)
      jcasType.jcas.throwFeatMissing("Pmid", "edu.cmu.lti.oaqa.type.kb.DocumentP");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentP_Type)jcasType).casFeatCode_Pmid, v);}    
   
    
  //*--------------*
  //* Feature: URI

  /** getter for URI - gets URI of the document
   * @generated
   * @return value of the feature 
   */
  public String getURI() {
    if (DocumentP_Type.featOkTst && ((DocumentP_Type)jcasType).casFeat_URI == null)
      jcasType.jcas.throwFeatMissing("URI", "edu.cmu.lti.oaqa.type.kb.DocumentP");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentP_Type)jcasType).casFeatCode_URI);}
    
  /** setter for URI - sets URI of the document 
   * @generated
   * @param v value to set into the feature 
   */
  public void setURI(String v) {
    if (DocumentP_Type.featOkTst && ((DocumentP_Type)jcasType).casFeat_URI == null)
      jcasType.jcas.throwFeatMissing("URI", "edu.cmu.lti.oaqa.type.kb.DocumentP");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentP_Type)jcasType).casFeatCode_URI, v);}    
  }

    