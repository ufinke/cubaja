// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Reader for <code>stdout</code> and <code>stderr</code> streams of a <code>Process</code>.
 * @author Uwe Finke
 */
public class StdReader {

  /**
   * Creates an instance to process <code>stdout</code>.
   * @param process runtime process
   * @param processor instance for processing stdout lines
   * @return instance
   */
  static public StdReader stdout(Process process, StdProcessor processor) {
    
    return new StdReader(process.getInputStream(), processor);
  }
  
  /**
   * Creates an instance to process <code>stderr</code>.
   * @param process runtime process
   * @param processor instance for processing stderr lines
   * @return instance
   */
  static public StdReader stderr(Process process, StdProcessor processor) {
    
    return new StdReader(process.getErrorStream(), processor);
  }
  
  private Future<Integer> future;
  private ExecutorService service;
  
  private StdReader(InputStream stream, StdProcessor processor) {

    BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(stream)));
    service = Executors.newSingleThreadExecutor();
    future = service.submit(new StdCallable(reader, processor));
  }
  
  /**
   * Waits for end of stream and releases resources.
   * @return number of lines processed
   * @throws Exception any exception during processing
   */
  public int waitFor() throws Exception {
    
    int result = future.get();
    service.shutdown();
    return result;
  }
}
