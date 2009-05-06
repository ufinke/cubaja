// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

/**
 * Column content editor.
 * @author Uwe Finke
 */
public interface ColumnEditor {

  /**
   * Edits the column.
   * @param column
   * @param colConfig
   * @return edited column content
   * @throws CsvException
   */
  public String editColumn(String column, ColConfig colConfig) throws CsvException;
}
