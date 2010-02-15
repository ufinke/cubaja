// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.etl.core;

import de.ufinke.cubaja.config.Mandatory;

public class LinkConfig {

  private String name;
  private ConnectionConfig from;
  private ConnectionConfig to;
  
  public LinkConfig() {
    
  }

  public ConnectionConfig getFrom() {
  
    return from;
  }
  
  public String getName() {
  
    return name;
  }

  @Mandatory
  public void setName(String name) {
  
    this.name = name;
  }

  @Mandatory
  public void setFrom(ConnectionConfig from) {
  
    this.from = from;
  }
  
  public ConnectionConfig getTo() {
  
    return to;
  }

  @Mandatory
  public void setTo(ConnectionConfig to) {
  
    this.to = to;
  }
}
