package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

class ConstantEntryString implements Generatable {

  private int utf8Index;
  
  ConstantEntryString(int utf8Index) {
  
    this.utf8Index = utf8Index;
  }
  
  public int hashCode() {
    
    return utf8Index;
  }
  
  public boolean equals(Object o) {
    
    if (! (o instanceof ConstantEntryClass)) {
      return false;
    }
    ConstantEntryString other = (ConstantEntryString) o;
    return utf8Index == other.utf8Index;
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeByte(8);
    out.writeShort(utf8Index);
  }
}
