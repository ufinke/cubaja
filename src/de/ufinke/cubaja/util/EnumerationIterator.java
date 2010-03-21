// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * Wrapper for legacy <tt>Enumeration</tt>s.
 * @author Uwe Finke
 * @param <E> type
 */
public class EnumerationIterator<E> implements Iterator<E>, Iterable<E> {

  private Enumeration<E> enumeration;
  
  /**
   * Constructor.
   * @param enumeration enumeration to wrap
   */
  public EnumerationIterator(Enumeration<E> enumeration) {
  
    this.enumeration = enumeration;
  }

  public boolean hasNext() {

    return enumeration.hasMoreElements();
  }

  public E next() {

    return enumeration.nextElement();
  }

  /**
   * Throws <tt>UnsupportedOperationException</tt>.
   */
  public void remove() {

    throw new UnsupportedOperationException();
  }
  
  public Iterator<E> iterator() {
    
    return this;
  }
}
