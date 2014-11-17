package edu.cmu.lti.oaqa.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

import edu.cmu.lti.oaqa.reader.CollectionReader;

public class StopWordSingleton {
  HashSet<String> stopWordSet = new HashSet<String>();
  private static StopWordSingleton singleton;

  private File stopWordList;
  
  public static synchronized StopWordSingleton getInstance() {
    if(StopWordSingleton.singleton == null) {
      StopWordSingleton.singleton = new StopWordSingleton();
    }
    return StopWordSingleton.singleton;
  }
  
  public StopWordSingleton() {
    stopWordList = new File(CollectionReader.STOP_WORD_FILE_NAME);
    Scanner reader = null;
    try {
      reader = new Scanner(stopWordList);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
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
