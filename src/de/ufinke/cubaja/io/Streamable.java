// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

public interface Streamable {

  public void write(BinaryOutputStream stream) throws Exception;
  
  public void read(BinaryInputStream stream) throws Exception;
}
