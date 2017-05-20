package edu.stanford.cs276.Scorer;

import edu.stanford.cs276.Config;
import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Skeleton code for the implementation of a 
 * Cosine Similarity Scorer in Task 1.
 */
public class CosineSimilarityScorer extends AScorer {

  /*
   * TODO: You will want to tune the values for
   * the weights for each field.
   */
  double urlweight = Config.urlweight;
  double titleweight  = Config.titleweight;
  double bodyweight = Config.bodyweight;
  double headerweight = Config.headerweight;
  double anchorweight = Config.anchorweight;
  double smoothingBodyLength = Config.smoothingBodyLength;
  
  /**
   * Construct a Cosine Similarity Scorer.
   * @param idfs the map of idf values
   */
  public CosineSimilarityScorer(Map<String,Double> idfs) {
    super(idfs);
  }

  /**
   * Get the net score for a query and a document.
   * @param tfs the term frequencies
   * @param q the Query
   * @param tfQuery the term frequencies for the query
   * @param d the Document
   * @return the net score
   */
  public double getNetScore(Map<String, Map<String, Double>> tfs, Query q, Map<String,Double> tfQuery, Document d) {
    double score = 0.0;
    Map<String, Double> termInQuery = q.termCount();

    for(Map.Entry<String, Double> map: termInQuery.entrySet()) {
      double qv_q = tfQuery.get(map.getKey()) * map.getValue();
      double sum = 0.0;
      sum += urlweight * sublinear(tfs.get("url").getOrDefault(map.getKey(), 0.0));
      sum += titleweight * sublinear(tfs.get("title").getOrDefault(map.getKey(), 0.0));
      sum += bodyweight * sublinear(tfs.get("body").getOrDefault(map.getKey(), 0.0));
      sum += headerweight * sublinear(tfs.get("header").getOrDefault(map.getKey(), 0.0));
      sum += anchorweight * sublinear(tfs.get("anchor").getOrDefault(map.getKey(), 0.0));
//      sum += urlweight * tfs.get("url").getOrDefault(map.getKey(), 0.0);
//      sum += titleweight * tfs.get("title").getOrDefault(map.getKey(), 0.0);
//      sum += bodyweight * tfs.get("body").getOrDefault(map.getKey(), 0.0);
//      sum += headerweight * tfs.get("header").getOrDefault(map.getKey(), 0.0);
//      sum += anchorweight * tfs.get("anchor").getOrDefault(map.getKey(), 0.0);
      score += qv_q * sum;
    }
    // For debug, delete it.
    System.out.println("***********************");
    System.out.println("Query: " + q.toString());
    System.out.println("Doc: " + d.url);
    System.out.println("Scroe: " + score);
    System.out.println("***********************");
    return score;
  }

  private double sublinear(double count) {
    if(count <= 0.0)  return 0.0;
    return 1d+Math.log(count);
  }
  
  /**
   * Normalize the term frequencies. 
   * @param tfs the term frequencies
   * @param d the Document
   * @param q the Query
   */
  public void normalizeTFs(Map<String,Map<String, Double>> tfs,Document d, Query q) {
    for(Map<String, Double> docParts: tfs.values()) {
      for(Map.Entry<String, Double> entry: docParts.entrySet()) {
        double normalized_length = d.body_length + smoothingBodyLength;
        double termFreq = entry.getValue();
        docParts.put(entry.getKey(), termFreq/normalized_length);
      }
    }
  }
  
  /**
   * Write the tuned parameters of cosineSimilarity to file.
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
        "urlweight", "titleweight", "bodyweight", "headerweight", 
        "anchorweight", "smoothingBodyLength"
      };
      double[] values = {
        this.urlweight, this.titleweight, this.bodyweight, 
    this.headerweight, this.anchorweight, this.smoothingBodyLength
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
  /** Get the similarity score between a document and a query.
   * @param d the Document
   * @param q the Query
   * @return the similarity score.
   */
  public double getSimScore(Document d, Query q) {
    Map<String,Map<String, Double>> tfs = this.getDocTermFreqs(d,q);
    this.normalizeTFs(tfs, d, q);
    Map<String,Double> tfQuery = getQueryFreqs(q);

    // Write out tuned cosineSimilarity parameters
    // This is only used for grading purposes.
    // You should NOT modify the writeParaValues method.
    writeParaValues("cosinePara.txt");
    return getNetScore(tfs,q,tfQuery,d);
  }
}
