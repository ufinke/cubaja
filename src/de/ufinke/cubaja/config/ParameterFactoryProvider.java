// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

/**
 * Provider of <tt>ParameterFactory</tt> instances for special tags and types.
 * A configuration object may implement this interface
 * to provide its own local factories.
 * @author Uwe Finke
 */
public interface ParameterFactoryProvider {

  /**
   * Returns a <tt>ParameterFactory</tt> for the specified name and type. 
   * If this provider can't provide an appropriate factory, it must
   * return <tt>null</tt>.
   * @param name element tag or attribute name
   * @param type parameter data type
   * @return parameter factory
   * @throws ConfigException
   */
  public ParameterFactory getFactory(String name, Class<?> type) throws ConfigException;
}
