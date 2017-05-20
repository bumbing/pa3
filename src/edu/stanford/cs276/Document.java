package edu.stanford.cs276;

import edu.stanford.cs276.util.Stemmer;

import java.util.*;

/**
 * The class is used to store useful information for a document. 
 * You can also write the document to a string for debugging.
 */
public class Document {

  public String url = null;
  public String title = null;
  public List<String> headers = null;

  // term -> [list of positions]
  public Map<String, List<Integer>> body_hits = null; 
  public int body_length = 0;
  public int page_rank = 0;

  // anchor text -> anchor count
  // The anchor text could contain multiple words separated
  // by whitespace. You may want to perform some tokenization.
  public Map<String, Integer> anchors = null; 

  // debug string for you to debug your implementation
  public String debugStr = "";

  /** 
   * Constructs a document with a String url.
   * @param url the url associated with a document.
   */
  public Document(String url) {
    this.url=url;
  }

  // Count overall length, if this filed doesn't exist, return 0
  public double url_length() {
    if(url == null) return 0.0;
    return url.split("\\W+").length;
  }

  public double title_length() {
    if(title == null) return 0.0;
    return title.split("\\s+").length;
  }

  public double header_length() {
    if(headers == null || headers.isEmpty())  return 0.0;
    double result = 0.0;
    for(String header: headers) {
      result += header.split("\\s+").length;
    }
    return result;
  }

  public double anchor_length() {
    if(anchors == null) return 0.0;
    double result = 0.0;
    for(Map.Entry<String, Integer> map: anchors.entrySet()) {
      double temp = map.getKey().split("\\s+").length * map.getValue();
    }
    return result;
  }

  // Count term frequency for a particular term.
  public double count_url(String term) {
    double result = 0.0;
    if(url == null) {
      return result;
    }
    // Split all non alphabeta chars.
    for(String word: url.toLowerCase().split("\\W+")) {
      word = getStem(word);
      if(term.equals(word)) result += 1.0;
    }
    return result;
  }

  public double count_title(String term) {
    double result = 0.0;
    if(title == null) {
      return result;
    }
    for(String word: title.toLowerCase().split("\\s+")) {
      word = getStem(word);
      if(term.equals(word)) result += 1.0;
    }
    return result;
  }

  public double count_header(String term) {
    double result = 0.0;
    if(headers == null || headers.isEmpty()) {
      return result;
    }
    for(String header : headers) {
      for(String word: header.toLowerCase().split("\\s+")) {
        word = getStem(word);
        if(term.equals(word)) result += 1.0;
      }
    }
    return result;
  }

  public Map<String, Double> count_anchor(Map<String, Double> queryWords) {
    Map<String, Double> result = new HashMap<>();
    for(String word: queryWords.keySet())  result.put(word, 0.0);
    if(anchors == null) return result;

    for(Map.Entry<String, Integer> entry : anchors.entrySet()) {
      for(String wordInAnchor: entry.getKey().toLowerCase().split("\\s+")) {
        wordInAnchor = getStem(wordInAnchor);
        if(result.containsKey(wordInAnchor)) {
          Double ct = result.get(wordInAnchor) + entry.getValue();
          result.put(wordInAnchor, ct);
        }
      }
    }
    return result;
  }

  public Map<String, Double> count_body(Map<String, Double> queryWords) {
    Map<String, Double> result = new HashMap<>();
    for(String word: queryWords.keySet()) {
      if(body_hits == null || !body_hits.containsKey(word)) {
        result.put(word, 0.0);
      } else {
        result.put(word, (double) body_hits.get(word).size());
      }
    }
    return result;
  }

  private String getStem(String word) {
    Stemmer stemmer = new Stemmer();
    stemmer.add(word.toCharArray(), word.length());
    stemmer.stem();
    return stemmer.toString();
  }

  /**
   * Returns a String representation of a Document.
   * @return the String of fields representing a Document
   */
  public String toString() {
    StringBuilder result = new StringBuilder();
    String NEW_LINE = System.getProperty("line.separator");
    result.append("url: "+ url + NEW_LINE);
    if (title != null) result.append("title: " + title + NEW_LINE);
    if (headers != null) result.append("headers: " + headers.toString() + NEW_LINE);
    if (body_hits != null) result.append("body_hits: " + body_hits.toString() + NEW_LINE);
    if (body_length != 0) result.append("body_length: " + body_length + NEW_LINE);
    if (page_rank != 0) result.append("page_rank: " + page_rank + NEW_LINE);
    if (anchors != null) result.append("anchors: " + anchors.toString() + NEW_LINE);
    return result.toString();
  }
}
