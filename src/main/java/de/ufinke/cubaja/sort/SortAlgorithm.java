// Copyright (c) 2008 - 2011, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;

/**
 * Sort algorithm.
 * @author Uwe Finke
 */
public interface SortAlgorithm {

  /**
   * Sorts an array of objects.
   * Important: This method may be called in parallel from several threads.
   * Implementations should work with local variables only;
   * avoid member variables!
   * @param array
   * @param size
   * @param comparator
   */
  @SuppressWarnings("rawtypes")
  public void sort(Object[] array, int size, Comparator comparator);
  
}
