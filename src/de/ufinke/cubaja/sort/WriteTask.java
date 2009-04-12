// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;

class WriteTask implements Callable<SyncInfo> {

  private SynchronousQueue<SyncInfo> queue;
  
  WriteTask(SynchronousQueue<SyncInfo> queue) {
  
    this.queue = queue; 
  }

  public SyncInfo call() throws Exception {

    boolean loop = true;
    
    while (loop) {
      
      SyncInfo syncInfo = queue.take();
      
      switch (syncInfo.getAction()) {
        
        case PROCESS_INPUT_ARRAY:
          //TODO
          break;
          
        case END:
          loop = false;
          break;
      }
    }
    
    return new SyncInfo(SyncAction.FINISHED, null);
  }
}
