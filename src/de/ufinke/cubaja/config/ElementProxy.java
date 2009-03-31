// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import de.ufinke.cubaja.util.Text;
import de.ufinke.cubaja.util.Util;

class ElementProxy {

  static private Text text = new Text(ElementProxy.class);
  static private String EMPTY_STRING = "";
  
  private String name;
  private ElementKind kind;
  private Object node;
  private boolean managedNode;
  private boolean dynamicNode;
  private Map<String, MethodProxy> methodMap;
  private MethodProxy parentMethod;
  private MethodProxy charDataMethod;
  private ParameterFactory factory;
  private StringBuilder charData;
  private boolean cdata;
  
  ElementProxy(String name, ElementKind kind) {
  
    this.name = name;
    this.kind = kind;
  }
  
  String getName() {
    
    return name;
  }
  
  void setKind(ElementKind kind) {
    
    this.kind = kind;
  }
  
  ElementKind getKind() {
    
    return kind;
  }
  
  void setFactory(ParameterFactory factory) {
    
    this.factory = factory;
  }
  
  ParameterFactory getFactory() {
    
    return factory;
  }
  
  void toggleCData() {
  
    ensureCharData();
    
    charData.append('\uFFFF');
    cdata = ! cdata;
  }
  
  void addCharData(char[] ch, int start, int length) {
    
    ensureCharData();
    
    int end = start + length;
    for (int i = start; i < end; i++) {
      if (ch[i] == '\n' && (! cdata)) {
        charData.append(' ');
      } else {
        charData.append(ch[i]);
      }
    }
  }
  
  private void ensureCharData() {
    
    if (charData == null) {
      charData = new StringBuilder(100);
    }
  }
  
  String getCharData() {
    
    return charData == null ? EMPTY_STRING : charData.toString();
  }
  
  void setNode(Object node) throws ConfigException {
    
    this.node = node;
    
    for (Class<?> implementedInterface : node.getClass().getInterfaces()) {
      if (ManagedElement.class.isAssignableFrom(implementedInterface)) {
        managedNode = true;
      }
      if (DynamicElement.class.isAssignableFrom(implementedInterface)) {
        dynamicNode = true;
      }
    }
    
    methodMap = new HashMap<String, MethodProxy>();
    for (Method method : node.getClass().getMethods()) {
      String methodName = method.getName();
      if (methodName.startsWith("set") || methodName.startsWith("add")) {
        checkMethod(method);
      }
    }
  }
  
  boolean isManagedNode() {
    
    return managedNode;
  }
  
  boolean isDynamicNode() {
    
    return dynamicNode;
  }
  
  private void checkMethod(Method method) throws ConfigException {
    
    if (method.getName().length() == 3) {
      return;
    }
    
    if (method.getParameterTypes().length != 1) {
      return;
    }
    
    if (method.getReturnType() != Void.TYPE) {
      return;
    }
    
    if (method.isAnnotationPresent(NoConfig.class)) {
      return;
    }

    MethodProxy entry = new MethodProxy(method);
    
    String searchName = method.getName().substring(3);
    
    if (methodMap.put(searchName, entry) != null) {
      throw new ConfigException(text.get("duplicateMethod", method.getName()));
    }
    
    if (entry.isCharData()) {
      charDataMethod = entry;
    }
  }
  
  Object getNode() {
    
    return node;
  }
  
  MethodProxy findMethod(String methodName) throws ConfigException {
    
    MethodProxy result = methodMap.get(Util.createMethodName(methodName, null));
    
    if (result == null && node != null && dynamicNode) {
      DynamicElement dynamicNode = (DynamicElement) node;
      String alternateName = dynamicNode.alternateName(methodName);
      if (alternateName != null) {        
        result = methodMap.get(Util.createMethodName(alternateName, null));
      }
    }
    
    if (result == null) {
      throw new ConfigException(text.get("unexpectedElement", name));
    }
    
    return result;
  }
  
  void setParentMethod(MethodProxy parentMethod) {
    
    this.parentMethod = parentMethod;
  }
  
  MethodProxy getParentMethod() {
    
    return parentMethod;
  }
  
  MethodProxy getCharDataMethod() {
    
    return charDataMethod;
  }
  
  void checkMandatory() throws ConfigException {
    
    for (MethodProxy entry : methodMap.values()) {
      entry.checkMandatory(name);
    }
  }
}