// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

class SortArray {

  private Object[] array;
  private int size;
  
  SortArray(int capacity) {
    
    array = new Object[capacity];
  }

  void enlarge(int newCapacity) {
  
    Object[] newArray = new Object[newCapacity];
    System.arraycopy(array, 0, newArray, 0, size);
    array = newArray;
  }
  
  boolean isFull() {
    
    return size == array.length;
  }
  
  void add(Object element) {
    
    array[size++] = element;
  }
  
  int getSize() {
    
    return size;
  }
  
  Object[] getArray() {
    
    return array;
  }
}
