// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.util.HashMap;
import java.util.Map;

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
  
  public Type(String type) {
  
    componentName = type;
  }
  
  public Type(String type, String parameterName) {
    
    this(type);
    this.parameterName = parameterName;
  }
  
  public Type(Class<?> type) {

    while (type.isArray()) {
      dimensions++;
      type = type.getComponentType();
    }
    componentName = type.getName();
  }
  
  public Type(Class<?> type, String parameterName) {
    
    this(type);
    this.parameterName = parameterName;
  }
  
  public Type(GenClass type) {

    componentName = type.getName();
  }
  
  public Type(GenClass type, String parameterName) {
    
    this(type);
    this.parameterName = parameterName;
  }
  
  public boolean equals(Object o) {
    
    if (! (o instanceof Type)) {
      return false;
    }
    
    Type other = (Type) o;
    return getDescriptor().equals(other.getDescriptor());
  }
  
  String getParameterName() {
    
    return parameterName;
  }
  
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
