// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.*;

class Run implements Iterator<Object>, Iterable<Object>  {

  private SortArray array;
  
  private long nextBlockPosition;
  private int nextBlockLength;
  
  public Run(Info info) {
    
  }

  public boolean hasNext() {
    
    return array.hasNext() || nextBlockLength > 0;
  }

  public Object next() {

    if (array.hasNext()) {
      return array.next();
    }
    
    //TODO next array
    return null;
  }

  public void remove() {

    throw new UnsupportedOperationException();
  }
  
  public Iterator<Object> iterator() {
    
    return this;
  }
}
