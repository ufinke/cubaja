// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PropertyClassAnalyzer {

  private List<PropertyDescription> propertyList;
  
  PropertyClassAnalyzer(Class<?> clazz) {

    Map<String, Class<?>> getterMap = new HashMap<String, Class<?>>();
    Map<String, Class<?>> setterMap = new HashMap<String, Class<?>>();
    
    for (Method method : clazz.getMethods()) {      
      if ((method.getModifiers() & Modifier.PUBLIC) != 0) {
        String name = method.getName();
        if (name.length() > 3) {
          if (name.startsWith("get")) {
            if (method.getParameterTypes().length == 0) {
              Class<?> type = method.getReturnType();
              if (type != Void.TYPE) {
                getterMap.put(name.substring(3), type);
              }
            }
          } else if (name.startsWith("set")) {
            if (method.getReturnType() == Void.TYPE) {
              if (method.getParameterTypes().length == 1) {
                Class<?> type = method.getParameterTypes()[0];
                setterMap.put(name.substring(3), type);
              }
            }
          }
        }
      }
    }
    
    propertyList = new ArrayList<PropertyDescription>();
    
    for (String name : setterMap.keySet()) {
      Class<?> type = getterMap.get(name);
      if (type != null && type == setterMap.get(name)) {
        propertyList.add(new PropertyDescription(name, type));
      } else {
        getterMap.remove(name);
      }
    }
    
    Collections.sort(propertyList);
  }
  
  List<PropertyDescription> getPropertyList() {
    
    return propertyList;
  }
  
  void checkIntersection(List<PropertyDescription> received) {

    HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
    for (PropertyDescription property : propertyList) {
      map.put(property.getName(), property.getType());
    }
    
    for (PropertyDescription property : received) {
      Class<?> type = map.get(property.getName());
      if (type == null || type != property.getType()) {
        property.markNoSetterPresent();
      }
    }
  }
}
