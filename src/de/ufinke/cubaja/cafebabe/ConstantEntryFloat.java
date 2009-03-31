// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

class ConstantEntryFloat implements Generatable {

  private float value;
  
  ConstantEntryFloat(float value) {
  
    this.value = value;
  }
  
  public int hashCode() {
    
    return Float.floatToRawIntBits(value);
  }
  
  public boolean equals(Object o) {
    
    if (! (o instanceof ConstantEntryFloat)) {
      return false;
    }
    ConstantEntryFloat other = (ConstantEntryFloat) o;
    return value== other.value;
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeByte(4);
    out.writeFloat(value);
  }
}
