package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

class ConstantEntryInterfaceMethodref implements Generatable {

  private int classIndex;
  private int nameAndTypeIndex;
  
  ConstantEntryInterfaceMethodref(int classIndex, int nameAndTypeIndex) {
  
    this.classIndex = classIndex;
    this.nameAndTypeIndex = nameAndTypeIndex;
  }
  
  public int hashCode() {
    
    return classIndex + nameAndTypeIndex;
  }
  
  public boolean equals(Object o) {
    
    if (! (o instanceof ConstantEntryInterfaceMethodref)) {
      return false;
    }
    ConstantEntryInterfaceMethodref other = (ConstantEntryInterfaceMethodref) o;
    return classIndex == other.classIndex && nameAndTypeIndex == other.nameAndTypeIndex;
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeByte(11);
    out.writeShort(classIndex);
    out.writeShort(nameAndTypeIndex);
  }
}
