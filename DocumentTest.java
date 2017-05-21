package edu.stanford.cs276;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by mingjianliu on 5/20/17.
 */
public class DocumentTest {
  Document doc = new Document("test/url");
  @org.junit.Before
  public void setUp() throws Exception {
    doc.title = "Test url";
    doc.headers = new ArrayList<String>();
    doc.headers.add("header1");
    doc.headers.add("header2");
    doc.headers.add("header3");
    doc.body_hits = new HashMap<String, List<Integer>>();
    doc.body_hits.put("x", Arrays.asList(1,2,3));
    doc.body_hits.put("y", Arrays.asList(4,5,6));
    doc.anchors = new HashMap<String, Integer>();
    doc.anchors.put("a b c d", 10);
  }

  @org.junit.Test
  public void url_length() throws Exception {
    assertTrue(doc.url_length() == (double)2.0);
  }

  @org.junit.Test
  public void title_length() throws Exception {
    assertTrue(doc.title_length() == (double)2.0);
  }

  @org.junit.Test
  public void header_length() throws Exception {
    assertTrue(doc.header_length() == (double)3.0);
  }

  @org.junit.Test
  public void anchor_length() throws Exception {
    assertTrue(doc.anchor_length() == (double)40.0);
  }

}