// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

class ConstantValueAttribute implements Generatable {

  private int nameIndex;
  private int valueIndex;
  
  ConstantValueAttribute(GenClass genClass, int valueIndex) {
    
    this.valueIndex = valueIndex;
    nameIndex = genClass.getConstantPool().addName("ConstantValue");
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeShort(nameIndex);
    out.writeInt(2);
    out.writeShort(valueIndex);
  }
}
