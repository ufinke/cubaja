// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

/**
 * Default <code>ErrorHandler</code>.
 * @author Uwe Finke
 */
public class DefaultErrorHandler implements ErrorHandler {

  /**
   * Constructor.
   */
  public DefaultErrorHandler() {
 
  }
  
  /**
   * Throws the passed exception.
   */
  public void handleError(CsvException error) throws CsvException {

    throw error;
  }   
}
