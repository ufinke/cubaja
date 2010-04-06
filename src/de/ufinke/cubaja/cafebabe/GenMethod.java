// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for a generated method.
 * An instance is created by {@link GenClass#createField createMethod} 
 * or {@link GenClass#createField createConstructor} in <tt>GenClass</tt>.
 * @author Uwe Finke
 */
public class GenMethod implements Generatable, AccessFlags {

  private GenClass genClass;
  private Type returnType;
  private String methodName;
  private Type[] args;
  private int accessFlags;
  private int methodNameIndex;
  private int descriptorIndex;
  private List<Generatable> attributeList;
  private CodeAttribute code;
  private ExceptionAttribute exceptionAttribute;
  
  GenMethod(GenClass genClass, int accessFlags, Type returnType, String methodName, Type[] args) {
  
    this.genClass = genClass;
    this.accessFlags = accessFlags;
    this.returnType = returnType;
    this.methodName = methodName;
    this.args = args;
    
    methodNameIndex = genClass.getConstantPool().addName(methodName);
    descriptorIndex = genClass.getConstantPool().addMethodDescriptor(returnType, args);
    
    attributeList = new ArrayList<Generatable>();
    code = new CodeAttribute(genClass, (accessFlags & AccessFlags.ACC_STATIC) != 0, args);
    attributeList.add(code);
  }
  
  /**
   * Gives access to the methods code.
   * @return code attribute
   */
  public CodeAttribute getCode() {
    
    return code;
  }
  
  /**
   * Adds an exception.
   * This corresponds to an entry in the <tt>throws</tt> clause of the method.
   * @param exception
   */
  public void addException(Type exception) {
    
    if (exceptionAttribute == null) {
      exceptionAttribute = new ExceptionAttribute(genClass);
      attributeList.add(exceptionAttribute);
    }
    exceptionAttribute.addException(exception);
  }
  
  /**
   * Creates a bridge method which calls this method.
   * The bridge method is added to the class.
   * In a bridge method, the generic arguments are casted to the 
   * argument types of the original method.
   * @param genericReturnType
   * @param genericArgs
   */
  public void createGenericBridge(Type genericReturnType, Type... genericArgs) {
    
    GenMethod bridge = genClass.createMethod(accessFlags | ACC_BRIDGE | ACC_SYNTHETIC, genericReturnType, methodName, genericArgs);
    
    CodeAttribute code = bridge.getCode();
    
    code.loadLocalReference(0);
    
    for (int i = 0; i < args.length; i++) {
      switch (args[i].getDescriptor().charAt(0)) {
        case 'I':
        case 'C':
        case 'S':
        case 'B':
        case 'Z':
          code.loadLocalInt(i + 1);
          break;
        case 'J':
          code.loadLocalLong(i + 1);
          break;
        case 'D':
          code.loadLocalDouble(i + 1);
          break;
        case 'F':
          code.loadLocalFloat(i + 1);
          break;
        default:
          code.loadLocalReference(i + 1);
          if (! genericArgs[i].equals(args[i])) {
            code.cast(args[i]);
          }
      }  
    }
    
    code.invokeVirtual(new Type(genClass), returnType, methodName, args);
    
    switch (genericReturnType.getDescriptor().charAt(0)) {
      case 'V':
        code.returnVoid();
        break;
      case 'I':
      case 'C':
      case 'S':
      case 'B':
      case 'Z':
        code.returnInt();
        break;
      case 'J':
        code.returnLong();
        break;
      case 'D':
        code.returnDouble();
        break;
      case 'F':
        code.returnFloat();
        break;
      default:
        code.returnReference();
    }
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
