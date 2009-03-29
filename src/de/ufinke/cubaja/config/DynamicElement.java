// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

/**
 * Enables an element node for dynamic attributes and sub-elements.
 * @author Uwe Finke
 */
public interface DynamicElement {

  /**
   * Returns an alternate name for a given XML element or attribute name.
   * The result value may be <code>null</code>.
   * @param originalName the original tag or attribute name
   * @return a name this config node object could handle
   * @throws ConfigException
   */
  public String alternateName(String originalName) throws ConfigException;
  
  /**
   * Returns a <code>ParameterFactoryFinder</code> individual to an element node. 
   * The result value may be <code>null</code>.
   * @return a parameter factory finder
   * @throws ConfigException
   */
  public ParameterFactoryFinder parameterFactoryFinder() throws ConfigException;
}
