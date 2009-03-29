// Copyright (c) 2008, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

/**
 * Basic property provider types.
 * @author Uwe Finke
 */
public enum PropertyProviderType {

  /**
   * System property provider.
   * <p/>
   * System properties are retrieved with <code>java.lang.System.getProperty</code>.
   */
  SYSTEM,
  
  /**
   * Properties file property provider.
   * <p/>
   * The properties are specified in the optional file <code><i>baseName</i>.properties</code>.
   * <code><i>baseName</i></code> is the same name as for the XML document, but with another extension.
   */
  BASE_PROPERTIES,
  
  /**
   * XML property provider.
   * <p/>
   * The properties are specified with name-/value-pairs in <code>configProperty</code> elements.
   */
  BASE_XML,
  
  /**
   * Environment variables property provider.
   * <p/>
   * Environment variables are retrieved with <code>java.lang.System.getEnv</code>.
   */
  ENVIRONMENT,
  
  /**
   * Final property provider.
   * <p/>
   * After adding this provider type, no other providers will be added to 
   * the <code>Configurator</code>, neither explicit nor automatically.
   */
  NULL
}
