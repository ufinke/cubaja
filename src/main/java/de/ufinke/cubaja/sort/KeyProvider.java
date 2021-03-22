// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

/**
 * Interface for data types that provide their key values for <code>Matcher</code>.
 * @author Uwe Finke
 * @param <K> key type
 */
public interface KeyProvider <K> {

  /**
   * Returns the key.
   * @return key value
   */
  public K getMatchKey();
}
