// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.etl.api;

import de.ufinke.cubaja.config.Mandatory;

public class TaskConfig {

  private String name;
  
  public TaskConfig() {
    
  }
  
  public String getName() {
  
    return name;
  }

  @Mandatory
  public void setName(String name) {
  
    this.name = name;
  }
}
