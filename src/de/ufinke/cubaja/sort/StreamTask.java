// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;

class StreamTask<D> implements Callable<Info<D>> {

  private SynchronousQueue<Info<D>> queue;
  
  StreamTask(SynchronousQueue<Info<D>> queue) {
  
    this.queue = queue; 
  }

  public Info<D> call() throws Exception {

    boolean loop = true;
    
    while (loop) {
      
      Info<D> info = queue.take();
      
      switch (info.getAction()) {
        
        case PROCESS_INPUT:
          //TODO
          break;
          
        case END_INPUT:
          //TODO
          break;
      }
    }
    
    return null;
  }
}
