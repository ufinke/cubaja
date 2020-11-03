// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;
import java.util.Iterator;

final class InternalMatchSource<D, K> {

  private Iterator<D> source;
  private KeyFactory<D, K> keyFactory;
  private D currentData;
  private K currentKey;
  private K matchKey;
  private Comparator<? super K> comparator;
  private boolean matching;
  private boolean hasCurrent;
  private boolean hasMatch;
  private int recordCount;
  
  InternalMatchSource(Iterable<D> sortedSource, KeyFactory<D, K> keyFactory, Comparator<? super K> comparator) {
    
    source = sortedSource.iterator();
    this.keyFactory = keyFactory;    
    this.comparator = comparator;
    
    readNext();
  }
  
  MatchSource<D> createDataSource() {
    
    return new MatchSource<D>(this);
  }
  
  D getCurrentData() {
    
    D result = currentData;
    readNext();
    return result;
  }
  
  K getCurrentKey() {
    
    return currentKey;
  }
  
  void skipUnusedData() {
    
    while (matching) {
      readNext();
    }
  }
  
  void setMatchKey(K matchKey) {
    
    this.matchKey = matchKey;
    hasMatch = true;
    testMatch();
  }
  
  boolean hasMore() {
    
    return hasCurrent;
  }
    
  boolean isMatching() {
    
    return matching;
  }
  
  int getRecordCount() {
    
    return recordCount;
  }
  
  private void readNext() {
    
    hasCurrent = source.hasNext();
    if (hasCurrent) {      
      currentData = source.next();
      currentKey = keyFactory.createKey(currentData);
      recordCount++;
    }
    testMatch();
  }
  
  private void testMatch() {
    
    matching = hasCurrent && hasMatch && comparator.compare(currentKey, matchKey) == 0;
  }
}
