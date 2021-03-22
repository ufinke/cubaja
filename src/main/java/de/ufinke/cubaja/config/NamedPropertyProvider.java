// Copyright (c) 2008 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.Map;

/**
 * <p>
 * PropertyProvider variant.
 * </p><p>
 * A <code>NamedPropertyProvider</code> is needed
 * when a property value depends on program logic and
 * the program logic depends on other configuration content.
 * </p>
 * @author Uwe Finke
 */
public interface NamedPropertyProvider {

  /**
   * Returns a property value.
   * @param name the name of the property
   * @param parms name/value-pairs specified by <code>parm</code> sub-elements of <code>configProperty</code>
   * @return the property value
   * @throws Exception any exception
   */
  public String getProperty(String name, Map<String, String> parms) throws Exception;
}
