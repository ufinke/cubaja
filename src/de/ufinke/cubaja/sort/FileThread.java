// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;


class FileThread extends Thread {

  private Info info;
  
  public FileThread(Info info) {
    
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
