// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.concurrent.*;

class SortThread extends Thread {

  private Info info;
  private boolean loop;
  
  public SortThread(Info info) {
    
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
  
  private void handleRequest(Request request) throws Exception {
    
  }
  
  private void testError() {
    
    if (info.hasError()) {
      loop = false;
    }
  }
}
