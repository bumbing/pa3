package edu.stanford.cs276.Scorer;

import edu.stanford.cs276.Document;
import edu.stanford.cs276.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A skeleton for implementing the Smallest Window scorer in Task 3.
 * Note: The class provided in the skeleton code extends BM25Scorer in Task 2. However, you don't necessarily
 * have to use Task 2. (You could also use Task 1, in which case, you'd probably like to extend CosineSimilarityScorer instead.)
 * Also, feel free to modify or add helpers inside this class.
 */
public class SmallestWindowScorer extends CosineSimilarityScorer {

  private double lambda = 2.65;
  
  public SmallestWindowScorer(Map<String, Double> idfs, Map<Query,Map<String, Document>> queryDict) {
    super(idfs);
  }

  /**
   * get smallest window of one document and query pair.
   * @param d: document
   * @param q: query
   */  
  private int getWindow(Document d, Query q) {
    int result = Integer.MAX_VALUE;
    result = Math.min(result, minimalWindow(d.url.split("\\w+"), q));
    result = Math.min(result, minimalWindow(d.title.split("\\w+"), q));
    if(d.headers != null) {
      for(String header: d.headers) {
        result = Math.min(result, minimalWindow(header.split("\\w+"), q));
      }
    }
    if(d.anchors != null) {
      for(String anchor: d.anchors.keySet()) {
        result = Math.min(result, minimalWindow(anchor.split("\\w+"), q));
      }
    }

    if(d.body_hits != null) {
      result = Math.min(result, minimalWindow(d.body_hits, q));
    }
    return result;
  }

  private int minimalWindow(String[] text, Query q) {
    int result = Integer.MAX_VALUE;
    int size = q.termCount().size();
    Map<String, Integer> lastTerm = new HashMap<>();
    int head = 0, tail = 0;
    while(tail < text.length) {
      if(lastTerm.size()<q.termCount().size()) {
        if(q.termCount().containsKey(text[tail])) {
          lastTerm.put(text[tail], tail);
        }
        tail++;
      } else {
        if (q.termCount().containsKey(text[head]) && lastTerm.get(text[head]) == head) {
          lastTerm.remove(text[head]);
        }
        head++;
      }

      if(lastTerm.size() == q.termCount().size()) {
        result = Math.min(result, tail - head + 1);
      }
    }
    return result;
  }

  private int minimalWindow(Map<String, List<Integer>> body_hits, Query q) {
    for(String term: q.termCount().keySet()) {
      if(body_hits.get(term)==null) {
        continue;
      }
    }
    return Integer.MAX_VALUE;
  }
  
  /**
   * get boost score of one document and query pair.
   * @param d: document
   * @param q: query
   */  
  private double getBoostScore (Document d, Query q) {
    int smallestWindow = getWindow(d, q);
    if(smallestWindow == Integer.MAX_VALUE) return 1d;
    return  1.0 + (lambda - 1.0) / (smallestWindow - q.termCount().size() + 1.0);
  }
  
  @Override
  public double getSimScore(Document d, Query q) {
    Map<String,Map<String, Double>> tfs = this.getDocTermFreqs(d,q);
    this.normalizeTFs(tfs, d, q);
    Map<String,Double> tfQuery = getQueryFreqs(q);
    double boost = getBoostScore(d, q);
    double rawScore = this.getNetScore(tfs, q, tfQuery, d);
    return boost * rawScore;
  }

}
