// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;
import java.util.Iterator;
import de.ufinke.cubaja.io.Streamer;

public class Sorter<D> implements Iterable<D> {

  private boolean iteratorCreated;
  private SortAlgorithm<D> algorithm;
  
  public Sorter(Comparator<D> comparator, Streamer<D> streamer) {
    
  }
  
  public void setAlgorithm(SortAlgorithm<D> algorithm) {
    
    this.algorithm = algorithm;
  }
  
  public void add(D element) throws Exception {
  
    if (iteratorCreated) {
      throw new IllegalStateException();
    }
    
    //TODO add element
  }

  /**
   * 
   */
  public Iterator<D> iterator() {

    if (iteratorCreated) {
      throw new IllegalStateException();
    }
    iteratorCreated = true;
    
    //TODO sort remaining items
    
    return new Iterator<D>() {

      public boolean hasNext() {

        // TODO Auto-generated method stub
        return false;
      }

      public D next() {

        // TODO Auto-generated method stub
        return null;
      }

      public void remove() {

        throw new UnsupportedOperationException();
      }
    };
  }
}
