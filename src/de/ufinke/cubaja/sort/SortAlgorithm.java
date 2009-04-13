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

  public D[] sort(D[] array, int size);
  
  public void setComparator(Comparator<? super D> comparator);
  
  public double memoryFactor();
}
