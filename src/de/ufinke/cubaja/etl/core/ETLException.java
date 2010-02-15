// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.etl.core;

public class ETLException extends Exception {

  public ETLException(String message) {
    
    super(message);
  }
  
  public ETLException(String message, Throwable t) {
    
    super(message, t);
  }
}
