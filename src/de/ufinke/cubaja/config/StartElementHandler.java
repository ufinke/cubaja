// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.Map;

/**
 * Handles start of element processing.
 * @author Uwe Finke
 */
public interface StartElementHandler {

  /**
   * Called immediately after instantiation of this node object.
   * @param sharedMap a map with shared information
   * @throws ConfigException
   */
  public void startElement(Map<Object, Object> sharedMap) throws ConfigException;  
}
