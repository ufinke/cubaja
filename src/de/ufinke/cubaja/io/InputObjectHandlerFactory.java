// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.math.*;
import java.util.*;

class InputObjectHandlerFactory {

  static private final Map<Class<?>, InputObjectHandler> handlerMap = new HashMap<Class<?>, InputObjectHandler>();
  
  static InputObjectHandler getHandler(Class<?> dataClass, List<PropertyDescription> properties) {
    
    InputObjectHandler handler = null;
    
    synchronized (handlerMap) {
      handler = handlerMap.get(dataClass);
    }
    
    if (handler == null) {
      handler = new InputObjectHandlerFactory().createHandler(dataClass);
      synchronized (handlerMap) {
        handlerMap.put(dataClass, handler);
      }
    }
    
    return handler;
  }
  
  private InputObjectHandlerFactory() {
    
  }
  
  private InputObjectHandler createHandler(Class<?> dataClass) {
    
    //TODO
    return null;
  }
}
