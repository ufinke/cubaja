// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import de.ufinke.cubaja.util.Text;

/**
 * Signals an exception thrown by <tt>Sorter</tt>.
 * @author Uwe Finke
 */
public class SorterException extends RuntimeException {

  static private Text text = new Text(SorterException.class);
  
  /**
   * Constructor with forwarded cause.
   * @param cause
   */
  public SorterException(Throwable cause) {
    
    super(text.get("sorterException"), cause);
  }
  
  /**
   * Constructor.
   * @param msg
   */
  public SorterException(String msg) {
    
    super(msg);
  }
}
