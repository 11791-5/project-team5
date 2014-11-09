package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.client.ClientProtocolException;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.PubMedSearchServiceResponse;


public class GoPubMedServiceSingleton 
{
  private static GoPubMedServiceSingleton instance;
  private static final String SERVICE_PROPERTIES = "project.properties";
  private GoPubMedService goPubMedService;

  public GoPubMedService getGoPubMedService() {
    return goPubMedService;
  }

  public void setGoPubMedService(GoPubMedService goPubMedService) {
    this.goPubMedService = goPubMedService;
  }

  public static GoPubMedServiceSingleton getService() 
  {
    if (instance == null)
      try {
        instance = new GoPubMedServiceSingleton();
        instance.setGoPubMedService(new GoPubMedService(GoPubMedServiceSingleton.SERVICE_PROPERTIES));
      } catch (ConfigurationException e) {
        e.printStackTrace();
      }
    return instance;
  } 
  
  public List<OntologyServiceResponse.Result> getConcepts(String text) 
  {
    List<OntologyServiceResponse.Result> resutList = new ArrayList<OntologyServiceResponse.Result>(); 
    try {
      OntologyServiceResponse.Result diseaseOntologyResult = goPubMedService.findDiseaseOntologyEntitiesPaged(text, 0);
      OntologyServiceResponse.Result geneOntologyResult = goPubMedService.findGeneOntologyEntitiesPaged(text,0, 10);    
      OntologyServiceResponse.Result jochemResult = goPubMedService.findJochemEntitiesPaged(text, 0);
      OntologyServiceResponse.Result meshResult = goPubMedService.findMeshEntitiesPaged(text, 0);
      OntologyServiceResponse.Result uniprotResult = goPubMedService.findUniprotEntitiesPaged(text, 0);
      resutList.add(diseaseOntologyResult);
      resutList.add(geneOntologyResult);
      resutList.add(jochemResult);
      resutList.add(meshResult);
      resutList.add(uniprotResult);

    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return resutList;

  }
  public PubMedSearchServiceResponse.Result getDocuments(String text) 
  {
    PubMedSearchServiceResponse.Result pubmedResult = null;
    try {
      pubmedResult = goPubMedService.findPubMedCitations(text, 0);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return pubmedResult;
  }

}
