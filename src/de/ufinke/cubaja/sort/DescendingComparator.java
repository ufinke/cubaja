// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;

/**
 * <tt>Comparator</tt> for descending sort order.
 * @author Uwe Finke
 * @param <D> data type
 */
public class DescendingComparator<D> implements Comparator<D> {

  private Comparator<D> comparator;
  
  /**
   * Constructor.
   * @param ascendingComparator
   */
  public DescendingComparator(Comparator<D> ascendingComparator) {
  
    comparator = ascendingComparator;
  }
  
  /**
   * Multiplies the result of the ascending comparator by -1.
   */
  public int compare(D a, D b) {
    
    return comparator.compare(a, b) * -1;
  }
}
