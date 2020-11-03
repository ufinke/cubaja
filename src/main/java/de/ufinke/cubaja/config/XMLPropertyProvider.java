// Copyright (c) 2008, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.Properties;

class XMLPropertyProvider implements PropertyProvider {

  private Properties properties;
  
  XMLPropertyProvider() {
    
    properties = new Properties();
  }
  
  public String getProperty(String key) {
  
    return properties.getProperty(key);
  }
  
  void setProperty(String key, String value) {
    
    properties.setProperty(key, value);
  }
}
