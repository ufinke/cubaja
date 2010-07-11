// Copyright (c) 2008 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.Map;

/**
 * PropertyProvider variant.
 * <p/>
 * A <tt>NamedPropertyProvider</tt> is needed
 * when a property value depends on program logic and
 * the program logic depends on other configuration content.
 * @author Uwe Finke
 */
public interface NamedPropertyProvider {

  /**
   * Returns a property value.
   * @param name the name of the property
   * @param parms name/value-pairs specified by <tt>parm</tt> sub-elements of <tt>configProperty</tt>
   * @return the property value
   * @throws Exception
   */
  public String getProperty(String name, Map<String, String> parms) throws Exception;
}
