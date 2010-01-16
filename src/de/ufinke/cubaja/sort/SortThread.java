// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.concurrent.*;

class SortThread extends Thread {

  private final Info info;
  private final SortAlgorithm algorithm;
  private boolean loop;
  
  public SortThread(Info info) {
    
    this.info = info;
    algorithm = info.getConfig().getAlgorithm();
  }
  
  public void run() {
    
    try {
      work();
    } catch (Throwable t) {
      info.setError(t);
    }
  }
  
  private void work() throws Exception {

    BlockingQueue<Request> queue = info.getSortQueue();
    loop = true;
    
    while (loop) {
      Request request = queue.poll(1, TimeUnit.SECONDS);
      testError();
      if (request != null) {
        handleRequest(request);
      }
    }
  }
  
  private void handleRequest(final Request request) throws Exception {
    
    switch (request.getType()) {
      case SORT_ARRAY:
        sortArray((SortArray) request.getData());
        break;
    }
  }
  
  private void sortArray(final SortArray sortArray) {

    algorithm.sort(sortArray.getArray(), sortArray.getSize());
  }
  
  private void testError() {
    
    if (info.hasError()) {
      loop = false;
    }
  }
}
