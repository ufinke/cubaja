// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.Map;

/**
 * Gives an element node control over its init and finish events.
 * @author Uwe Finke
 */
public interface ManagedElement {

  /**
   * Called immediately after instantiation of this node object.
   * @param sharedMap a map with shared information
   * @throws ConfigException
   */
  public void init(Map<Object, Object> sharedMap) throws ConfigException;
  
  /**
   * Called after all attributes and sub-elements have been set.
   * @throws ConfigException
   */
  public void finish() throws ConfigException;  
}
