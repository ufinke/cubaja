// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

/**
 * Provider of <code>ParameterFactory</code> instances for special types. 
 * @author Uwe Finke
 */
public interface ParameterFactoryFinder {

  /**
   * Returns a <code>ParameterFactory</code> for the specified type.
   * <p/>
   * If this finder can't provide an appropriate factory, it must
   * return <code>null</code>.
   * @param type the parameter type of a setter / adder method
   * @return a paramter factory or <code>null</code>
   * @throws ConfigException
   */
  public ParameterFactory findFactory(Class<?> type) throws ConfigException;
}
