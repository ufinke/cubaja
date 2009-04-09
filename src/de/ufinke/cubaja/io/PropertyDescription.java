// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import de.ufinke.cubaja.cafebabe.Type;

class PropertyDescription implements Comparable<PropertyDescription> {

  private String name;
  private Class<?> clazz;
  private Type type;
  private boolean noSetterPresent;

  PropertyDescription(String name, Class<?> clazz) {

    this.name = name;
    this.clazz = clazz;
  }
  
  public int compareTo(PropertyDescription other) {
    
    return name.compareTo(other.name);
  }

  String getName() {

    return name;
  }
  
  String getSetterName() {
    
    return "set" + name;
  }
  
  String getGetterName() {
    
    return "get" + name;
  }

  Class<?> getClazz() {

    return clazz;
  }
  
  Type getType() {
    
    if (type == null) {
      type = new Type(clazz);
    }
    return type;
  }
  
  void markNoSetterPresent() {
    
    noSetterPresent = true;
  }
  
  boolean isNoSetterPresent() {
    
    return noSetterPresent;
  }
}
