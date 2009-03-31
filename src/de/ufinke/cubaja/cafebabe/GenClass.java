// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenClass implements Generatable, AccessFlags {

  {
    String version = System.getProperty("java.class.version");
    int index = version.indexOf('.');
    majorVersion = Integer.parseInt(version.substring(0, index));
    minorVersion = Integer.parseInt(version.substring(index + 1));
  }
  
  static private int majorVersion;
  static private int minorVersion;
  
  private ConstantPool constantPool;
  private int accessFlags;
  private String className;
  private int classNameIndex;
  private int superClassIndex;
  private int[] interfaceIndex;
  private List<GenField> fieldList;
  private List<GenMethod> methodList;
  
  public GenClass(int accessFlags, String className, Type superClass, Type... interfaces) {

    this.accessFlags = accessFlags;
    this.className = className;
    
    constantPool = new ConstantPool();
    
    classNameIndex = constantPool.addClass(new Type(className)); 
    superClassIndex = constantPool.addClass(superClass);
    interfaceIndex = new int[interfaces.length];
    for (int i = 0; i < interfaces.length; i++) {
      interfaceIndex[i] = constantPool.addClass(interfaces[i]);
    }
        
    fieldList = new ArrayList<GenField>();
    methodList = new ArrayList<GenMethod>();
  }
  
  public GenField createField(int fieldAccessFlags, Type type, String fieldName) {
    
    GenField field = new GenField(this, fieldAccessFlags, type, fieldName);
    fieldList.add(field);
    return field;
  }
  
  public GenMethod createMethod(int methodAccessFlags, Type returnType, String methodName, Type... args) {
    
    GenMethod method = new GenMethod(this, methodAccessFlags, returnType, methodName, args);
    methodList.add(method);
    return method;
  }
  
  public GenMethod createConstructor(int methodAccessFlags, Type... args) {
    
    return createMethod(methodAccessFlags, new Type(Void.TYPE), "<init>", args);
  }
  
  public String getName() {
    
    return className;
  }
  
  ConstantPool getConstantPool() {
    
    return constantPool;
  }
  
  public void generate(DataOutputStream out) throws IOException {
        
    out.writeInt(0xCAFEBABE);
    
    out.writeShort(minorVersion);
    out.writeShort(majorVersion);
    
    constantPool.generate(out);
    
    out.writeShort(accessFlags);
    out.writeShort(classNameIndex);
    out.writeShort(superClassIndex);

    out.writeShort(interfaceIndex.length);
    for (int i = 0; i < interfaceIndex.length; i++) {
      out.writeShort(interfaceIndex[i]);
    }
    
    out.writeShort(fieldList.size());
    for (GenField field : fieldList) {
      field.generate(out);
    }
    
    out.writeShort(methodList.size());
    for (GenMethod method : methodList) {
      method.generate(out);
    }
    
    out.writeShort(0); // attributes
  }
}
