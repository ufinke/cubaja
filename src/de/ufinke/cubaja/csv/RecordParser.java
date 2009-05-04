// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.Reader;

/**
 * Record parser.
 * @author Uwe Finke
 */
public interface RecordParser {

  /**
   * Called during <code>CsvReader</code> initialization.
   * @param in
   * @param config
   * @throws CsvException
   */
  public void init(Reader in, CsvConfig config) throws IOException, CsvException;

  /**
   * Returns next record, or <code>null</code> when EOF.
   * @return complete record line
   * @throws IOException
   */
  public String readRecord() throws IOException, CsvException;
  
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
   * Returns number of columns in record.
   * @return column count
   */
  public int getColumnCount();
  
  /**
   * Returns whether all columns are empty.
   * @return flag
   */
  public boolean isEmptyRecord();
  
}
