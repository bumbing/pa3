package edu.stanford.cs276.Scorer;

import edu.stanford.cs276.Config;
import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;

import javax.print.Doc;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Skeleton code for the implementation of a BM25 Scorer in Task 2.
 */
public class BM25Scorer extends AScorer {

  /*
   *  TODO: You will want to tune these values
   */
  double urlweight = Config.urlweight;
  double titleweight  = Config.titleweight;
  double bodyweight = Config.bodyweight;
  double headerweight = Config.headerweight;
  double anchorweight = Config.anchorweight;
  
  // BM25-specific weights
  double burl = Config.burl;
  double btitle = Config.btitle;
  double bheader = Config.bheader;
  double bbody = Config.bbody;
  double banchor = Config.banchor;
  
  double k1 = Config.k1;
  double pageRankLambda = Config.pageRankLambda;
  double pageRankLambdaPrime = Config.pageRankLambdaPrime;
  
  // query -> url -> document
  Map<Query,Map<String, Document>> queryDict;

  // BM25 data structures--feel free to modify these
  // Document -> field -> length
  Map<Document,Map<String,Double>> lengths;  

  // field name -> average length
  Map<String,Double> avgLengths;    

  // Document -> pagerank score
  Map<Document,Double> pagerankScores;
  
    /**
     * Construct a BM25Scorer.
     * @param idfs the map of idf scores
     * @param queryDict a map of query to url to document
     */
    public BM25Scorer(Map<String,Double> idfs, Map<Query,Map<String, Document>> queryDict) {
      super(idfs);
      this.queryDict = queryDict;
      this.calcAverageLengths();
      //System.out.print("DEFAULTSMOOTHVALUE" + DEFAULTSMOOTHVALUE);
    }

    /**
     * Set up average lengths for BM25, also handling PageRank.
     */
  public void calcAverageLengths() {
    lengths = new HashMap<Document,Map<String,Double>>();
    avgLengths = new HashMap<String,Double>();
    pagerankScores = new HashMap<Document,Double>();

    if(queryDict == null) System.out.println("Nothing in queryDict, ERROR!");

    for(Map<String, Document> docs: queryDict.values()) {
      for(Document doc: docs.values()) {
        Map<String, Double> temp = new HashMap<>();
        temp.put("url", doc.url_length());
        temp.put("title", doc.title_length());
        temp.put("header", doc.header_length());
        temp.put("body", (double)doc.body_length);
        temp.put("anchor", doc.anchor_length());
        lengths.put(doc, temp);
        pagerankScores.put(doc, (double)doc.page_rank);
      }
    }

    for (String tfType : this.TFTYPES) {
      double count = 0.0, sum = 0.0;
      for(Map<String, Double> map: lengths.values()) {
        if(map.containsKey(tfType)) {
          count += 1.0;
          sum += map.get(tfType);
        }
      }
      double res = (count == 0.0) ? 0.0 : sum/count;
      //System.out.println("Type: " + tfType + "; average length: " + res);
      avgLengths.put(tfType, res);
    }

  }

  /**
   * Get the net score. 
   * @param tfs the term frequencies
   * @param q the Query 
   * @param tfQuery
   * @param d the Document
   * @return the net score
   */
  public double getNetScore(Map<String,Map<String, Double>> tfs, Query q, Map<String,Double> tfQuery,Document d) {

    double score = 0.0;
    Map<String, Double> termInQuery = q.termCount();
    HashMap<String, Double> idf = new HashMap<>();

    for(Map.Entry<String, Double> map: termInQuery.entrySet()) {
      idf.put(map.getKey(), tfQuery.get(map.getKey()));
    }
    Comparator<String> comparator = new ValueComparator<>(idf);
    TreeMap<String, Double> rare_word_top = new TreeMap<>(comparator);
    rare_word_top.putAll(idf);

    int count = 0;
    for(Map.Entry<String, Double> map: rare_word_top.entrySet()) {
      if(count>=4)  break;


      String word = map.getKey();
      Double w_dt = getWdt(d, word, tfs);
      Double idfs = tfQuery.getOrDefault(word, DEFAULTSMOOTHVALUE);
      score += w_dt *  sublinear(termInQuery.get(map.getKey())) * map.getValue() / (k1+w_dt);
      //score += w_dt * tf * sublinear(map.getValue()) / (k1+w_dt);
      count++;
    }
    //System.out.println(" Doc: " + d.toString());
    //System.out.println("BM25 Score: " + score);
    score += pageRankLambda * PageRankFunction((double)d.page_rank);
    Double pagerank = pageRankLambda * PageRankFunction((double)d.page_rank);
    //System.out.println("Pagerank: " + d.page_rank);
    //System.out.println("Pagerank score: " + pagerank);
    //System.out.println("Total: " + score);
    //.out.println("**************");
    return score;
  }

