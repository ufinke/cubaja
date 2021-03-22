// Copyright (c) 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.Iterator;

/**
 * Source of iterable data.
 * <p>
 * Starts a background worker thread.
 * Within the implementation of <code>execute</code>
 * (which runs in the background thread) 
 * call <code>add</code> for every source object.
 * The <code>add</code> method will add the object to a queue. The queue is read by <code>iterator</code>.   
 * @author Uwe Finke
 * @param <D> data type
 */
public abstract class AbstractIterable<D> implements Iterable<D> {

  private AbstractIterableWorker<D> iterator;
  
  /**
   * Constructor with a default queue capacity of 10000 objects.
   */
  protected AbstractIterable() {
    
    this(10000);
  }
  
  /**
   * Constructor.
   * @param queueCapacity maximum number of elements in the queue
   */
  protected AbstractIterable(int queueCapacity) {

    iterator = new AbstractIterableWorker<D>(this, queueCapacity);
    new Thread(iterator).start();
  }
  
  /**
   * Data provider method.
   * This method runs in a separate thread.
   * @throws Exception any exception
   */
  protected abstract void execute() throws Exception;
  
  /**
   * Adds an object to the queue.
   * Call this method within <code>execute</code>.
   * It is not allowed to pass <code>null</code>.
   * @param data object to add to the queue
   * @throws Exception any exception
   */
  public final void add(D data) throws Exception {
    
    iterator.add(data);
  }
  
  /**
   * Delivers the objects added by <code>execute</code>.
   */
  public final Iterator<D> iterator() {

    return iterator;
  }
}
