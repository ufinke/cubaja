// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.util.Iterator;
import java.util.NoSuchElementException;
import de.ufinke.cubaja.util.IteratorException;

/**
 * <tt>Iterator</tt> over rows of a <tt>ColumnReader</tt>.
 * Returns itself as <tt>Iterable</tt>.
 * An instance is created by method {@link ColumnReader#cursor cursor} of a <tt>ColumnReader</tt>.
 * <p>
 * Because an <tt>Iterator</tt> is not allowed to throw a normal <tt>Exception</tt>,
 * the methods <tt>next</tt> and <tt>hasNext</tt> wrap exceptions into
 * an <tt>IteratorException</tt> which is a <tt>RuntimeException</tt>.
 * <p>
 * The <tt>remove</tt> operation is not supported.
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
  
  /**
   * Returns this object as <tt>Iterator</tt>
   */
  public Iterator<D> iterator() {
    
    return this;
  }
  
  /**
   * Signals whether there a next row to read.
   */
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
  
  /**
   * Retrieves the next row.
   */
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
   * Throws an <tt>UnsupportedOperationException</tt>.
   */
  public void remove() throws UnsupportedOperationException {
    
    throw new UnsupportedOperationException();
  }
}
