// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import de.ufinke.cubaja.util.Text;

class MethodProxy {

  static private Text text = Text.getPackageInstance(MethodProxy.class);
  
  private Method method;
  private Class<?> type;
  private Annotation[] annotations;
  private int useCount;
  private boolean mandatory;
  private boolean unique;
  private boolean charData;
  
  MethodProxy(Method method) {

    this.method = method;
    type = method.getParameterTypes()[0];
    annotations = method.getAnnotations();
    mandatory = method.isAnnotationPresent(Mandatory.class);
    charData = method.isAnnotationPresent(CharData.class) && method.getParameterTypes()[0] == String.class;
    unique = method.getName().startsWith("set");
  }
  
  Class<?> getType() {
    
    return type;
  }
  
  Annotation[] getAnnotations() {
    
    return annotations;
  }
  
  boolean isCharData() {
    
    return charData;
  }
  
  Method getMethod() {
  
    return method;
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
