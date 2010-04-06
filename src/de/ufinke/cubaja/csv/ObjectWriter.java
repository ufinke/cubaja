// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

/**
 * Interface needed internally for bytecode generation.
 * @author Uwe Finke
 */
public interface ObjectWriter {

  public void writeObject(CsvWriter writer, Object dataObject) throws Exception;
}
