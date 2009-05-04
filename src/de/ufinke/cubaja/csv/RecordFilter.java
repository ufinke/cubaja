// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

/**
 * Record filter.
 * @author Uwe Finke
 */
public interface RecordFilter {

  /**
   * Signals whether a record should be processed.
   * A filter is used in
   * method <code>nextRecord</code> of class <code>CsvReader</code>.
   * If the result is <code>false</code>,
   * the reader reads the next record immediately.
   * @param reader
   * @return flag
   */
  public boolean acceptRecord(CsvReader reader);
}
