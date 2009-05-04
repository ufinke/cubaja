// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.util.Iterator;
import de.ufinke.cubaja.util.IteratorException;

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
      try {
        hasNext = reader.nextRecord();
      } catch (Exception e) {
        throw new IteratorException(e);
      }
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

    try {
      return reader.readObject(clazz);
    } catch (Exception e) {
      throw new IteratorException(e);
    }
  }
  
  public void remove() {
    
    throw new UnsupportedOperationException();
  }
}
