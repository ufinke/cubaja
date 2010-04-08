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

  {
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
  
  static private Map<String, String> primitiveMap;
  
  private int dimensions;
  private String componentName;
  private String className;
  private String descriptor;
  private String parameterName;
  private int size;
  private boolean sizeInitialized;
  private int hashCode;
  
  /**
   * Defines a type by name.
   * @param type
   */
  public Type(String type) {
  
    componentName = type;
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
    componentName = type.getName();
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

    componentName = type.getName();
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
  
  /**
   * Tests equality to another <tt>Type</tt> instance.
   * A type instance equals to another if both have the same descriptor.
   */
  public boolean equals(Object o) {
    
    if (! (o instanceof Type)) {
      return false;
    }
    
    Type other = (Type) o;
    return getDescriptor().equals(other.getDescriptor());
  }
  
  /**
   * Returns the hash code.
   */
  public int hashCode() {
    
    if (hashCode == 0) {
      hashCode = getDescriptor().hashCode();
    }
    return hashCode;
  }
  
  /**
   * Returns a <tt>String</tt> representation.
   */
  public String toString() {
    
    return getDescriptor();
  }
  
  String getParameterName() {
    
    return parameterName;
  }
  
  /**
   * Returns the type's memory size in words.
   * <tt>long</tt> and <tt>double</tt> have size <tt>2</tt>,
   * <tt>void</tt> has size <tt>0</tt>
   * and all other types have size <tt>1</tt>.
   * @return size
   */
  public int getSize() {

    if (! sizeInitialized) {
      size = 1;
      String descriptor = getDescriptor();
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
      sizeInitialized = true;
    }
    
    return size;
  }
  
  String getClassName() {

    if (className == null && dimensions == 0 && primitiveMap.get(componentName) == null) {
      className = componentName.replace('.', '/');
    }
    return className;
  }
  
  String getDescriptor() {
    
    if (descriptor == null) {
      descriptor = primitiveMap.get(componentName);
      if (descriptor == null || dimensions > 0) {
        StringBuilder sb = new StringBuilder(100);
        for (int i = 0; i < dimensions; i++) {
          sb.append('[');
        }
        if (descriptor == null) {
          sb.append('L');
          sb.append(getClassName());
          sb.append(';');
        } else {
          sb.append(descriptor);
        }
        descriptor = sb.toString();
      }
    }
    return descriptor;
  }
}
