
/* First created by JCasGen Tue Nov 18 13:59:08 EST 2014 */
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
 * Updated by JCasGen Sun Nov 23 23:31:37 EST 2014
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
    
  
 
  /** @generated */
  final Feature casFeat_questionsSyn;
  /** @generated */
  final int     casFeatCode_questionsSyn;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getQuestionsSyn(int addr) {
        if (featOkTst && casFeat_questionsSyn == null)
      jcas.throwFeatMissing("questionsSyn", "edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult");
    return ll_cas.ll_getRefValue(addr, casFeatCode_questionsSyn);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setQuestionsSyn(int addr, int v) {
        if (featOkTst && casFeat_questionsSyn == null)
      jcas.throwFeatMissing("questionsSyn", "edu.cmu.lti.oaqa.type.retrieval.SnippetSearchResult");
    ll_cas.ll_setRefValue(addr, casFeatCode_questionsSyn, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public SnippetSearchResult_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_snippets = jcas.getRequiredFeatureDE(casType, "snippets", "edu.cmu.lti.oaqa.type.retrieval.Passage", featOkTst);
    casFeatCode_snippets  = (null == casFeat_snippets) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_snippets).getCode();

 
    casFeat_questionsSyn = jcas.getRequiredFeatureDE(casType, "questionsSyn", "uima.cas.StringList", featOkTst);
    casFeatCode_questionsSyn  = (null == casFeat_questionsSyn) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_questionsSyn).getCode();

  }
}



    