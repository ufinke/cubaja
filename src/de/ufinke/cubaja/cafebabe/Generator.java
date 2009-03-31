// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

public interface Generator {

  public String getClassName() throws Exception;
  
  public GenClass generate() throws Exception;
}
