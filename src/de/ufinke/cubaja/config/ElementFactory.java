// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface ElementFactory {

  public Object getElement(Annotation[] annotations) throws ConfigException;
  
  public Method getMethod() throws ConfigException;
}
