// Copyright (c) 2011, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.Iterator;

public abstract class AbstractIterable<D> implements Iterable<D> {

  private AbstractIterableWorker<D> iterator;
  
  protected AbstractIterable() {
    
    this(5000);
  }
  
  protected AbstractIterable(int queueSize) {

    iterator = new AbstractIterableWorker<D>(this, queueSize);
    new Thread(iterator).start();
  }
  
  protected abstract void execute() throws Exception;
  
  protected final void add(D data) throws Exception {
    
    iterator.add(data);
  }
  
  public final Iterator<D> iterator() {

    return iterator;
  }
}
