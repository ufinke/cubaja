// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

/**
 * Processor of lines read by <tt>StdReader</tt>.
 * @author Uwe Finke
 */
public interface StdProcessor {

  /**
   * Process a line.
   * @param line
   * @throws Exception
   */
  public void processLine(String line) throws Exception;
  
  /**
   * Cleanup when all lines have been processed.
   * @throws Exception
   */
  public void close() throws Exception;
}
