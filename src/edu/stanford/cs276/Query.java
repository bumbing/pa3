package edu.stanford.cs276;

import java.util.*;

/**
 * This class is used to store a query sequence.
 */
public class Query {
  public List<String> queryWords;
  Map<String,Double> termCount;

  /**
   * Constructs a query.
   * @param query the query String.
   */
  public Query(String query) {
    termCount = new HashMap<>();
    queryWords = new ArrayList<String>(Arrays.asList(query.toLowerCase().split("\\s+")));
    for(String word: queryWords) {
      termCount.put(word, termCount.getOrDefault(word, 0.0)+1.0);
    }
  }

  public Map<String,Double> termCount() {
    return termCount;
  }
  /**
   * Returns a String representation of the Query.
   * @return the Query as a String
   */
  public String toString() {
    String str = "";
    for (String word : queryWords) {
      str += word + " ";
    }
    return str.trim();
  }
}
