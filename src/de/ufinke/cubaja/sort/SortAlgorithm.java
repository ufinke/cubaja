// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
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
   * @param array
   * @return sorted array
   */
  public void sort(SortArray array);
  
  /**
   * Sets the comparator.
   * @param comparator
   */
  @SuppressWarnings("rawtypes")
  public void setComparator(Comparator comparator);
}
