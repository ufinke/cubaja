// Copyright (c) 2008, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

class EnvironmentPropertyProvider implements PropertyProvider {

  EnvironmentPropertyProvider() {
    
  }
  
  public String getProperty(String key) {
    
    return System.getenv(key);
  }
}
