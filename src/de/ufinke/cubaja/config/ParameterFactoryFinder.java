// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

/**
 * Provider of <tt>ParameterFactory</tt> instances for special types. 
 * @author Uwe Finke
 */
public interface ParameterFactoryFinder {

  /**
   * Returns a <tt>ParameterFactory</tt> for the specified type.
   * <p/>
   * If this finder can't provide an appropriate factory, it must
   * return <tt>null</tt>.
   * @param type the parameter type of a setter / adder method
   * @return a parameter factory or <tt>null</tt>
   * @throws Exception
   */
  public ParameterFactory findFactory(Class<?> type) throws Exception;
}
