// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

public class DOMContent {

  private DOMType type;
  
  protected DOMContent(DOMType type) {
  
    this.type = type;
  }
  
  public DOMType getType() {
    
    return type;
  }
}
