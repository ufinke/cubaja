// Copyright (c) 2008 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import de.ufinke.cubaja.util.Text;

public class Sorter<D extends Serializable> implements Iterable<D> {

  static private enum State {
    PUT,
    GET
  }
  
  static private final Text text = new Text(Sorter.class);
  
  private final SortManager manager;
  private State state;
  
  private Object[] array;
  private int size;
  
  private boolean sortTaskStarted;
  
  public Sorter(Comparator<? super D> comparator) {
    
    this(comparator, new SortConfig());
  }
  
  public Sorter(Comparator<? super D> comparator, SortConfig config) {
  
    manager = new SortManager(config, comparator);
    
    if (manager.isDebug()) {
      manager.debug("sortOpen");
    }    
    
    state = State.PUT;
    allocateArray();
  }
  
  private void allocateArray() {
    
    array = new Object[manager.getArraySize()];
    size = 0;    
  }
  
  public void add(D element) throws SorterException, IllegalStateException {
  
    if (state != State.PUT) {
      throw new IllegalStateException(text.get("illegalAdd"));
    }

    if (size == array.length) {
      writeArray();
      allocateArray();
    }
    
    array[size++] = element;
  }
  
  private void writeArray() {
    
    if (size == 0) {
      return;
    }

    if (! sortTaskStarted) {
      manager.submit(new SortTask(manager));
    }
    
    writeRequest(new Request(RequestType.SORT_ARRAY, new SortArray(array, size)));
  }
  
  private void writeRequest(Request request) {
    
    final BlockingQueue<Request> queue = manager.getSortQueue();
    boolean written = false;
    while (! written) {
      manager.checkError();
      try {
        written = queue.offer(request, 1, TimeUnit.SECONDS);
      } catch (Exception e) {
        throw new SorterException(e);
      }
    }
  }
  
  public Iterator<D> iterator() throws SorterException {

    state = State.GET;

    try {
      return createIterator(); 
    } catch (Exception e) {
      throw new SorterException(e);
    }
  }
  
  @SuppressWarnings({"unchecked"})
  private Iterator<D> createIterator() {
    
    final Iterator<Object> source = (sortTaskStarted) ? getQueueIterator() : getSimpleIterator();
    
    return new Iterator<D>() {

      public boolean hasNext() {

        boolean result = source.hasNext();
        if (! result) {
          close();
        }
        return result;
      }

      public D next() {

        return (D) source.next();
      }

      public void remove() {

        throw new UnsupportedOperationException();
      }      
    };
  }
  
  private Iterator<Object> getSimpleIterator() {

    manager.getAlgorithm().sort(array, size);
    return new SortArray(array, size);
  }
  
  private Iterator<Object> getQueueIterator() {

    writeRequest(new Request(RequestType.SWITCH_STATE));
    return new ResultQueueIterator(manager);
  }
  
  void close() {
    
    manager.close();
  }

}
