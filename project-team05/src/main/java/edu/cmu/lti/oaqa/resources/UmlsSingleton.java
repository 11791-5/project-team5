package edu.cmu.lti.oaqa.resources;

import edu.cmu.lti.oaqa.bio.umls_wrapper.UmlsTermsDAO;

public class UmlsSingleton {
  private static UmlsSingleton singleton = null;

  private UmlsTermsDAO umlsService;

  public static synchronized UmlsSingleton getInstance() {
    if (UmlsSingleton.singleton == null) {
      UmlsSingleton.singleton = new UmlsSingleton();
    }
    return UmlsSingleton.singleton;
  }

  public UmlsSingleton() {
    setUmlsService(new UmlsTermsDAO());
  }

  public UmlsTermsDAO getUmlsService() {
    return umlsService;
  }

  public void setUmlsService(UmlsTermsDAO umlsService) {
    this.umlsService = umlsService;
  }
}
