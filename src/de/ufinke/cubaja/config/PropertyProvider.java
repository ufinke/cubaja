// Copyright (c) 2007 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

/**
 * Individual property provider.
 * @author Uwe Finke
 */
public interface PropertyProvider {

  /**
   * Returns a property value.
   * @param name property name
   * @return property value
   * @throws ConfigException
   */
  public String getProperty(String name) throws ConfigException;
}
