// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

public interface OutputObjectHandler {

  public void write(BinaryOutputStream stream, Object object) throws Exception;
}
