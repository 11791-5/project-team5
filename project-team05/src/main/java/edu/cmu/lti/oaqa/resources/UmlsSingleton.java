package edu.cmu.lti.oaqa.resources;

import edu.cmu.lti.oaqa.bio.umls_wrapper.UmlsTermsDAO;

/**
 * Singleton for the UMLS service
 *
 */
public class UmlsSingleton {
  // Singleton for the umls service
  private static UmlsSingleton singleton = null;

  private UmlsTermsDAO umlsService;

  public static synchronized UmlsSingleton getInstance() {
    if (UmlsSingleton.singleton == null) {
      // create singleton if doesn't exist
      UmlsSingleton.singleton = new UmlsSingleton();
    }
    return UmlsSingleton.singleton;
  }

  public UmlsSingleton() {
    // set the umls service
    setUmlsService(new UmlsTermsDAO());
  }

  
  public UmlsTermsDAO getUmlsService() {
    return umlsService;
  }

  /**
   * Setter for umls service
   * @param umlsService
   */
  public void setUmlsService(UmlsTermsDAO umlsService) {
    this.umlsService = umlsService;
  }
}
