
/* First created by JCasGen Sun Nov 09 14:50:05 EST 2014 */
package edu.cmu.lti.oaqa.type.kb;

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

/** Class for storing the Document Pmid and URI
 * Updated by JCasGen Fri Nov 14 15:30:39 EST 2014
 * @generated */
public class DocumentP_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DocumentP_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DocumentP_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DocumentP(addr, DocumentP_Type.this);
  			   DocumentP_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DocumentP(addr, DocumentP_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = DocumentP.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.cmu.lti.oaqa.type.kb.DocumentP");
 
  /** @generated */
  final Feature casFeat_Pmid;
  /** @generated */
  final int     casFeatCode_Pmid;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPmid(int addr) {
        if (featOkTst && casFeat_Pmid == null)
      jcas.throwFeatMissing("Pmid", "edu.cmu.lti.oaqa.type.kb.DocumentP");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Pmid);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPmid(int addr, String v) {
        if (featOkTst && casFeat_Pmid == null)
      jcas.throwFeatMissing("Pmid", "edu.cmu.lti.oaqa.type.kb.DocumentP");
    ll_cas.ll_setStringValue(addr, casFeatCode_Pmid, v);}
    
  
 
  /** @generated */
  final Feature casFeat_URI;
  /** @generated */
  final int     casFeatCode_URI;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getURI(int addr) {
        if (featOkTst && casFeat_URI == null)
      jcas.throwFeatMissing("URI", "edu.cmu.lti.oaqa.type.kb.DocumentP");
    return ll_cas.ll_getStringValue(addr, casFeatCode_URI);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setURI(int addr, String v) {
        if (featOkTst && casFeat_URI == null)
      jcas.throwFeatMissing("URI", "edu.cmu.lti.oaqa.type.kb.DocumentP");
    ll_cas.ll_setStringValue(addr, casFeatCode_URI, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public DocumentP_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_Pmid = jcas.getRequiredFeatureDE(casType, "Pmid", "uima.cas.String", featOkTst);
    casFeatCode_Pmid  = (null == casFeat_Pmid) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Pmid).getCode();

 
    casFeat_URI = jcas.getRequiredFeatureDE(casType, "URI", "uima.cas.String", featOkTst);
    casFeatCode_URI  = (null == casFeat_URI) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_URI).getCode();

  }
}



    