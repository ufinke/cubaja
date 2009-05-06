// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import de.ufinke.cubaja.io.ColumnReader;

/**
 * Factory needed internaly for <code>CsvReader.readObject()</code>.
 * @author Uwe Finke
 */
public interface ObjectFactory {

  /**
   * Creates a data object.
   * @param reader
   * @return data object
   * @throws CsvException
   */
  public Object createObject(ColumnReader reader) throws CsvException;
}
