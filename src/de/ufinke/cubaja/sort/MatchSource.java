// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Accessor to data objects matching to a key.
 * Instances are created by {@link Matcher}.
 * @author Uwe Finke
 * @param <D> data type
 */
public final class MatchSource<D> {

  private InternalMatchSource<D, ?> source;
  
  MatchSource(InternalMatchSource<D, ?> source) {
  
    this.source = source;
  }
  
  /**
   * Returns a single data object with matching key.
   * An application should first test whether the key matches
   * (see {@link #matches matches})
   * before calling this method.
   * @return a data object
   * @throws NoSuchElementException if the data doesn't match the key
   */
  public D get() throws NoSuchElementException {
    
    if (matches()) {      
      return source.getCurrentData();
    }
    
    throw new NoSuchElementException();
  }
  
  /**
   * Returns an iterator over all matching data objects.
   * This iterator doesn't support the <tt>remove</tt> method.
   * @return an iterator
   */
  public Iterable<D> getAll() {
    
    return new Iterable<D>() {
      
      public Iterator<D> iterator() {

        return new Iterator<D>() {
          
          public boolean hasNext() {
            
            return matches();
          }
          
          public D next() {

            return get();
          }
          
          public void remove() {
            
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }
  
  /**
   * Returns a list with all matching data objects.
   * The list may be empty if there is no matching data.
   * @return a list
   */
  public List<D> getList() {
    
    List<D> list = new ArrayList<D>();
    for (D item : getAll()) {
      list.add(item);
    }
    return list;
  }
  
  /**
   * Tells whether this source matches the <tt>Matcher</tt>s key.
   * @return <tt>true</tt> when matching, <tt>false</tt> otherwise
   */
  public boolean matches() {
    
    return source.isMatching(); 
  }
}
