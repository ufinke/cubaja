// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

/**
 * Signals a sort sequence violation.
 * @author Uwe Finke
 */
public class OutOfSequenceException extends RuntimeException {

  /**
   * Constructor.
   * @param msg a message text
   */
  public OutOfSequenceException(String msg) {
    
    super(msg);
  }
}
