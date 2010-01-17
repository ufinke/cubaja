// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;


final class FileTask implements Runnable {

  private SortManager info;
  
  public FileTask(SortManager info) {
    
  }
  
  public void run() {
    
    try {
      work();
    } catch (Throwable t) {
      info.setError(t);
    }
  }
  
  private void work() throws Exception {
    
  }
}
