// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper for class and primitive type information.
 * @author Uwe Finke
 */
public class Type {

  static private final Map<String, String> primitiveMap;
  
  static {
    primitiveMap = new HashMap<String, String>(16);
    primitiveMap.put(Byte.TYPE.getName(), "B");
    primitiveMap.put(Character.TYPE.getName(), "C");
    primitiveMap.put(Double.TYPE.getName(), "D");
    primitiveMap.put(Float.TYPE.getName(), "F");
    primitiveMap.put(Integer.TYPE.getName(), "I");
    primitiveMap.put(Long.TYPE.getName(), "J");
    primitiveMap.put(Short.TYPE.getName(), "S");
    primitiveMap.put(Void.TYPE.getName(), "V");
    primitiveMap.put(Boolean.TYPE.getName(), "Z");
  }
  
  /**
   * Type representing <code>void</code>.
   */
  static public final Type VOID = new Type(Void.TYPE);
  
  /**
   * Type representing <code>boolean</code>.
   */
  static public final Type BOOLEAN = new Type(Boolean.TYPE);
  
  /**
   * Type representing <code>byte</code>.
   */
  static public final Type BYTE = new Type(Byte.TYPE);
  
  /**
   * Type representing <code>short</code>.
   */
  static public final Type SHORT = new Type(Short.TYPE);
  
  /**
   * Type representing <code>int</code>.
   */
  static public final Type INT = new Type(Integer.TYPE);
  
  /**
   * Type representing <code>long</code>.
   */
  static public final Type LONG = new Type(Long.TYPE);
  
  /**
   * Type representing <code>float</code>.
   */
  static public final Type FLOAT = new Type(Float.TYPE);
  
  /**
   * Type representing <code>double</code>.
   */
  static public final Type DOUBLE = new Type(Double.TYPE);
  
  /**
   * Type representing <code>char</code>.
   */
  static public final Type CHAR = new Type(Character.TYPE);
  
  /**
   * Type representing <code>String</code>.
   */
  static public final Type STRING = new Type(String.class);
  
  /**
   * Type representing <code>Object</code>.
   */
  static public final Type OBJECT = new Type(Object.class);
  
  /**
   * Type representing <code>Class</code>.
   */
  static public final Type CLASS = new Type(Class.class);
  
  private int dimensions;
  private String className;
  private String descriptor;
  private String parameterName;
  private int size;
  
  /**
   * Defines a type by name.
   * @param type name of type
   */
  public Type(String type) {
  
    initValues(type);
  }
  
  /**
   * Defines a type by name and gives it a name.
   * This constructor may be used to name arguments which are passed to a method.
   * @param type name of type
   * @param parameterName name of argument
   */
  public Type(String type, String parameterName) {
    
    this(type);
    this.parameterName = parameterName;
  }
  
  /**
   * Defines a type by its class.
   * @param type class of type
   */
  public Type(Class<?> type) {

    while (type.isArray()) {
      dimensions++;
      type = type.getComponentType();
    }
    initValues(type.getName());
  }
  
  /**
   * Defines a type by class and gives it a name.
   * This constructor may be used to name arguments which are passed to a method.
   * @param type class of type
   * @param parameterName name of argument
   */
  public Type(Class<?> type, String parameterName) {
    
    this(type);
    this.parameterName = parameterName;
  }
  
  /**
   * Defines a type for a generated class.
   * @param type generated class of type
   */
  public Type(GenClass type) {

    initValues(type.getName());
  }
  
  /**
   * Defines a type for a generated class and gives it a name.
   * This constructor may be used to name arguments which are passed to a method.
   * @param type generated class of type
   * @param parameterName name of argument
   */
  public Type(GenClass type, String parameterName) {
    
    this(type);
    this.parameterName = parameterName;
  }
  
  private void initValues(String componentName) {

    className = componentName.replace('.', '/');
    
    descriptor = primitiveMap.get(componentName);    
    if (descriptor == null || dimensions > 0) {
      StringBuilder sb = new StringBuilder(100);
      for (int i = 0; i < dimensions; i++) {
        sb.append('[');
      }
      if (descriptor == null) {
        sb.append('L');
        sb.append(className);
        sb.append(';');
      } else {
        sb.append(descriptor);
      }
      descriptor = sb.toString();
    }
    
    size = 1;
    if (descriptor.length() == 1) {
      switch (descriptor.charAt(0)) {
        case 'V':
          size = 0;
          break;
        case 'D':
        case 'J':
          size = 2;
          break;
      }
    }
  }
  
  /**
   * Tests equality to another <code>Type</code> instance.
   * A type instance equals to another if both have the same descriptor.
   */
  public boolean equals(Object o) {
    
    if (! (o instanceof Type)) {
      return false;
    }
    
    Type other = (Type) o;
    return descriptor.equals(other.descriptor);
  }
  
  /**
   * Returns the hash code.
   */
  public int hashCode() {
    
    return descriptor.hashCode();
  }
  
  /**
   * Returns a <code>String</code> representation.
   */
  public String toString() {
    
    return descriptor;
  }
  
  /**
   * Returns the type's memory size in words.
   * <code>long</code> and <code>double</code> have size <code>2</code>,
   * <code>void</code> has size <code>0</code>
   * and all other types have size <code>1</code>.
   * @return size
   */
  public int getSize() {

    return size;
  }
  
  String getDescriptor() {
    
    return descriptor;
  }
  
  String getClassName() {

    return className;
  }
  
  String getParameterName() {
    
    return parameterName;
  }
  
}
