// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.util.Iterator;

class ObjectIterator<D> implements Iterator<D>, Iterable<D> {

  private CsvReader reader;
  private Class<? extends D> clazz;
  private boolean calledHasNext;
  private boolean hasNext;
  
  ObjectIterator(CsvReader reader, Class<? extends D> clazz) {
  
    this.reader = reader;
    this.clazz = clazz;
  }
  
  public Iterator<D> iterator() {
    
    return this;
  }
  
  public boolean hasNext() {
    
    if (! calledHasNext) {
      calledHasNext = true;
      hasNext = reader.nextLine();
    }
    
    return hasNext;
  }
  
  public D next() {
    
    if (! calledHasNext) {
      if (! hasNext()) {
        return null;
      }
    }
    calledHasNext = false;
    
    return reader.readObject(clazz);
  }
  
  public void remove() {
    
    throw new UnsupportedOperationException();
  }
}
