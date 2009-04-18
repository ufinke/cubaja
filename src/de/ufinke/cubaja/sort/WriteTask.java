// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import de.ufinke.cubaja.io.BinaryOutputStream;

class WriteTask {

  Semaphore semaphore;
  Future<Object> result;
  
  private ExecutorService workerService;
  private FileManager fileManager;
  private BinaryOutputStream stream;
  
  WriteTask(ExecutorService workerService, FileManager fileManager) {
    
    this.workerService = workerService;
    this.fileManager = fileManager;
    semaphore = new Semaphore(1);
  }
  
  void checkException() throws Exception {
    
    if (result != null && result.isDone()) {
      try {
        result.get();
      } catch (ExecutionException e) {
        throw new SortException(e.getCause());
      }
    }
  }
  
  void write(SortArray array) throws Exception {
    
    if (stream == null) {
      stream = fileManager.createOutput();
    }
    
    Object[] objects = array.getArray();
    int size = array.getSize();
    
    for (int i = 0; i < size; i++) {
      stream.writeObject(objects[i]);
    }
  }
  
  void runWorker(final SortArray array) throws Exception {
    
    semaphore.acquire();

    result = workerService.submit(new Callable<Object>() {
      
      public Object call() throws Exception {
        
        write(array);
        semaphore.release();
        return null;
      }
    });
  }

}
