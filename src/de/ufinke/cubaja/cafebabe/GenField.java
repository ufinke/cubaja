// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

public class GenField implements Generatable, AccessFlags {

  private GenClass genClass;
  private int accessFlags;
  private int fieldNameIndex;
  private int descriptorIndex;
  private Generatable constantValue;
  
  GenField(GenClass genClass, int accessFlags, Type type, String fieldName) {
  
    this.genClass = genClass;
    this.accessFlags = accessFlags;
    fieldNameIndex = genClass.getConstantPool().addName(fieldName);
    descriptorIndex = genClass.getConstantPool().addFieldDescriptor(type);
  }
  
  public void setConstantValue(long value) {
    
    constantValue = new ConstantValueAttribute(genClass, genClass.getConstantPool().addLong(value));
  }
  
  public void setConstantValue(float value) {
    
    constantValue = new ConstantValueAttribute(genClass, genClass.getConstantPool().addFloat(value));
  }
  
  public void setConstantValue(double value) {
    
    constantValue = new ConstantValueAttribute(genClass, genClass.getConstantPool().addDouble(value));
  }
  
  public void setConstantValue(int value) {
    
    constantValue = new ConstantValueAttribute(genClass, genClass.getConstantPool().addInteger(value));
  }
  
  public void setConstantValue(boolean value) {
    
    constantValue = new ConstantValueAttribute(genClass, genClass.getConstantPool().addInteger(value ? 1 : 0));
  }
  
  public void setConstantValue(String value) {
    
    constantValue = new ConstantValueAttribute(genClass, genClass.getConstantPool().addString(value));
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeShort(accessFlags);
    out.writeShort(fieldNameIndex);
    out.writeShort(descriptorIndex);

    if (constantValue == null) {
      out.writeShort(0);
    } else {
      out.writeShort(1);
      constantValue.generate(out);
    }
  }
}
