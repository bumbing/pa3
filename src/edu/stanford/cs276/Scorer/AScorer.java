package edu.stanford.cs276.Scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * An abstract class for a scorer. 
 * Needs to be extended by each specific implementation of scorers.
 */
public abstract class AScorer {

  // Map: term -> idf
  Map<String,Double> idfs; 
  final Double DEFAULTSMOOTHVALUE;

  // Various types of term frequencies that you will need
  String[] TFTYPES = {"url","title","body","header","anchor"};
  
  /**
   * Construct an abstract scorer with a map of idfs.
   * @param idfs the map of idf scores
   */
  public AScorer(Map<String,Double> idfs) {
    this.idfs = idfs;
    DEFAULTSMOOTHVALUE = (idfs==null) ? 1.0 : idfs.size()+1.0;
    //DEFAULTSMOOTHVALUE = 1.0;
  }

  /**
  * You can implement your own function to whatever you want for debug string
  * The following is just an example to include page information in the debug string
  * The string will be forced to be 1-line and truncated to only include the first 200 characters
  */
  public String getDebugStr(Document d, Query q)
  {
    return "Pagerank: " + Integer.toString(d.page_rank);
  }
  
    /**
     * Score each document for each query.
     * @param d the Document
     * @param q the Query
     */
  public abstract double getSimScore(Document d, Query q);
  
  /**
   * Get frequencies for a query.
   * @param q the query to compute frequencies for
   */
  public Map<String,Double> getQueryFreqs(Query q) {

    // queryWord -> term frequency
    Map<String,Double> tfQuery = new HashMap<String, Double>();     
    //System.out.println("DEFAULTSMOOTHVALUE: " + DEFAULTSMOOTHVALUE);
    Map<String,Double> termCount = q.termCount();
    for(Map.Entry<String,Double> termAndCount: termCount.entrySet()) {
      String term = termAndCount.getKey();
      // Use sublinear for termAndCount.getValue().
      // Here terms in query are all present, i.e term count >= 1, but the idf may return 0 since the collection may not
      // have the word, so smooth to 1.
      //tfQuery.put(term, 1.0 * sublinear(termAndCount.getValue()) * idfs.getOrDefault(term, DEFAULTSMOOTHVALUE));
      //System.out.println("sublinear(termAndCount.getValue())" + sublinear(termAndCount.getValue()));
      //System.out.println("idfs.getOrDefault(term, DEFAULTSMOOTHVALUE)" + idfs.getOrDefault(term, DEFAULTSMOOTHVALUE));
      tfQuery.put(term, 1.0 * termAndCount.getValue() * idfs.getOrDefault(term, DEFAULTSMOOTHVALUE));
    }

    return tfQuery;
  }

  private double sublinear(double count) {
    if(count < 0.0){
      System.out.println("The count is smaller than 0.");
    }
    if(count == 0.0)  return 0.0;
    else return 1+Math.log(count);
  }

  /*
   * TODO : Your code here
   * Include any initialization and/or parsing methods
   * that you may want to perform on the Document fields
   * prior to accumulating counts.
   * See the Document class in Document.java to see how
   * the various fields are represented.
   */

  
  /**
   * Accumulate the various kinds of term frequencies 
   * for the fields (url, title, body, header, and anchor).
   * You can override this if you'd like, but it's likely 
   * that your concrete classes will share this implementation.
   * @param d the Document
   * @param q the Query
   */
  public Map<String,Map<String, Double>> getDocTermFreqs(Document d, Query q) {

    // Map from tf type -> queryWord -> score
    Map<String,Map<String, Double>> tfs = new HashMap<String,Map<String, Double>>();
    tfs.put("url", new HashMap<String, Double>());
    tfs.put("title", new HashMap<String, Double>());
    tfs.put("header", new HashMap<String, Double>());

    // Handle duplicate words, here use continue, but in anchor/body, just use hashset.
    for (String queryWord : q.termCount().keySet()) {
      tfs.get("url").put(queryWord, docFreq("url", queryWord, d));
      tfs.get("title").put(queryWord, docFreq("title", queryWord, d));
      tfs.get("header").put(queryWord, docFreq("header", queryWord, d));
    }

    Map<String, Double> anchor_count = d.count_anchor(q.termCount());
    tfs.put("anchor", anchor_count);
    Map<String, Double> body_count = d.count_body(q.termCount());
    tfs.put("body", body_count);
    return tfs;
  }


  private Double docFreq(String part, String queryword, Document d) {
    Double count = 0.0;
    switch(part) {
      case "url":
        count = d.count_url(queryword);
        break;
      case "title":
        count = d.count_title(queryword);
        break;
      case "header":
        count = d.count_header(queryword);
        break;
    }
    return count;
  }

}
