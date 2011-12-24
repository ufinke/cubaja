// Copyright (c) 2011, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class AbstractIterableWorker<E> implements Runnable, Iterator<E> {

  private AbstractIterable<E> abstractIterator;
  private BlockingQueue<E> queue;
  private volatile boolean running;
  private E data;
  
  public AbstractIterableWorker(AbstractIterable<E> abstractIterator, int queueSize) {
  
    this.abstractIterator = abstractIterator;
    queue = new LinkedBlockingQueue<E>(queueSize);
    running = true;
  }
  
  public boolean hasNext() {

    while (queue.size() == 0 && running) {
      try {
        Thread.sleep(100);
      } catch (Exception e) {
        throw new IteratorException(e);
      }
    }
    
    if (running) {
      try {
        data = queue.take();
      } catch (Exception e) {
        throw new IteratorException(e);
      }
    }
    
    return data != null;
  }

  public E next() {

    if (data != null) {
      return data;
    }
    
    throw new NoSuchElementException();
  }

  public void remove() {

    throw new UnsupportedOperationException();
  }
  
  public final void run() {

    try {
      abstractIterator.execute();
    } catch (Throwable t) {
      throw new IteratorException(t);
    }
    
    running = false;
  }
  
  public void add(E data) throws Exception {
    
    queue.put(data);
  }
}

