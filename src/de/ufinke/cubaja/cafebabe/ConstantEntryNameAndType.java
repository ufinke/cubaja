package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

class ConstantEntryNameAndType implements Generatable {

  private int nameIndex;
  private int descriptorIndex;
  
  ConstantEntryNameAndType(int nameIndex, int descriptorIndex) {
  
    this.nameIndex = nameIndex;
    this.descriptorIndex = descriptorIndex;
  }
  
  public int hashCode() {
    
    return nameIndex + descriptorIndex;
  }
  
  public boolean equals(Object o) {
    
    if (! (o instanceof ConstantEntryNameAndType)) {
      return false;
    }
    ConstantEntryNameAndType other = (ConstantEntryNameAndType) o;
    return nameIndex == other.nameIndex && descriptorIndex == other.descriptorIndex;
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeByte(12);
    out.writeShort(nameIndex);
    out.writeShort(descriptorIndex);
  }
}
