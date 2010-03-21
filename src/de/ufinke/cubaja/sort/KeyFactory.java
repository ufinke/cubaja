// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

/**
 * Sort key extractor needed with <tt>Matcher</tt>.
 * @author Uwe Finke
 * @param <D> data type
 * @param <K> key type
 */
public interface KeyFactory<D, K> {

  /**
   * Creates the key object which is subject to a comparator.
   * @param data the data object
   * @return the key, may be <tt>null</tt>
   */
  public K createKey(D data);
}
