// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

/**
 * Handles end of element processing.
 * @author Uwe Finke
 */
public interface EndElementHandler {

  /**
   * Called after all attributes and sub-elements have been set.
   * @throws ConfigException
   */
  public void endElement() throws ConfigException;  
}
