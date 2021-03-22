// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

/**
 * Interface needed internally for bytecode generation.
 * @author Uwe Finke
 */
public interface ObjectFactory {

  /**
   * Creates a data object.
   * @param reader reader
   * @return data object data object with setter methods
   * @throws CsvException when the object could not be created
   */
  public Object createObject(CsvReader reader) throws CsvException;
}
