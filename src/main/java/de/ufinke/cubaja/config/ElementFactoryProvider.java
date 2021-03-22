// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.Map;

/**
 * Provider of <code>ElementFactory</code>s.
 * A configuration node which implements this interface is able to choose
 * the source of subordinate node instances dynamically.
 * This feature is needed when the classes which represent XML sub-elements
 * are not known at compile time, e.g. when they depend on other configuration parameters.
 * @author Uwe Finke
 */
public interface ElementFactoryProvider {

  /**
   * Returns an <code>ElementFactory</code> to the parser.
   * If the given tag or attributes are not supported by the implementing class,
   * the result may be <code>null</code>, so that the parser tries to create a node instance
   * in the standard way. 
   * @param tagName XML tag
   * @param attributes map of XML attributes
   * @return element factory 
   * @throws Exception any exception
   */
  public ElementFactory getFactory(String tagName, Map<String, String> attributes) throws Exception;
}
