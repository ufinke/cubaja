// Copyright (c) 2007 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;

/**
 * Mergesort algorithm.
 * Stable sort; the original sequence of equal elements will be retained.
 * @author Uwe Finke
 */
public class Mergesort implements SortAlgorithm {

  private Object[] entries;
  private Object[] temp;
  @SuppressWarnings("rawtypes")
  private Comparator comparator;
  
  /**
   * Constructor.
   */
  public Mergesort() {
    
  }
  
  @SuppressWarnings("rawtypes")
  public void setComparator(Comparator comparator) {
    
    this.comparator = comparator;
  }
  
  public void sort(Object[] array, int size) {

    if (size == 0) {
      return;
    }
    
    entries = array;
    
    if (temp == null && temp.length < size) {
      temp = new Object[size];
    }
    
    mergesort(0, size - 1);
  }
  
  @SuppressWarnings("unchecked")
  private void mergesort(int left, int right) {
    
    if (right <= left) {
      return;
    }

    int i = left;
    boolean sorted = true;
    while (sorted && (i < right)) {
      sorted = comparator.compare(entries[i], entries[++i]) <= 0;
    }
    if (sorted) {
      return;
    }
    
    int middle = (left + right) / 2;
    
    mergesort(left, middle);
    mergesort(middle + 1, right);
    
    System.arraycopy(entries, left, temp, left, right - left + 1);
    
    int iLeft = left;
    int iRight = middle + 1;
    int iOut = left;
    
    while ((iLeft <= middle) && (iRight <= right)) {
      entries[iOut++] = comparator.compare(temp[iLeft], temp[iRight]) <= 0 ? temp[iLeft++] : temp[iRight++];
    }
    
    if (iLeft <= middle) {
      System.arraycopy(temp, iLeft, entries, iOut, middle - iLeft + 1);
    } else {
      System.arraycopy(temp, iRight, entries, iOut, right - iRight + 1);
    }    
  }
}
