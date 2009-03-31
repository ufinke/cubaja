// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

class ConstantEntryClass implements Generatable {

  private int utf8Index;
  
  ConstantEntryClass(int utf8Index) {
  
    this.utf8Index = utf8Index;
  }
  
  public int hashCode() {
    
    return utf8Index;
  }
  
  public boolean equals(Object o) {
    
    if (! (o instanceof ConstantEntryClass)) {
      return false;
    }
    ConstantEntryClass other = (ConstantEntryClass) o;
    return utf8Index == other.utf8Index;
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeByte(7);
    out.writeShort(utf8Index);
  }
}
