// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

class ConstantEntryLong implements Generatable {

  private long value;
  
  ConstantEntryLong(long value) {
  
    this.value = value;
  }
  
  public int hashCode() {
    
    return (int) value;
  }
  
  public boolean equals(Object o) {
    
    if (! (o instanceof ConstantEntryLong)) {
      return false;
    }
    ConstantEntryLong other = (ConstantEntryLong) o;
    return value== other.value;
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeByte(5);
    out.writeLong(value);
  }
}
