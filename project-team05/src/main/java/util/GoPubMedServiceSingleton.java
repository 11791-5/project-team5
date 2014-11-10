package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.client.ClientProtocolException;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.LinkedLifeDataServiceResponse;
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
      OntologyServiceResponse.Result diseaseOntologyResult = goPubMedService
              .findDiseaseOntologyEntitiesPaged(text, 0);
      resutList.add(diseaseOntologyResult);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    OntologyServiceResponse.Result geneOntologyResult;
    try {
      geneOntologyResult = goPubMedService.findGeneOntologyEntitiesPaged(text, 0, 10);
      resutList.add(geneOntologyResult);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

   /* OntologyServiceResponse.Result jochemResult;
    try {
      jochemResult = goPubMedService.findJochemEntitiesPaged(text, 0);
      resutList.add(jochemResult);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }*/

    OntologyServiceResponse.Result meshResult;
    try {
      meshResult = goPubMedService.findMeshEntitiesPaged(text, 0);
      resutList.add(meshResult);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    OntologyServiceResponse.Result uniprotResult;
    try {
      uniprotResult = goPubMedService.findUniprotEntitiesPaged(text, 0);
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

  public LinkedLifeDataServiceResponse.Result getTriples(String questionText) {
    LinkedLifeDataServiceResponse.Result linkedLifeDataResult = null;
    try {
      linkedLifeDataResult = goPubMedService
              .findLinkedLifeDataEntitiesPaged(questionText, 0);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return linkedLifeDataResult;
  }

}
