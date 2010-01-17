// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

final class ResultQueueIterator implements Iterator<Object>, Iterable<Object> {

  private final SortManager manager;
  private final BlockingQueue<Request> queue;
  
  private Object[] array;
  private int size;
  private int position;
  private boolean endOfData;
  
  public ResultQueueIterator(SortManager manager) {
  
    this.manager = manager;
    queue = manager.getMainQueue();
    readFromQueue();
  }

  public boolean hasNext() {
    
    if (position < size) {
      return true;
    }
    
    if (endOfData) {
      return false;
    }
    
    readFromQueue();
    return position < size;
  }

  public Object next() {

    if (hasNext()) {
      return array[position++];
    }
    throw new NoSuchElementException();
  }

  public void remove() {

    throw new UnsupportedOperationException();
  }

  public Iterator<Object> iterator() {

    return this;
  }
  
  private void readFromQueue() {
    
    Request request = null;
    
    while (request == null) {
      try {
        request = queue.poll(1, TimeUnit.SECONDS);
      } catch (Exception e) {
        manager.setError(e);
      }
      manager.checkError();
    }
    
    switch (request.getType()) {
      
      case RESULT:
        SortArray data = (SortArray) request.getData();
        array = data.getArray();
        size = data.getSize();
        position = 0;
        endOfData = false;
        break;
        
      case END_OF_DATA:
        endOfData = true;
        break;
    }
  }
}
