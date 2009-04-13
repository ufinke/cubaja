// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;

class SortTask<D> implements Callable<Info<D>> {

  private SortAlgorithm<D> algorithm;
  private SynchronousQueue<Info<D>> sortTaskQueue;
  private SynchronousQueue<Info<D>> writeTaskQueue;
  
  SortTask(SortAlgorithm<D> algorithm, SynchronousQueue<Info<D>> sortTaskQueue, SynchronousQueue<Info<D>> writeTaskQueue) {
    
    this.algorithm = algorithm;
    this.sortTaskQueue = sortTaskQueue;
    this.writeTaskQueue = writeTaskQueue;
  }

  public Info<D> call() throws Exception {

    boolean loop = true;
    
    while (loop) {
      
      Info<D> info = sortTaskQueue.take();
      
      switch (info.getAction()) {
        
        case PROCESS_INPUT:
          SortArray<D> array = info.getArray();
          algorithm.sort(array.getArray(), array.getSize());
          writeTaskQueue.put(info);
          break;
          
        case END:
          loop = false;
          break;
      }
    }
    
    return null;
  }
}
