// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.Map;

/**
 * PropertyProvider variant.
 * <p/>
 * We need a <code>NamedPropertyProvider</code>
 * when a property value depends on program logic and
 * the program logic depends on other configuration content.
 * @author Uwe Finke
 */
public interface NamedPropertyProvider {

  /**
   * Returns a property value.
   * @param name the property's name
   * @param parms name/value-pairs specified by <code>configProperty</code>'s <code>parm</code> sub-elements
   * @return the property value
   * @throws ConfigException
   */
  public String getProperty(String name, Map<String, String> parms) throws ConfigException;
}
