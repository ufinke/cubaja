// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.util.HashMap;
import java.util.Map;
import de.ufinke.cubaja.cafebabe.*;

class ObjectFactoryGenerator implements Generator {

  private GenClassLoader loader;
  private Map<Class<?>, ObjectFactory> factoryMap;
  
  ObjectFactoryGenerator() {
  
    loader = new GenClassLoader();
    factoryMap = new HashMap<Class<?>, ObjectFactory>();
  }
  
  ObjectFactory getFactory(Class<?> clazz) throws Exception {
    
    ObjectFactory factory = factoryMap.get(clazz);
    if (factory == null) {
      factory = (ObjectFactory) loader.createInstance(this);
      factoryMap.put(clazz, factory);
    }    
    return factory; 
  }

  public GenClass generate() throws Exception {

    // TODO Auto-generated method stub
    return null;
  }

  public String getClassName() throws Exception {

    // TODO Auto-generated method stub
    return null;
  }
}
