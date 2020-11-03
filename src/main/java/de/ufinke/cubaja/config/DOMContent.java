// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

/**
 * Represents DOM content.
 * Useful to store parts of configuration which contain plain XML or XHTML.
 * @author Uwe Finke
 */
public class DOMContent {

  private DOMType type;
  
  /**
   * Constructor.
   * @param type
   */
  protected DOMContent(DOMType type) {
  
    this.type = type;
  }
  
  /**
   * Returns the type.
   * Useful for <tt>switch</tt> statements.
   * @return type of this content node
   */
  public DOMType getType() {
    
    return type;
  }
}
