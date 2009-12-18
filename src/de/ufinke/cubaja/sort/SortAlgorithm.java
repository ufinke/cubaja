// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;

/**
 * Sort algorithm.
 * @author Uwe Finke
 */
public interface SortAlgorithm {

  public Object[] sort(Object[] array, int size);
  
  @SuppressWarnings("rawtypes")
  public void setComparator(Comparator comparator);
  
  public double memoryFactor();
}
