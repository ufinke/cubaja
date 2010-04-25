// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Dynamic factory for an element node instance.
 * An instance is supplied by an implementation of {@link ElementFactoryProvider}.
 * @author Uwe Finke
 */
public interface ElementFactory {

  /**
   * Returns the element node instance.
   * @param annotations
   * @return element node
   * @throws Exception
   */
  public Object getElement(Annotation[] annotations) throws Exception;
  
  /**
   * Returns the setter or adder method of the parent node.
   * @return method
   * @throws Exception
   */
  public Method getMethod() throws Exception;
}
