// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;

class AlgorithmTask implements Callable<SyncInfo> {

  private SynchronousQueue<SyncInfo> sortTaskQueue;
  private SynchronousQueue<SyncInfo> writeTaskQueue;
  
  AlgorithmTask(SynchronousQueue<SyncInfo> sortTaskQueue, SynchronousQueue<SyncInfo> writeTaskQueue) {
    
    this.sortTaskQueue = sortTaskQueue;
    this.writeTaskQueue = writeTaskQueue;
  }

  public SyncInfo call() throws Exception {

    boolean loop = true;
    
    while (loop) {
      
      SyncInfo syncInfo = sortTaskQueue.take();
      
      switch (syncInfo.getAction()) {
        
        case PROCESS_INPUT_ARRAY:
          InputInfo sortInfo = (InputInfo) syncInfo.getInfo();
          sortInfo.getAlgorithm().sort(sortInfo.getArray(), sortInfo.getArraySize());
          writeTaskQueue.put(syncInfo);
          break;
          
        case END:
          loop = false;
          break;
      }
    }
    
    return new SyncInfo(SyncAction.FINISHED, null);
  }
}
