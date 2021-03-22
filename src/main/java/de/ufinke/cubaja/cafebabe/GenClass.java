// Copyright (c) 2009 - 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for a generated class.
 * An instance is built by a {@link Generator}.
 * @author Uwe Finke
 */
public class GenClass implements Generatable, AccessFlags {

  static private final int MAJOR_VERSION = 49; // Java 1.5
  static private final int MINOR_VERSION = 0;
  
  private ConstantPool constantPool;
  private int accessFlags;
  private String className;
  private int classNameIndex;
  private Type superClass;
  private int superClassIndex;
  private int[] interfaceIndex;
  private List<GenField> fieldList;
  private List<GenMethod> methodList;
  
  /**
   * Constructor.
   * @param accessFlags access bits
   * @param className name of the class
   * @param superClass type of the classes super class
   * @param implementedInterfaces optional types of interfaces
   */
  public GenClass(int accessFlags, String className, Type superClass, Type... implementedInterfaces) {

    this.accessFlags = accessFlags | ACC_SUPER;
    this.className = className;
    this.superClass = superClass;
    
    constantPool = new ConstantPool();
    
    classNameIndex = constantPool.addClass(new Type(className)); 
    superClassIndex = constantPool.addClass(superClass);
    interfaceIndex = new int[implementedInterfaces.length];
    for (int i = 0; i < implementedInterfaces.length; i++) {
      interfaceIndex[i] = constantPool.addClass(implementedInterfaces[i]);
    }
        
    fieldList = new ArrayList<GenField>();
    methodList = new ArrayList<GenMethod>();
  }
  
  /**
   * Creates a field.
   * @param fieldAccessFlags access bits of the field
   * @param type type of the field
   * @param fieldName name of the field
   * @return field
   */
  public GenField createField(int fieldAccessFlags, Type type, String fieldName) {
    
    GenField field = new GenField(this, fieldAccessFlags, type, fieldName);
    fieldList.add(field);
    return field;
  }
  
  /**
   * Creates a method.
   * @param methodAccessFlags access bits of the method
   * @param returnType return type of the method
   * @param methodName name of the method
   * @param args argument types of the method
   * @return method
   */
  public GenMethod createMethod(int methodAccessFlags, Type returnType, String methodName, Type... args) {
    
    GenMethod method = new GenMethod(this, methodAccessFlags, returnType, methodName, args);
    methodList.add(method);
    return method;
  }
  
  /**
   * Creates a constructor.
   * @param methodAccessFlags access bits
   * @param args argument types of the constructor
   * @return constructor (method with name '&lt;init&gt;')
   */
  public GenMethod createConstructor(int methodAccessFlags, Type... args) {
    
    return createMethod(methodAccessFlags, new Type(Void.TYPE), "<init>", args);
  }
  
  /**
   * Creates an empty default constructor.
   */
  public void createDefaultConstructor() {
    
    GenMethod constructor = createConstructor(ACC_PUBLIC);
    
    CodeAttribute code = constructor.getCode();
    
    code.loadLocalReference(0);
    code.invokeSpecial(superClass, Type.VOID, "<init>");
    code.returnVoid();
  }
  
  /**
   * Returns the class name.
   * @return class name
   */
  public String getName() {
    
    return className;
  }
  
  ConstantPool getConstantPool() {
    
    return constantPool;
  }
  
  public void generate(DataOutputStream out) throws Exception {
        
    out.writeInt(0xCAFEBABE);
    
    out.writeShort(MINOR_VERSION);
    out.writeShort(MAJOR_VERSION);
    
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
