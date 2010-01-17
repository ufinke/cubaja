// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import de.ufinke.cubaja.util.Text;

public class SorterException extends RuntimeException {

  static private Text text = new Text(SorterException.class);
  
  public SorterException(Throwable cause) {
    
    super(text.get("sorterException"), cause);
  }
  
  public SorterException(String msg) {
    
    super(msg);
  }
}
