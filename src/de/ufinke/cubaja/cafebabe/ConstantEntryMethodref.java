package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

class ConstantEntryMethodref implements Generatable {

  private int classIndex;
  private int nameAndTypeIndex;
  
  ConstantEntryMethodref(int classIndex, int nameAndTypeIndex) {
  
    this.classIndex = classIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }
  
  public int hashCode() {
    
    return classIndex + nameAndTypeIndex;
  }
  
  public boolean equals(Object o) {
    
    if (! (o instanceof ConstantEntryMethodref)) {
      return false;
    }
    ConstantEntryMethodref other = (ConstantEntryMethodref) o;
    return classIndex == other.classIndex && nameAndTypeIndex == other.nameAndTypeIndex;
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeByte(10);
    out.writeShort(classIndex);
    out.writeShort(nameAndTypeIndex);
  }
}
