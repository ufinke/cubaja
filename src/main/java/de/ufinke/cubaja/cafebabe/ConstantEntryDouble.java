// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

class ConstantEntryDouble implements Generatable {

  private double value;
  
  ConstantEntryDouble(double value) {
  
    this.value = value;
  }
  
  public int hashCode() {
    
    return (int) Double.doubleToRawLongBits(value);
  }
  
  public boolean equals(Object o) {
    
    if (! (o instanceof ConstantEntryDouble)) {
      return false;
    }
    ConstantEntryDouble other = (ConstantEntryDouble) o;
    return value == other.value;
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeByte(6);
    out.writeDouble(value);
  }
}
