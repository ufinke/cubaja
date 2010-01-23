// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.util.Iterator;
import java.util.NoSuchElementException;
import de.ufinke.cubaja.util.IteratorException;

/**
 * <code>Iterator</code> over rows of a <code>ColumnReader</code>.
 * Returns itself as <code>Iterable</code>.
 * An instance is created by method <code>readAllRows</code> of a <code>ColumnReader</code>.
 * <p>
 * Because an <code>Iterator</code> is not allowed to throw a normal <code>Exception</code>,
 * the methods <code>next</code> and <code>hasNext</code> wrap exceptions into
 * an <code>IteratorException</code> which is a <code>RuntimeException</code>.
 * <p>
 * The <code>remove</code> operation is not supported.
 * @author Uwe Finke
 * @param <D> data object type
 */
public class RowIterator<D> implements Iterator<D>, Iterable<D> {

  private ColumnReader reader;
  private Class<? extends D> clazz;
  private boolean calledHasNext;
  private boolean hasNext;
  
  /**
   * Constructor.
   * @param reader
   * @param clazz
   */
  public RowIterator(ColumnReader reader, Class<? extends D> clazz) {
  
    this.reader = reader;
    this.clazz = clazz;
  }
  
  public Iterator<D> iterator() {
    
    return this;
  }
  
  public boolean hasNext() throws IteratorException {
    
    if (! calledHasNext) {
      calledHasNext = true;
      try {
        hasNext = reader.nextRow();
      } catch (Exception e) {
        throw new IteratorException(e);
      }
    }
    
    return hasNext;
  }
  
  public D next() throws IteratorException, NoSuchElementException {
    
    if (! calledHasNext) {
      if (! hasNext()) {
        throw new NoSuchElementException();
      }
    }
    calledHasNext = false;

    try {
      return reader.readRow(clazz);
    } catch (Exception e) {
      throw new IteratorException(e);
    }
  }
  
  /**
   * Throws an <code>UnsupportedOperationException</code>.
   */
  public void remove() throws UnsupportedOperationException {
    
    throw new UnsupportedOperationException();
  }
}
