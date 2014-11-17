

/* First created by JCasGen Fri Nov 14 15:30:39 EST 2014 */
package edu.cmu.lti.oaqa.type.input;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSList;


/** Used to store expanded queries.
 * Updated by JCasGen Fri Nov 14 15:30:39 EST 2014
 * XML source: /root/git/project-team5/project-team05/src/main/resources/type/OAQATypes.xml
 * @generated */
public class ExpandedQuestion extends Question {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ExpandedQuestion.class);
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
  protected ExpandedQuestion() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ExpandedQuestion(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ExpandedQuestion(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ExpandedQuestion(JCas jcas, int begin, int end) {
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
  //* Feature: synSets

  /** getter for synSets - gets List of synonyms for each token in the original query.
   * @generated
   * @return value of the feature 
   */
  public FSList getSynSets() {
    if (ExpandedQuestion_Type.featOkTst && ((ExpandedQuestion_Type)jcasType).casFeat_synSets == null)
      jcasType.jcas.throwFeatMissing("synSets", "edu.cmu.lti.oaqa.type.input.ExpandedQuestion");
    return (FSList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ExpandedQuestion_Type)jcasType).casFeatCode_synSets)));}
    
  /** setter for synSets - sets List of synonyms for each token in the original query. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSynSets(FSList v) {
    if (ExpandedQuestion_Type.featOkTst && ((ExpandedQuestion_Type)jcasType).casFeat_synSets == null)
      jcasType.jcas.throwFeatMissing("synSets", "edu.cmu.lti.oaqa.type.input.ExpandedQuestion");
    jcasType.ll_cas.ll_setRefValue(addr, ((ExpandedQuestion_Type)jcasType).casFeatCode_synSets, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    