  private double getWdt(Document d, String word, Map<String,Map<String, Double>> tfs) {
    double score = 0.0, temp;
    score += urlweight * tfs.get("url").getOrDefault(word, 0d);
    score += titleweight * tfs.get("title").getOrDefault(word, 0d);
    score += headerweight * tfs.get("header").getOrDefault(word, 0d);
    score += bodyweight * tfs.get("body").getOrDefault(word, 0d);
    score += anchorweight * tfs.get("anchor").getOrDefault(word, 0d);
    return score;
  }

  private double PageRankFunction(Double pagerank){
    return Math.log(pagerank + pageRankLambdaPrime);
  }

  /**
   * Do BM25 Normalization.
   * @param tfs the term frequencies
   * @param d the Document
   * @param q the Query
   */
  public void normalizeTFs(Map<String,Map<String, Double>> tfs,Document d, Query q) {
    for(Map.Entry<String, Map<String, Double>> docComponents: tfs.entrySet()) {
      String type_name = docComponents.getKey();
      Double temp = 1d;
      switch(type_name) {
        case "url":
          temp += burl * (d.url_length()/avgLengths.get(type_name) - 1);
          break;
        case "title":
          temp += btitle * (d.title_length()/avgLengths.get(type_name) - 1);
          break;
        case "header":
          temp += bheader * (d.header_length()/avgLengths.get(type_name) - 1);
          break;
        case "body":
          temp += bbody * ((double)d.body_length/avgLengths.get(type_name) - 1);
          break;
        case "anchor":
          temp += banchor * (d.anchor_length()/avgLengths.get(type_name) - 1);
          break;
      }
      for(Map.Entry<String, Double> termCount: docComponents.getValue().entrySet()) {
        String st = termCount.getKey();
        Double tf = termCount.getValue();
        //Double tf = sublinear(termCount.getValue());
        tf /= temp;
        docComponents.getValue().put(st, tf);
      }

    }
  }
  
  /**
   * Write the tuned parameters of BM25 to file.
   * Only used for grading purpose, you should NOT modify this method.
   * @param filePath the output file path.
   */
  private void writeParaValues(String filePath) {
    try {
      File file = new File(filePath);
      if (!file.exists()) {
        file.createNewFile();
      }
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      String[] names = {
        "urlweight", "titleweight", "bodyweight", 
        "headerweight", "anchorweight", "burl", "btitle", 
        "bheader", "bbody", "banchor", "k1", "pageRankLambda", "pageRankLambdaPrime"
      };
      double[] values = {
        this.urlweight, this.titleweight, this.bodyweight, 
        this.headerweight, this.anchorweight, this.burl, this.btitle, 
        this.bheader, this.bbody, this.banchor, this.k1, this.pageRankLambda, 
        this.pageRankLambdaPrime
      };
      BufferedWriter bw = new BufferedWriter(fw);
      for (int idx = 0; idx < names.length; ++ idx) {
        bw.write(names[idx] + " " + values[idx]);
        bw.newLine();
      }
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  /**
   * Get the similarity score.
   * @param d the Document
   * @param q the Query
   * @return the similarity score
   */
  public double getSimScore(Document d, Query q) {
    Map<String,Map<String, Double>> tfs = this.getDocTermFreqs(d,q);
    this.normalizeTFs(tfs, d, q);
    Map<String,Double> tfQuery = getQueryFreqs(q);

    // Write out the tuned BM25 parameters
    // This is only used for grading purposes.
    // You should NOT modify the writeParaValues method.
    writeParaValues("bm25Para.txt");
    return getNetScore(tfs,q,tfQuery,d);
  }
  
}
