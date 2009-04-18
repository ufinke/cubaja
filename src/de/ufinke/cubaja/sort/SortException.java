// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import de.ufinke.cubaja.util.Text;

public class SortException extends RuntimeException {

  static private final Text text = new Text(SortException.class);
  
  SortException(Throwable t) {
  
    super(text.get("sortException", t.toString()), t);
  }
}
