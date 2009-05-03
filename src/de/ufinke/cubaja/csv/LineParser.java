// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.Reader;

/**
 * Line parser.
 * @author Uwe Finke
 */
public interface LineParser {

  /**
   * Called during <code>CsvReader</code> initialization.
   * @param reader
   * @param config
   * @throws CsvException
   */
  public void init(Reader in, CsvConfig config) throws IOException, CsvException;

  /**
   * Returns next line, or <code>null</code> when EOF.
   * @return line
   * @throws IOException
   */
  public String readLine() throws IOException, CsvException;
  
  /**
   * Returns a column.
   * @param index
   * @return net column content
   * @throws CsvException
   */
  public String getColumn(int index) throws CsvException;

  /**
   * Returns number of columns in line.
   * @return column count
   */
  public int getColumnCount();
  
}
