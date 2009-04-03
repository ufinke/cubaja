// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GenMethod implements Generatable, AccessFlags {

  private GenClass genClass;
  private int accessFlags;
  private int methodNameIndex;
  private int descriptorIndex;
  private List<Generatable> attributeList;
  private CodeAttribute code;
  private ExceptionAttribute exceptionAttribute;
  
  GenMethod(GenClass genClass, int accessFlags, Type returnType, String methodName, Type[] args) {
  
    this.genClass = genClass;
    this.accessFlags = accessFlags;
    
    methodNameIndex = genClass.getConstantPool().addName(methodName);
    descriptorIndex = genClass.getConstantPool().addMethodDescriptor(returnType, args);
    
    attributeList = new ArrayList<Generatable>();
    code = new CodeAttribute(genClass, (accessFlags & AccessFlags.ACC_STATIC) != 0, args);
    attributeList.add(code);
  }
  
  public CodeAttribute getCode() {
    
    return code;
  }
  
  public void addException(Type exception) {
    
    if (exceptionAttribute == null) {
      exceptionAttribute = new ExceptionAttribute(genClass);
      attributeList.add(exceptionAttribute);
    }
    exceptionAttribute.addException(exception);
  }
  
  public void generate(DataOutputStream out) throws Exception {
    
    out.writeShort(accessFlags);
    out.writeShort(methodNameIndex);
    out.writeShort(descriptorIndex);
    
    out.writeShort(attributeList.size());
    for (Generatable attribute : attributeList) {
      attribute.generate(out);
    }
  }
}
