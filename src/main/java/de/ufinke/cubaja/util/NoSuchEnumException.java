// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

/**
 * Exception thrown when there is no enum corresponding to a given value.
 * @author Uwe Finke
 */
public class NoSuchEnumException extends Exception {

  /**
   * Constructor.
   * @param message message text
   */
  public NoSuchEnumException(String message) {
    
    super(message);
  }
}
