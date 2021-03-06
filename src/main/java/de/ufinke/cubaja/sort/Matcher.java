// Copyright (c) 2009 - 2011, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import de.ufinke.cubaja.util.Text;

/**
 * Matches any number of sorted sources.
 * <p>
 * All sources have to be previously sorted by key in the same order.
 * If applicable, use the same <code>Comparator</code> for sorting and matching.
 * Keys may be <code>null</code>;
 * the <code>Comparator</code> should allow for <code>null</code> values
 * to avoid <code>NullPointerException</code>s.
 * <p>
 * The distinct keys are presented to the application by an <code>Iterator</code> in an ascending order
 * (ascending as defined by the <code>Comparator</code>). 
 * For every key, the application can get the matching data objects from the {@link MatchSource} instances.
 * The <code>Matcher</code> ensures that any matching data objects which are not read from the
 * <code>MatchSource</code> are skipped before the next key in sequence is determined,
 * which is the lowest next key of all sources.
 * @author Uwe Finke
 * @param <K> key type
 */
public final class Matcher<K> implements Iterable<K> {

  static Text text = Text.getPackageInstance(Matcher.class);
  
  Comparator<? super K> comparator;
  List<InternalMatchSource<?, K>> sourceList;
  private Iterator<K> iterator;
  
  /**
   * Constructor.
   * @param comparator key comparator
   */
  public Matcher(Comparator<? super K> comparator) {
  
    this.comparator = comparator;
    sourceList = new ArrayList<InternalMatchSource<?, K>>();
  }
  
  /**
   * Adds a source.
   * @param <D> data type
   * @param sortedSource data source
   * @param keyFactory something that is able to extract a key from the data
   * @return an accessor to the data
   */
  public <D> MatchSource<D> addSource(Iterable<D> sortedSource, KeyFactory<D, K> keyFactory) {
    
    InternalMatchSource<D, K> source = new InternalMatchSource<D, K>(sortedSource, keyFactory, comparator);
    sourceList.add(source);
    return source.createDataSource();
  }
  
  /**
   * Adds a source where the key type is the data type.
   * @param <D> data type
   * @param sortedSource data source
   * @return an accessor to the data
   */
  public <D extends K> MatchSource<D> addSource(Iterable<D> sortedSource) {
    
    KeyFactory<D, K> keySource = new KeyFactory<D, K>() {
      
      public K createKey(D data) {
        
        return data;
      }
    };
    
    return addSource(sortedSource, keySource);
  }
  
  /**
   * Adds a source where the data type provides the key.
   * The data type has to implement {@link KeyProvider}.
   * @param <D> data type
   * @param sortedSource data source
   * @param type the data class
   * @return an accessor to the data
   */
  public <D extends KeyProvider<K>> MatchSource<D> addSource(Iterable<D> sortedSource, Class<D> type) {
    
    KeyFactory<D, K> keySource = new KeyFactory<D, K>() {
      
      public K createKey(D data) {
        
        return data.getMatchKey();
      }
    };
    
    return addSource(sortedSource, keySource);
  }
  
  /**
   * Returns an <code>Iterator</code> over the key values.
   */
  public Iterator<K> iterator() {
    
    if (iterator == null) {
      createIterator();
    }
    return iterator;
  }
  
  private void createIterator() {
    
    iterator = new Iterator<K>() {

      private K matchKey;
      private boolean hasMatchKey;
      private boolean tested;
      
      public final boolean hasNext() {

        for (InternalMatchSource<?, K> source : sourceList) {
          source.skipUnusedData();
        }
        
        hasMatchKey = false;
        
        for (InternalMatchSource<?, K> source : sourceList) {
          if (source.hasMore()) {
            K currentKey = source.getCurrentKey();
            if ((! hasMatchKey) || comparator.compare(currentKey, matchKey) < 0) {
              matchKey = currentKey;
            }
            hasMatchKey = true;
          }
        }
        
        for (InternalMatchSource<?, K> source : sourceList) {
          source.setMatchKey(matchKey);
        }
        
        tested = true;
        
        return hasMatchKey;
      }

      public final K next() {

        if (! tested) {
          hasNext();
        }
        
        if (hasMatchKey) {
          tested = false;
          return matchKey;
        }
        
        throw new NoSuchElementException();
      }

      public final void remove() {

        throw new UnsupportedOperationException();
      }
    };
  }
  
}
