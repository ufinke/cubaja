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
   * System properties are retrieved with <tt>java.lang.System.getProperty</tt>.
   */
  SYSTEM,
  
  /**
   * Properties file property provider.
   * <p/>
   * The properties are specified in the optional file <tt><i>baseName</i>.properties</tt>.
   * <tt><i>baseName</i></tt> is the same name as for the XML document, but with another extension.
   */
  BASE_PROPERTIES,
  
  /**
   * XML property provider.
   * <p/>
   * The properties are specified with name-/value-pairs in <tt>configProperty</tt> elements.
   */
  BASE_XML,
  
  /**
   * Environment variables property provider.
   * <p/>
   * Environment variables are retrieved with <tt>java.lang.System.getEnv</tt>.
   */
  ENVIRONMENT,
  
  /**
   * Final property provider.
   * <p/>
   * After adding this provider type, no other providers will be added to 
   * the <tt>Configurator</tt>, neither explicit nor automatically.
   */
  NULL;
}
