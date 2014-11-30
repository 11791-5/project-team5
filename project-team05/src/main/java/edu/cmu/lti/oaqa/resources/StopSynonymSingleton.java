package edu.cmu.lti.oaqa.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

import edu.cmu.lti.oaqa.reader.CollectionReader;
/**
 * Singleton for the Stop Synonym 
 *
 */
public class StopSynonymSingleton {
  // Singleton for the stop word list
  HashSet<String> stopWordSet = new HashSet<String>();
  private static StopSynonymSingleton singleton;

  private File stopWordList;
  
  public static synchronized StopSynonymSingleton getInstance() {
    if(StopSynonymSingleton.singleton == null) {
      StopSynonymSingleton.singleton = new StopSynonymSingleton();
    }
    return StopSynonymSingleton.singleton;
  }
  
  public StopSynonymSingleton() {
    stopWordList = new File(CollectionReader.STOP_SYNONYM_FILE_NAME);
    Scanner reader = null;
    try {
      reader = new Scanner(stopWordList);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    while (reader.hasNext()) {
      String line = reader.nextLine();
      if (!line.isEmpty()) {
        stopWordSet.add(line.trim());
      }
    }
  }
  
  public boolean isStopWord(String s) {
    for(String word: stopWordSet) {
      if(s.equals(word)) {
        return true;
      }
    }
    return false;
  }
}
