// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.annotation.Annotation;

/**
 * <p>
 * Factory for method parameter types.
 * </p><p>
 * <code>ParameterFactory</code>s are provided by
 * <code>ParameterFactoryFinder</code>s.
 * </p>
 * @author Uwe Finke
 */
public interface ParameterFactory {

  /**
   * Returns an object of a given type representing a configuration value.
   * @param value attribute value or element content
   * @param type (super-) type of the object to be returned
   * @param annotations annotations of the setter / adder method
   * @return an object of type <code>type</code> used as parameter of a setter / adder method
   * @throws Exception any exception
   */
  public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception;
  
  /**
   * Tells whether a parameter is an element node or an attribute only.
   * Attributes and leaf elements can't have sub-elements;
   * their setter and adder methods (if any) aren't used during configuration.
   * @return <code>true</code> if element with sub-elements; <code>false</code> otherwise
   */
  public boolean isNode();
}
