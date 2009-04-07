// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.math.*;
import java.util.*;

class BinaryObjectHandlerFactory {

  static private final Map<Class<?>, InputObjectHandler> inputHandlerMap = new HashMap<Class<?>, InputObjectHandler>();
  static private final Map<Class<?>, OutputObjectHandler> outputHandlerMap = new HashMap<Class<?>, OutputObjectHandler>();
  
  static OutputObjectHandler getOutputHandler(Class<?> dataClass) {
    
    OutputObjectHandler handler = null;
    
    synchronized (outputHandlerMap) {
      handler = outputHandlerMap.get(dataClass);
    }
    
    if (handler == null) {
      handler = new BinaryObjectHandlerFactory().createOutputHandler(dataClass);
      synchronized (outputHandlerMap) {
        outputHandlerMap.put(dataClass, handler);
      }
    }
    
    return handler;
  }
  
  static InputObjectHandler getInputHandler(Class<?> dataClass) {
    
    InputObjectHandler handler = null;
    
    synchronized (inputHandlerMap) {
      handler = inputHandlerMap.get(dataClass);
    }
    
    if (handler == null) {
      handler = new BinaryObjectHandlerFactory().createInputHandler(dataClass);
      synchronized (inputHandlerMap) {
        inputHandlerMap.put(dataClass, handler);
      }
    }
    
    return handler;
  }
  
  private BinaryObjectHandlerFactory() {
    
  }
  
  private OutputObjectHandler createOutputHandler(Class<?> dataClass) {
    
    //TODO
    return null;
  }
  
  private InputObjectHandler createInputHandler(Class<?> dataClass) {
    
    //TODO
    return null;
  }
}
