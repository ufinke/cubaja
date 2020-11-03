// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

class ConstantEntryInteger implements Generatable {

  private int value;
  
  ConstantEntryInteger(int value) {
  
    this.value = value;
  }
  
  public int hashCode() {
    
    return value;
  }
  
  public boolean equals(Object o) {
    
    if (! (o instanceof ConstantEntryInteger)) {
      return false;
    }
    ConstantEntryInteger other = (ConstantEntryInteger) o;
    return value == other.value;
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeByte(3);
    out.writeInt(value);
  }
}
