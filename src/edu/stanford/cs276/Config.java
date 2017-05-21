package edu.stanford.cs276;

public class Config {
  public static double urlweight = 486;
  public static double titleweight  = 34;
  public static double bodyweight = 526;
  public static double headerweight = 34.55;
  public static double anchorweight = 9.7028;
  public static double smoothingBodyLength = 500.0;

  // BM25-specific weights
  public static double burl = 1.0;
  public static double btitle = 0.9;
  public static double bheader = 0.5;
  public static double bbody = 1.0;
  public static double banchor = 0.2;

  public static double k1 = 2.5;
  public static double pageRankLambda = 50;
  public static double pageRankLambdaPrime = 2.1;

}
