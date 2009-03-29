// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import de.ufinke.cubaja.util.Text;
import de.ufinke.cubaja.util.Util;

class Element {

  static private Text text = new Text(Element.class);
  static private String EMPTY_STRING = "";
  
  private String name;
  private ConfigNode node;
  private Map<String, MethodEntry> methodMap;
  private MethodEntry parentMethod;
  private ElementKind kind;
  private StringBuilder charData;
  private boolean cdata;
  
  Element(String name, ElementKind kind) {
  
    this.name = name;
    this.kind = kind;
  }
  
  void setKind(ElementKind kind) {
    
    this.kind = kind;
  }
  
  ElementKind getKind() {
    
    return kind;
  }
  
  String getName() {
    
    return name;
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
  
  void setNode(ConfigNode node) throws ConfigException {
    
    this.node = node;
    methodMap = new HashMap<String, MethodEntry>();
    
    for (Method method : node.getClass().getMethods()) {
      String methodName = method.getName();
      if (methodName.startsWith("set") || methodName.startsWith("add")) {
        checkMethod(method);
      }
    }
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

    MethodEntry entry = new MethodEntry(method);
    
    String searchName = method.getName().substring(3);
    
    if (methodMap.put(searchName, entry) != null) {
      throw new ConfigException(text.get("duplicateMethod", method.getName()));
    }
  }
  
  ConfigNode getNode() {
    
    return node;
  }
  
  MethodEntry findMethod(String methodName) {
    
    MethodEntry result = methodMap.get(Util.createMethodName(methodName, null));
    
    if (result == null && node != null) {
      String alternateName = node.assignAlternateName(methodName);
      if (alternateName != null) {        
        result = methodMap.get(Util.createMethodName(alternateName, null));
      }
    }
    
    return result;
  }
  
  void setParentMethod(MethodEntry parentMethod) {
    
    this.parentMethod = parentMethod;
  }
  
  MethodEntry getParentMethod() {
    
    return parentMethod;
  }
  
  void checkMandatory() throws ConfigException {
    
    for (MethodEntry entry : methodMap.values()) {
      entry.checkMandatory(name);
    }
  }
}
