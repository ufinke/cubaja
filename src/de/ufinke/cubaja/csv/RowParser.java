// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.Reader;

/**
 * Row parser.
 * A parser recognizes column delimiting separators and escape characters.
 * @author Uwe Finke
 */
public interface RowParser {

  /**
   * Called during <tt>CsvReader</tt> initialization.
   * @param in
   * @param config
   * @throws CsvException
   */
  public void init(Reader in, CsvConfig config) throws IOException, CsvException;

  /**
   * Returns next row, or <tt>null</tt> when EOF.
   * @return complete row
   * @throws IOException
   */
  public String readRow() throws IOException, CsvException;
  
  /**
   * Returns the number of raw lines read so far.
   * @return line count
   */
  public int getLineCount();
  
  /**
   * Returns a column.
   * @param index
   * @return net column content
   * @throws CsvException
   */
  public String getColumn(int index) throws CsvException;

  /**
   * Returns number of columns in row.
   * @return column count
   */
  public int getColumnCount();
  
  /**
   * Returns whether all columns are empty.
   * @return flag
   */
  public boolean isEmptyRow();
  
}
