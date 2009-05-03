// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

/**
 * <code>RuntimeException</code> wrapping another exception.
 * Needed because <code>Iterator</code> methods don't allow regular exceptions to be thrown.
 * @author Uwe Finke
 */
public class IteratorException extends RuntimeException {

  public IteratorException(String message, Throwable cause) {
    
    super(message, cause);
  }
  
  public IteratorException(Throwable cause) {
    
    super(cause.toString(), cause);
  }
}
