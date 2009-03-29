package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

class ConstantEntryUtf8 implements Generatable {

  private String string;
  
  ConstantEntryUtf8(String string) {
  
    this.string = string;
  }
  
  public int hashCode() {
    
    return string.hashCode();
  }
  
  public boolean equals(Object o) {
    
    if (! (o instanceof ConstantEntryUtf8)) {
      return false;
    }
    ConstantEntryUtf8 other = (ConstantEntryUtf8) o;
    return string.equals(other.string);
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeByte(1);
    out.writeUTF(string);
  }
}
