// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

public interface InputObjectHandler {

  public Object read(BinaryInputStream stream, Class<?> clazz) throws Exception;
}
