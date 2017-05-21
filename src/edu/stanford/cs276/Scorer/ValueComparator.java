package edu.stanford.cs276.Scorer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mingjianliu on 5/20/17.
 */
class ValueComparator<K, V extends Comparable<V>> implements Comparator<K>{

  HashMap<K, V> map = new HashMap<K, V>();

  public ValueComparator(HashMap<K, V> map){
    this.map.putAll(map);
  }

  @Override
  public int compare(K s1, K s2) {
    return -map.get(s1).compareTo(map.get(s2));//descending order
  }
}