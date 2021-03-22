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
   * @param out writer
   * @param config CSV configuration
   * @throws IOException passed from writer
   * @throws CsvException CSV formatting problem
   */
  public void init(Writer out, CsvConfig config) throws IOException, CsvException;
  
  /**
   * Terminates a row.
   * Normally, the formatter starts a new line.
   * @throws IOException passed from writer
   * @throws CsvException CSV formatting problem
   */
  public void writeRow() throws IOException, CsvException;
  
  /**
   * Writes a column including any needed separator and escape characters.
   * @param content column content
   * @throws IOException passed from writer
   * @throws CsvException CSV formatting problem
   */
  public void writeColumn(String content) throws IOException, CsvException;
  
  /**
   * Cleanup hook.
   * Note that the writer will be closed by <code>CsvWriter</code>.
   * @throws IOException passed from writer
   * @throws CsvException CSV formatting problem
   */
  public void finish() throws IOException, CsvException;
}
