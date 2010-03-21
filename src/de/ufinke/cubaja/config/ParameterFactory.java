// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.annotation.Annotation;

/**
 * Factory for method parameter types.
 * <p/>
 * <tt>ParameterFactory</tt>s are provided by
 * <tt>ParameterFactoryFinder</tt>s.
 * @author Uwe Finke
 */
public interface ParameterFactory {

  /**
   * Returns an object of a given type representing a configuration value.
   * @param value attribute value or element content
   * @param type (super-) type of the object to be returned
   * @param annotations annotations of the setter / adder method
   * @return an object of type <tt>type</tt> used as parameter of a setter / adder method
   * @throws Exception
   */
  public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception;
  
  /**
   * Tells whether a parameter is an element node or an attribute only.
   * Attributes and leaf elements can't have sub-elements;
   * their setter and adder methods (if any) aren't used during configuration.
   * @return <tt>true</tt> if element with sub-elements; <tt>false</tt> otherwise
   */
  public boolean isNode();
}
