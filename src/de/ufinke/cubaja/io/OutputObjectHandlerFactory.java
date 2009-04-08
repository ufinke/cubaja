// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.math.*;
import java.util.*;

class OutputObjectHandlerFactory {

  static private final Map<Class<?>, OutputObjectHandler> handlerMap = new HashMap<Class<?>, OutputObjectHandler>();
  
  static OutputObjectHandler getHandler(Class<?> dataClass, List<PropertyDescription> properties) {
    
    OutputObjectHandler handler = null;
    
    synchronized (handlerMap) {
      handler = handlerMap.get(dataClass);
    }
    
    if (handler == null) {
      handler = new OutputObjectHandlerFactory().createHandler(dataClass);
      synchronized (handlerMap) {
        handlerMap.put(dataClass, handler);
      }
    }
    
    return handler;
  }
  
  private OutputObjectHandlerFactory() {
    
  }
  
  private OutputObjectHandler createHandler(Class<?> dataClass) {
    
    //TODO
    return null;
  }
  
}
