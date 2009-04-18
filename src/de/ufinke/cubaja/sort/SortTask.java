// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

class SortTask {

  Semaphore semaphore;
  WriteTask writeTask;
  
  private SortAlgorithm algorithm;
  private ExecutorService workerService;
  private Future<Object> result;
  
  SortTask(ExecutorService workerService, SortAlgorithm algorithm, WriteTask writeTask) {
    
    this.workerService = workerService;
    this.algorithm = algorithm;
    this.writeTask = writeTask;
    semaphore = new Semaphore(1);
  }
  
  SortArray sort(SortArray array) {
    
    Object[] result = algorithm.sort(array.getArray(), array.getSize());
    return new SortArray(result, array.getSize());
  }
  
  void checkException() throws Exception {
    
    writeTask.checkException();
    
    if (result != null && result.isDone()) {
      try {
        result.get();
      } catch (ExecutionException e) {
        throw new SortException(e.getCause());
      }
    }
  }
  
  void runWorker(final SortArray array) throws Exception {
    
    semaphore.acquire();
    
    result = workerService.submit(new Callable<Object>() {
     
      public Object call() throws Exception {
        
        writeTask.runWorker(sort(array));
        semaphore.release();
        return null;
      }
    });
  }

}
