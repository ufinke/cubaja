// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import de.ufinke.cubaja.cafebabe.*;

class ObjectFactoryGenerator implements Generator {

  private GenClassLoader loader;
  
  ObjectFactoryGenerator() {
  
    loader = new GenClassLoader();
  }
  
  ObjectFactory createFactory(Class<?> clazz) throws Exception {
    
    return (ObjectFactory) loader.createInstance(this);
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
