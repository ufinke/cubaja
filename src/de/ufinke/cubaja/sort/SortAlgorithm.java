// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;

/**
 * Sort algorithm.
 * @author Uwe Finke
 * @param <T> element data type
 */
public interface SortAlgorithm<T> {

  /**
   * Sorts an array with data elements.
   * @param array the array
   * @param size number of valid array elements
   * @param comparator the comparator
   * @throws Exception
   */
  public void sort(T[] array, int size, Comparator<T> comparator);
}
