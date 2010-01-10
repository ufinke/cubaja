// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Iterator;

class SortArray implements Iterator<Object>, Iterable<Object> {

  static int id = 0;
  
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
  
  public boolean hasNext() {

    return position < size;
  }

  public Object next() {
    
    Object result = array[position];
    array[position++] = null;
    return result;
  }
  
  public void remove() {

    throw new UnsupportedOperationException();
  }
  
  public Iterator<Object> iterator() {
    
    return this;
  }
}
