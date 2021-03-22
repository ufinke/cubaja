// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

/**
 * Processor of lines read by <code>StdReader</code>.
 * @author Uwe Finke
 */
public interface StdProcessor {

  /**
   * Process a line.
   * @param line string submitted by the reader
   * @throws Exception any exception during processing of the line
   */
  public void processLine(String line) throws Exception;
  
  /**
   * Cleanup when all lines have been processed.
   * @throws Exception when the processor could not be closed
   */
  public void close() throws Exception;
}
