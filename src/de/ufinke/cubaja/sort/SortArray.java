// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.*;

public class SortArray implements Iterator<Object> {

  private Object[] array;
  private int size;
  private int position;
  
  public SortArray(int capacity) {
    
    array = new Object[capacity];
  }
  
  public SortArray(Object[] array) {
    
    this.array = array;
    size = array.length;
  }
  
  public SortArray(Object[] array, int size) {
    
    this.array = array;
    this.size = size;
  }

  public void enlarge(int newCapacity) {
  
    Object[] newArray = new Object[newCapacity];
    System.arraycopy(array, 0, newArray, 0, size);
    array = newArray;
  }
  
  public void clear() {
    
    size = 0;
  }
  
  public boolean isFull() {
    
    return size == array.length;
  }
  
  public void add(Object element) {
    
    array[size++] = element;
  }
  
  public int getSize() {
    
    return size;
  }
  
  public Object[] getArray() {
    
    return array;
  }
  
  public void setArray(Object[] array) {
    
    this.array = array;
  }

  public boolean hasNext() {

    return position < size;
  }

  public Object next() {

    if (hasNext()) {
      Object result = array[position];
      array[position++] = null;
      return result;
    } else {
      throw new NoSuchElementException();
    }
  }

  public void remove() {

    throw new UnsupportedOperationException();
  }
}
