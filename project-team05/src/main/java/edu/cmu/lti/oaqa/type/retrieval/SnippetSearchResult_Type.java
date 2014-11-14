
/* First created by JCasGen Fri Nov 14 14:19:41 EST 2014 */
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

/** 
 * Updated by JCasGen Fri Nov 14 16:41:28 EST 2014
 * @generated */
public class SnippetSearchResult_Type extends AnswerSearchResult_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (SnippetSearchResult_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = SnippetSearchResult_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new SnippetSearchResult(addr, SnippetSearchResult_Type.this);
  			   SnippetSearchResult_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new SnippetSearchResult(addr, SnippetSearchResult_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = SnippetSearchResult.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult");
 
  /** @generated */
  final Feature casFeat_snippets;
  /** @generated */
  final int     casFeatCode_snippets;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSnippets(int addr) {
        if (featOkTst && casFeat_snippets == null)
      jcas.throwFeatMissing("snippets", "edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult");
    return ll_cas.ll_getRefValue(addr, casFeatCode_snippets);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSnippets(int addr, int v) {
        if (featOkTst && casFeat_snippets == null)
      jcas.throwFeatMissing("snippets", "edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult");
    ll_cas.ll_setRefValue(addr, casFeatCode_snippets, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public int getSnippets(int addr, int i) {
        if (featOkTst && casFeat_snippets == null)
      jcas.throwFeatMissing("snippets", "edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_snippets), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_snippets), i);
  return ll_cas.ll_getRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_snippets), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setSnippets(int addr, int i, int v) {
        if (featOkTst && casFeat_snippets == null)
      jcas.throwFeatMissing("snippets", "edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult");
    if (lowLevelTypeChecks)
      ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_snippets), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_snippets), i);
    ll_cas.ll_setRefArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_snippets), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public SnippetSearchResult_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_snippets = jcas.getRequiredFeatureDE(casType, "snippets", "uima.cas.FSArray", featOkTst);
    casFeatCode_snippets  = (null == casFeat_snippets) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_snippets).getCode();

  }
}



    