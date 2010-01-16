// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

class SortArray {

  private final Object[] array;
  private final int size;
  
  public SortArray(Object[] array, int size) {

    this.array = array;
    this.size = size;
  }
  
  public int getSize() {
    
    return size;
  }
  
  public Object[] getArray() {
    
    return array;
  }
  
}
