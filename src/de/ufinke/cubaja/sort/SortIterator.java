// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.*;

class SortIterator<D> implements Iterator<D> {

  private Sorter<D> sorter;
  
  SortIterator(Sorter<D> sorter) {
    
    this.sorter = sorter;
  }

  public boolean hasNext() {

    try {      
      return sorter.hasNext();
    } catch (Throwable t) {
      throw new SortException(t);
    }
  }

  public D next() {

    try {      
      return sorter.next();
    } catch (Throwable t) {
      throw new SortException(t);
    }
  }

  public void remove() {

    throw new UnsupportedOperationException();
  }
}
