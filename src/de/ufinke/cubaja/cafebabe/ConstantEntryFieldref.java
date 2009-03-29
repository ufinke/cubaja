package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

class ConstantEntryFieldref implements Generatable {

  private int classIndex;
  private int nameAndTypeIndex;
  
  ConstantEntryFieldref(int classIndex, int nameAndTypeIndex) {
  
    this.classIndex = classIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }
  
  public int hashCode() {
    
    return classIndex + nameAndTypeIndex;
  }
  
  public boolean equals(Object o) {
    
    if (! (o instanceof ConstantEntryFieldref)) {
      return false;
    }
    ConstantEntryFieldref other = (ConstantEntryFieldref) o;
    return classIndex == other.classIndex && nameAndTypeIndex == other.nameAndTypeIndex;
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeByte(9);
    out.writeShort(classIndex);
    out.writeShort(nameAndTypeIndex);
  }
}
