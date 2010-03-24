// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;

interface Generatable {

  /**
   * Writes generated result to a stream.
   * @param out
   * @throws Exception
   */
  public void generate(DataOutputStream out) throws Exception;
}
