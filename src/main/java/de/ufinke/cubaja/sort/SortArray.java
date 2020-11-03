// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class SortArray implements Iterator<Object>, Iterable<Object> {

  private final Object[] array;
  private final int size;
  
  private int position;
  
  public SortArray(Object[] array, int size) {

    this.array = array;
    this.size = size;
  }
  
  public int getSize() {
    
    return size;
  }
  
  public Object[] getArray() {
    
    return array;
  }

  public boolean hasNext() {

    return position < size;
  }

  public Object next() {

    if (hasNext()) {
      return array[position++];
    }
    throw new NoSuchElementException();
  }

  public void remove() {

    throw new UnsupportedOperationException();
  }

  public Iterator<Object> iterator() {

    return this;
  }
  
}
