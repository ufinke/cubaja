// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;

/**
 * Wrapper for a generated field.
 * An instance is created by {@link GenClass#createField}.
 * The methods give the opportunity to assign constant values.
 * @author Uwe Finke
 */
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
  
  /**
   * Assign a <code>long</code> constant.
   * @param value long value
   */
  public void setConstantValue(long value) {
    
    constantValue = new ConstantValueAttribute(genClass, genClass.getConstantPool().addLong(value));
  }
  
  /**
   * Assign a <code>float</code> constant.
   * @param value float value
   */
  public void setConstantValue(float value) {
    
    constantValue = new ConstantValueAttribute(genClass, genClass.getConstantPool().addFloat(value));
  }
  
  /**
   * Assign a <code>double</code> constant.
   * @param value double value
   */
  public void setConstantValue(double value) {
    
    constantValue = new ConstantValueAttribute(genClass, genClass.getConstantPool().addDouble(value));
  }
  
  /**
   * Assign an <code>integer</code> constant.
   * @param value int value
   */
  public void setConstantValue(int value) {
    
    constantValue = new ConstantValueAttribute(genClass, genClass.getConstantPool().addInteger(value));
  }
  
  /**
   * Assign a <code>boolean</code> constant.
   * @param value boolean value
   */
  public void setConstantValue(boolean value) {
    
    constantValue = new ConstantValueAttribute(genClass, genClass.getConstantPool().addInteger(value ? 1 : 0));
  }
  
  /**
   * Assign a <code>String</code> constant.
   * @param value string value
   */
  public void setConstantValue(String value) {
    
    constantValue = new ConstantValueAttribute(genClass, genClass.getConstantPool().addString(value));
  }
  
  public void generate(DataOutputStream out) throws Exception {
    
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
