// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

/**
 * Row filter.
 * @author Uwe Finke
 */
public interface RowFilter {

  /**
   * Signals whether a row should be processed.
   * A filter is used in
   * {@link CsvReader#nextRow()}.
   * If the result is <tt>false</tt>,
   * the reader reads the next row immediately.
   * @param reader
   * @return flag
   */
  public boolean acceptRow(CsvReader reader);
}
