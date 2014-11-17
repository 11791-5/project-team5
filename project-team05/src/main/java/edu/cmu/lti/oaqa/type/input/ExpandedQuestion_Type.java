
/* First created by JCasGen Fri Nov 14 15:30:39 EST 2014 */
package edu.cmu.lti.oaqa.type.input;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** Used to store expanded queries.
 * Updated by JCasGen Fri Nov 14 15:30:39 EST 2014
 * @generated */
public class ExpandedQuestion_Type extends Question_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ExpandedQuestion_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ExpandedQuestion_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ExpandedQuestion(addr, ExpandedQuestion_Type.this);
  			   ExpandedQuestion_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ExpandedQuestion(addr, ExpandedQuestion_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ExpandedQuestion.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.type.input.ExpandedQuestion");
 
  /** @generated */
  final Feature casFeat_synSets;
  /** @generated */
  final int     casFeatCode_synSets;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getSynSets(int addr) {
        if (featOkTst && casFeat_synSets == null)
      jcas.throwFeatMissing("synSets", "edu.cmu.lti.oaqa.type.input.ExpandedQuestion");
    return ll_cas.ll_getRefValue(addr, casFeatCode_synSets);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSynSets(int addr, int v) {
        if (featOkTst && casFeat_synSets == null)
      jcas.throwFeatMissing("synSets", "edu.cmu.lti.oaqa.type.input.ExpandedQuestion");
    ll_cas.ll_setRefValue(addr, casFeatCode_synSets, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ExpandedQuestion_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_synSets = jcas.getRequiredFeatureDE(casType, "synSets", "uima.cas.FSList", featOkTst);
    casFeatCode_synSets  = (null == casFeat_synSets) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_synSets).getCode();

  }
}



    