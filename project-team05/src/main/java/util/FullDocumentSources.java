package util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import edu.cmu.lti.oaqa.type.retrieval.Document;

/**
 *  Parse response from  http://metal.lti.cs.cmu.edu:30002/pmc/PMID
 * @author larbi
 *
 */
public class FullDocumentSources {


  //static String url = "http://gold.lti.cs.cmu.edu:30002/pmc/";
  static String url = "http://metal.lti.cs.cmu.edu:30002/pmc/";
  static GsonBuilder builder = new GsonBuilder();
  /**
   * Return raw text from document
   * @param doc
   * @return
   * @throws IOException
   */
  public static List<String> getFullText(Document doc) throws IOException{

    String rawText = new String();
    
      String pmid = doc.getDocId();
      String request = url + pmid;

      URL serviceURL = new URL(request);
      InputStream is = serviceURL.openStream();
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String response = br.readLine();
      
      // The service sends one line response only
      // parse it 
      if (response ==null)
          return null;
      InputStream stream = IOUtils.toInputStream(response, "UTF-8");

      JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
      
      Object o = builder.create().fromJson(reader, Object.class);
    
      com.google.gson.internal.LinkedTreeMap<String, List<String>> o2 = (com.google.gson.internal.LinkedTreeMap) o; 

      List<String> text = o2.get("sections");
    
      return text;

    
  }
}


