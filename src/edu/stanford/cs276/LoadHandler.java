package edu.stanford.cs276;

import edu.stanford.cs276.util.Stemmer;

import java.io.*;
import java.util.*;

/**
 * This class is used to
 * 1) load training data from files
 * 2) build idf from data collections in PA1.
 */
public class LoadHandler {
  /**
   * Loads the training data.
   *
   * @param feature_file_name the name of the feature file.
   * @return the mapping of Query-url-Document
   */
  public static Map<Query, Map<String, Document>> loadTrainData(String feature_file_name) throws Exception {
    File feature_file = new File(feature_file_name);
    if (!feature_file.exists()) {
      System.err.println("Invalid feature file name: " + feature_file_name);
      return null;
    }

    BufferedReader reader = new BufferedReader(new FileReader(feature_file));
    String line = null, url = null, anchor_text = null;
    Query query = null;
    
    /* Feature dictionary: Query -> (url -> Document)  */
    Map<Query, Map<String, Document>> queryDict = new HashMap<Query, Map<String, Document>>();

    while ((line = reader.readLine()) != null) {
      String[] tokens = line.split(":", 2);
      String key = tokens[0].trim();
      String value = tokens[1].trim();

      if (key.equals("query")) {
        query = new Query(value);
        queryDict.put(query, new HashMap<String, Document>());
      } else if (key.equals("url")) {
        url = value;
        queryDict.get(query).put(url, new Document(url));
      } else if (key.equals("title")) {
        queryDict.get(query).get(url).title = new String(value);
      } else if (key.equals("header")) {
        if (queryDict.get(query).get(url).headers == null)
          queryDict.get(query).get(url).headers = new ArrayList<String>();
        queryDict.get(query).get(url).headers.add(value);
      } else if (key.equals("body_hits")) {
        if (queryDict.get(query).get(url).body_hits == null)
          queryDict.get(query).get(url).body_hits = new HashMap<String, List<Integer>>();
        String[] temp = value.split(" ", 2);
        String term = temp[0].trim();
        term = getStem(term);
        List<Integer> positions_int;

        if (!queryDict.get(query).get(url).body_hits.containsKey(term)) {
          positions_int = new ArrayList<Integer>();
          queryDict.get(query).get(url).body_hits.put(term, positions_int);
        } else
          positions_int = queryDict.get(query).get(url).body_hits.get(term);

        String[] positions = temp[1].trim().split(" ");
        for (String position : positions)
          positions_int.add(Integer.parseInt(position));

      } else if (key.equals("body_length"))
        queryDict.get(query).get(url).body_length = Integer.parseInt(value);
      else if (key.equals("pagerank"))
        queryDict.get(query).get(url).page_rank = Integer.parseInt(value);
      else if (key.equals("anchor_text")) {
        anchor_text = value;
        if (queryDict.get(query).get(url).anchors == null)
          queryDict.get(query).get(url).anchors = new HashMap<String, Integer>();
      } else if (key.equals("stanford_anchor_count"))
        queryDict.get(query).get(url).anchors.put(anchor_text, Integer.parseInt(value));
    }

    reader.close();

    return queryDict;
  }

  private static String getStem(String word) {
    Stemmer stemmer = new Stemmer();
    stemmer.add(word.toCharArray(), word.length());
    stemmer.stem();
    return stemmer.toString();
  }

  /**
   * Unserializes the term-doc counts from file.
   *
   * @param idfFile the file containing the idfs.
   * @return the mapping of term-doc counts.
   */
  public static Map<String, Double> loadDFs(String idfFile) {
    Map<String, Double> IDF = null;
    try {
      FileInputStream fis = new FileInputStream(idfFile);
      ObjectInputStream ois = new ObjectInputStream(fis);
      IDF = (HashMap<String, Double>) ois.readObject();
      ois.close();
      fis.close();
    } catch (IOException | ClassNotFoundException ioe) {
      ioe.printStackTrace();
      return null;
    }
    return IDF;
  }

  /**
   * Builds document frequencies and then serializes to file.
   *
   * @param dataDir the data directory
   * @param idfFile the file containing the idfs.
   * @return the term-doc counts
   */
  public static Map<String, Double> buildDFs(String dataDir, String idfFile) throws IOException {
    // Get root directory
    String root = dataDir;
    File rootdir = new File(root);
    if (!rootdir.exists() || !rootdir.isDirectory()) {
      System.err.println("Invalid data directory: " + root);
      return null;
    }

    // Array of all the blocks (sub directories) in the PA1 corpus
    File[] dirlist = rootdir.listFiles();

    int totalDocCount = 0;

    // Count number of documents in which each term appears
    Map<String, Double> termDocCount = new HashMap<String, Double>();
    Map<String, Double> IDF = new HashMap<String, Double>();

    /*
     * TODO: Your code here.
     * For each file in each block, accumulate counts for:
     * 1) Total number of documents
     * 2) Total number of documents containing each term
     * Hint: consult PA1 for how to load each file in each block
     */

    FileFilter filter = new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        String name = pathname.getName();
        return !name.startsWith(".");
      }
    };

    // The structure is borrowed from PA1
    for (File block : dirlist) {

      File blockDir = new File(root, block.getName());
      File[] filelist = blockDir.listFiles(filter);

      /* For each file */
      for (File file : filelist) {
        ++totalDocCount;
        Set<String> dict = new HashSet<>();
        String fileName = block.getName() + "/" + file.getName();
        // use pre-increment to ensure docID > 0

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
          String[] tokens = line.trim().toLowerCase().split("\\s+");
          for (String token : tokens) {
            if(dict.contains(token))  continue;
            dict.add(token);
            double count = termDocCount.getOrDefault(token, 0.0);
            termDocCount.put(token, count + 1.0);
          }
        }
        reader.close();
      }
      // Compute inverse document frequencies using document frequencies
      for (String term : termDocCount.keySet()) {
      /*
       * TODO : Your code here
       * Remember that it's possible for a term to not appear 
       * in the collection corpus.
       * Thus to guard against such a case, we will apply 
       * Laplace add-one smoothing.
       */
        double count = termDocCount.getOrDefault(term, 0.0);
        double idf_value = Math.log((totalDocCount + 1d) / (count + 1d));
        if(idf_value < 0d){
          System.out.println("totalDocCount: " + totalDocCount);
          System.out.println("count: " + count);
        }
        IDF.put(term, idf_value);
      }
      IDF.put("@@@", (double)Math.log(totalDocCount+1d));

      // Save to file
      try {
        FileOutputStream fos = new FileOutputStream(idfFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(IDF);
        oos.close();
        fos.close();
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }

    }
    return IDF;
  }
}
