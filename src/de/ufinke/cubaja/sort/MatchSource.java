// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Accessor to data objects matching to a key.
 * Instances are created by <code>Matcher</code>.
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
   * We should first test whether the key matches
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
   * This iterator doesn't support the <code>remove</code> method.
   * @return an iterator
   */
  public Iterable<D> getAll() {
    
    return new Iterable<D>() {
      
      public final Iterator<D> iterator() {

        return new Iterator<D>() {
          
          public final boolean hasNext() {
            
            return matches();
          }
          
          public final D next() {

            return get();
          }
          
          public final void remove() {
            
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
   * Signals whether this source matches the <code>Matcher</code>s key.
   * @return <code>true</code> when matching, <code>false</code> otherwise
   */
  public boolean matches() {
    
    return source.isMatching(); 
  }
}
