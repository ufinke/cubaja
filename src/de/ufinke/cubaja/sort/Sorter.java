// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

public class Sorter<D extends Serializable> implements Iterable<D> {

  private SortAlgorithm algorithm;

  public Sorter(Comparator<? super D> comparator, SortConfig config) {
    
    algorithm = config.getAlgorithm();
    algorithm.setComparator(comparator);
    
  }
  
  public Sorter(Comparator<? super D> comparator) {
    
    this(comparator, new SortConfig());
  }
  
  public void add(D element) {
  
  }
  
  public Iterator<D> iterator() {

    try {      
    } catch (Throwable t) {      
      throw new SortException(t);
    }
    
    return new SortIterator<D>(this);
  }
  
  boolean hasNext() throws Exception {
    
    return false;
  }
  
  D next() throws Exception {
    
    return null;
  }
}
