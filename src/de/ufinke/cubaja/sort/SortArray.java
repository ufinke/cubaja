// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

class SortArray<D> {

  private D[] array;
  private int size;
  
  SortArray(D[] array, int size) {
    
    this.array = array;
    this.size = size;
  }
  
  boolean isFull() {
    
    return size == array.length;
  }
  
  void add(D element) {
    
    array[size++] = element;
  }
  
  int getSize() {
    
    return size;
  }
  
  D[] getArray() {
    
    return array;
  }
}
