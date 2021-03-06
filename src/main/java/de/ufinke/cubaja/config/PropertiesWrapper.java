// Copyright (c) 2007, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.Properties;

/**
 * Wrapper for <code>java.util.Properies</code> implementing <code>PropertyProvider</code>.
 * @author Uwe Finke
 */
class PropertiesWrapper implements PropertyProvider {

  private Properties properties;
  
  /**
   * Constructor.
   * @param properties properties
   */
  PropertiesWrapper(Properties properties) {
    
    this.properties = properties;
  }
  
  public String getProperty(String key) {
    
    return properties.getProperty(key);
  }
}
