// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

/**
 * Factory needed internaly for <tt>CsvReader.readObject()</tt>.
 * @author Uwe Finke
 */
public interface ObjectFactory {

  /**
   * Creates a data object.
   * @param reader
   * @return data object
   * @throws CsvException
   */
  public Object createObject(CsvReader reader) throws CsvException;
}
