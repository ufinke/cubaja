// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

/**
 * Provider of <code>ParameterFactory</code> instances for special types. 
 * @author Uwe Finke
 */
public interface ParameterFactoryFinder {

  /**
   * <p>
   * Returns a <code>ParameterFactory</code> for the specified type.
   * </p><p>
   * If this finder can't provide an appropriate factory, it must
   * return <code>null</code>.
   * </p>
   * @param type the parameter type of a setter / adder method
   * @return a parameter factory or <code>null</code>
   * @throws Exception any exception
   */
  public ParameterFactory findFactory(Class<?> type) throws Exception;
}
