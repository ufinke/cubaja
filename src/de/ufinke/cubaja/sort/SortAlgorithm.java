// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;

/**
 * Sort algorithm.
 * @author Uwe Finke
 * @param <D> element data type
 */
public interface SortAlgorithm<D> {

  /**
   * Sorts an array with data elements.
   * @param array the array
   * @param size number of valid array elements
   * @param comparator the comparator
   * @throws Exception
   */
  public Object[] sort(Object[] array, int size);
  
  public void setComparator(Comparator<? super D> comparator);
  
  public double memoryFactor();
}
