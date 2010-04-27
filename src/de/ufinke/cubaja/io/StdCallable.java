// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.BufferedReader;
import java.util.concurrent.Callable;

class StdCallable implements Callable<Integer> {

  private BufferedReader reader;
  private StdProcessor processor;
  
  StdCallable(BufferedReader reader, StdProcessor processor) {
  
    this.reader = reader;
    this.processor = processor;
  }

  public Integer call() throws Exception {

    int count = 0;
    
    String line = reader.readLine();
    while (line != null) {
      count++;
      processor.processLine(line);
      line = reader.readLine();
    }
    reader.close();
    processor.close();
    
    return count;
  }
}
