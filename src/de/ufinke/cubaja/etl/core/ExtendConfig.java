// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.etl.core;

import de.ufinke.cubaja.config.Mandatory;

public class ExtendConfig {

  private String lib;
  
  public ExtendConfig() {
    
  }

  public String getLib() {
  
    return lib;
  }

  @Mandatory
  public void setLib(String lib) {
  
    this.lib = lib;
  }
}
