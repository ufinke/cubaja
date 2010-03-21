// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

/**
 * Needed internally for <tt>CsvWriter.writeObject</tt>.
 * @author Uwe Finke
 */
public interface ObjectWriter {

  public void writeObject(CsvWriter writer, Object dataObject) throws Exception;
}
