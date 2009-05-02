// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import de.ufinke.cubaja.cafebabe.GenClass;
import de.ufinke.cubaja.cafebabe.Generator;
import de.ufinke.cubaja.cafebabe.Loader;

class ObjectFactoryManager implements Generator {

  static private final Map<Class<?>, ObjectFactory> factoryMap = new ConcurrentHashMap<Class<?>, ObjectFactory>();
  
  static ObjectFactory getFactory(Class<?> clazz) throws Exception {
    
    ObjectFactory factory = factoryMap.get(clazz);
    if (factory == null) {
      String className = Loader.createClassName(ObjectFactoryManager.class, "ObjectFactory", clazz);
      factory = (ObjectFactory) Loader.createInstance(className, new ObjectFactoryManager(clazz));
      factoryMap.put(clazz, factory);
    }
    return factory;
  }

  private Class<?> dataClass;
  
  private ObjectFactoryManager(Class<?> dataClass) {
  
    this.dataClass = dataClass;
  }
  
  public GenClass generate(String className) throws Exception {

    // TODO Auto-generated method stub
    return null;
  }
}
