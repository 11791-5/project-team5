
/* First created by JCasGen Fri Nov 14 15:30:40 EST 2014 */
package edu.cmu.lti.oaqa.type.retrieval;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Holds a word and a list of synonyms, where each synonym contains its source and confidence.
 * Updated by JCasGen Fri Nov 14 15:30:40 EST 2014
 * @generated */
public class SynSet_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (SynSet_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = SynSet_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new SynSet(addr, SynSet_Type.this);
  			   SynSet_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new SynSet(addr, SynSet_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = SynSet.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.type.retrieval.SynSet");
 
  /** @generated */
  final Feature casFeat_originalToken;
  /** @generated */
  final int     casFeatCode_originalToken;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getOriginalToken(int addr) {
        if (featOkTst && casFeat_originalToken == null)
      jcas.throwFeatMissing("originalToken", "edu.cmu.lti.oaqa.type.retrieval.SynSet");
    return ll_cas.ll_getStringValue(addr, casFeatCode_originalToken);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOriginalToken(int addr, String v) {
        if (featOkTst && casFeat_originalToken == null)
      jcas.throwFeatMissing("originalToken", "edu.cmu.lti.oaqa.type.retrieval.SynSet");
    ll_cas.ll_setStringValue(addr, casFeatCode_originalToken, v);}
    
  
 
  /** @generated */
  final Feature casFeat_synonyms;
  /** @generated */
  final int     casFeatCode_synonyms;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSynonyms(int addr) {
        if (featOkTst && casFeat_synonyms == null)
      jcas.throwFeatMissing("synonyms", "edu.cmu.lti.oaqa.type.retrieval.SynSet");
    return ll_cas.ll_getRefValue(addr, casFeatCode_synonyms);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSynonyms(int addr, int v) {
        if (featOkTst && casFeat_synonyms == null)
      jcas.throwFeatMissing("synonyms", "edu.cmu.lti.oaqa.type.retrieval.SynSet");
    ll_cas.ll_setRefValue(addr, casFeatCode_synonyms, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public SynSet_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_originalToken = jcas.getRequiredFeatureDE(casType, "originalToken", "uima.cas.String", featOkTst);
    casFeatCode_originalToken  = (null == casFeat_originalToken) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_originalToken).getCode();

 
    casFeat_synonyms = jcas.getRequiredFeatureDE(casType, "synonyms", "uima.cas.FSList", featOkTst);
    casFeatCode_synonyms  = (null == casFeat_synonyms) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_synonyms).getCode();

  }
}



    