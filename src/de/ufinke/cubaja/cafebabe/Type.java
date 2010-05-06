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
  
  private int dimensions;
  private String className;
  private String descriptor;
  private String parameterName;
  private int size;
  
  /**
   * Defines a type by name.
   * @param type
   */
  public Type(String type) {
  
    initValues(type);
  }
  
  /**
   * Defines a type by name and gives it a name.
   * This constructor may be used to name arguments which are passed to a method.
   * @param type
   * @param parameterName
   */
  public Type(String type, String parameterName) {
    
    this(type);
    this.parameterName = parameterName;
  }
  
  /**
   * Defines a type by its class.
   * @param type
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
   * @param type
   * @param parameterName
   */
  public Type(Class<?> type, String parameterName) {
    
    this(type);
    this.parameterName = parameterName;
  }
  
  /**
   * Defines a type for a generated class.
   * @param type
   */
  public Type(GenClass type) {

    initValues(type.getName());
  }
  
  /**
   * Defines a type for a generated class and gives it a name.
   * This constructor may be used to name arguments which are passed to a method.
   * @param type
   * @param parameterName
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
   * Tests equality to another <tt>Type</tt> instance.
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
   * Returns a <tt>String</tt> representation.
   */
  public String toString() {
    
    return descriptor;
  }
  
  /**
   * Returns the type's memory size in words.
   * <tt>long</tt> and <tt>double</tt> have size <tt>2</tt>,
   * <tt>void</tt> has size <tt>0</tt>
   * and all other types have size <tt>1</tt>.
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
