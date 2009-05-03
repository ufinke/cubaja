// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

/**
 * <code>CsvException</code> handler.
 * @author Uwe Finke
 */
public interface ErrorHandler {

  /**
   * Handle an exception.
   * @param error
   * @throws CsvException
   */
  public void handleError(CsvException error) throws CsvException;
}
