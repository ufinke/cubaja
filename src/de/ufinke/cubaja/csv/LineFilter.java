// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

/**
 * Line filter.
 * @author Uwe Finke
 */
public interface LineFilter {

  /**
   * Signals whether a line should be processed.
   * A line filter is used in
   * method <code>nextLine</code> of class <code>CsvReader</code>.
   * If the result is <code>false</code>,
   * the reader reads the next line immediately.
   * @param reader
   * @return flag
   */
  public boolean acceptLine(CsvReader reader);
}
