// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import de.ufinke.cubaja.util.IteratorException;

class SortArray implements Iterator<Object>, Iterable<Object> {

  private Object[] array;
  private int size;
  private int position;
  private boolean followUp;
  private BlockingQueue<SortArray> queue;
  
  public SortArray(int capacity) {
    
    array = new Object[capacity];
  }
  
  public SortArray(Object[] array) {
    
    this.array = array;
    size = array.length;
  }
  
  public SortArray(BlockingQueue<SortArray> queue) {
    
    this.queue = queue;
    followUp = true;
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
  
  public Object getLastEntry() {
    
    if (size == 0) {
      return null;
    }
    return array[size - 1];
  }

  public void setFollowUp(boolean followUp) {
    
    this.followUp = followUp;
  }
  
  public boolean hasFollowUp() {
    
    return followUp;
  }
  
  public boolean hasNext() {

    return position < size || followUp;
  }

  public Object next() {

    if (position == size) {
      try {
        SortArray nextArray = queue.take();
        array = nextArray.getArray();
        size = nextArray.getSize();
        position = 0;
        followUp = nextArray.followUp;
      } catch (Exception e) {
        throw new IteratorException(e);
      }
    }
    
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
