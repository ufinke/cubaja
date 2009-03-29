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
  
  String getParameterName() {
    
    return parameterName;
  }
  
  int getSize() {

    if (size == 0) {
      size = ("D".equals(getDescriptor()) || "J".equals(getDescriptor())) ? 2 : 1;
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
