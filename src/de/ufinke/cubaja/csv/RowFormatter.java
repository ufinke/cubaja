// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.Writer;

/**
 * Row formatter for <code>CsvWriter</code>.
 * @author Uwe Finke
 */
public interface RowFormatter {

  /**
   * Called during <code>CsvWriter</code> initialization.
   * @param out
   * @param config
   * @throws IOException
   * @throws CsvException
   */
  public void init(Writer out, CsvConfig config) throws IOException, CsvException;
  
  /**
   * Terminate a row.
   * Normally, the formatter starts a new line.
   * @throws IOException
   * @throws CsvException
   */
  public void writeRow() throws IOException, CsvException;
  
  /**
   * Write a column including any needed separator and escape characters.
   * @param content
   * @throws IOException
   * @throws CsvException
   */
  public void writeColumn(String content) throws IOException, CsvException;
}
