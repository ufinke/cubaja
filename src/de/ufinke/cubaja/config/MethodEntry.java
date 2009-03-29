// Copyright (c) 2008, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import de.ufinke.cubaja.util.Text;

class MethodEntry {

  static private Text text = new Text(MethodEntry.class);
  
  private Method method;
  private Class<?> parmType;
  private int useCount;
  private boolean mandatory;
  private boolean unique;
  private boolean nodeType;
  
  MethodEntry(Method method) {

    this.method = method;
    parmType = method.getParameterTypes()[0];
    mandatory = method.isAnnotationPresent(Mandatory.class);
    unique = method.getName().startsWith("set");
    nodeType = ConfigNode.class.isAssignableFrom(parmType);
  }
  
  Method getMethod() {
    
    return method;
  }
  
  Class<?> getParmType() {
    
    return parmType;
  }
  
  boolean isNodeType() {
    
    return nodeType;
  }
  
  void invoke(String nodeName, Object target, Object arg) throws ConfigException {
    
    useCount++;
    
    if (unique && useCount > 1) {
      throw new ConfigException(text.get("unique", nodeName));
    }
    
    try {      
      method.invoke(target, arg);
    } catch (InvocationTargetException ite) {
      if (ConfigException.class.isAssignableFrom(ite.getCause().getClass())) {
        throw (ConfigException) ite.getCause();
      } else {
        throw new ConfigException(ite);
      }
    } catch (Exception e) {
      throw new ConfigException(e);
    }
  }
  
  void checkMandatory(String elementName) throws ConfigException {
    
    if (mandatory && useCount == 0) {
      throw new ConfigException(text.get("mandatory", getStrippedName(), elementName));
    }
  }
  
  String getStrippedName() {
    
    String strippedName = method.getName();
    StringBuilder sb = new StringBuilder(100);
    sb.append(Character.toLowerCase(strippedName.charAt(3)));
    if (strippedName.length() > 4) {
      sb.append(strippedName.substring(4));
    }
    return sb.toString();
  }
}
