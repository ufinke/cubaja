// Copyright (c) 2008 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import de.ufinke.cubaja.util.Text;

/**
 * Sorts an arbitrary number of objects.
 * If the objects don't fit into memory,
 * pre-sorted runs with serialized objects are written
 * to a temporary file.
 * An object is put to the sorter with the <tt>add</tt> method.
 * Sorted objects are retrieved with an iterator;
 * <tt>Sorter</tt> implements the appropriate <tt>Iterable</tt>.
 * @author Uwe Finke
 * @param <D> data type
 */
public class Sorter<D extends Serializable> implements Iterable<D> {

  static private enum State {
    PUT,
    GET,
    CLOSED
  }
  
  static private final Text text = new Text(Sorter.class);
  
  private final SortManager manager;
  private State state;
  
  private Object[] array;
  private int size;
  
  private boolean sortTaskStarted;
  
  /**
   * Constructor with default configuration.
   * @param comparator
   */
  public Sorter(Comparator<? super D> comparator) {
    
    this(comparator, new SortConfig());
  }
  
  /**
   * Constructor with explicit configuration.
   * @param comparator
   * @param config
   */
  public Sorter(Comparator<? super D> comparator, SortConfig config) {
  
    manager = new SortManager(config, comparator);    
    state = State.PUT;
    allocateArray();
  }
  
  private void allocateArray() {
    
    array = new Object[manager.getArraySize()];
    size = 0;    
  }
  
  /**
   * Adds an object.
   * @param element
   * @throws SorterException
   * @throws IllegalStateException
   */
  public void add(D element) throws SorterException, IllegalStateException {
  
    if (state != State.PUT) {
      throw new IllegalStateException(text.get("illegalState", state));
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
      sortTaskStarted = true;
    }
    
    writeRequest(new Request(RequestType.SORT_ARRAY, new SortArray(array, size)));
    manager.addPutCount(size);
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
  
  /**
   * Retrieves the sorted objects.
   */
  public Iterator<D> iterator() throws SorterException {

    if (state != State.PUT) {
      throw new IllegalStateException(text.get("illegalState", state));
    }
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

    manager.addPutCount(size);
    manager.getAlgorithm().sort(array, size);
    manager.switchState();
    
    return new SortArray(array, size);
  }
  
  private Iterator<Object> getQueueIterator() {

    writeArray();
    array = null;
    manager.switchState();
    
    writeRequest(new Request(RequestType.SWITCH_STATE));
    return new ResultQueueIterator(manager);
  }
  
  /**
   * Aborts the sort before all objects have been read.
   */
  public void abort() {
    
    close();
  }
  
  void close() {

    if (state == State.CLOSED) {
      return;
    }
    
    state = State.CLOSED;
    manager.checkError();
    array = null;
    writeRequest(new Request(RequestType.CLOSE));
    manager.close();
  }

}
