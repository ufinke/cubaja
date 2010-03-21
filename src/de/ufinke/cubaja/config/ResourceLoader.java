// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import org.xml.sax.InputSource;

/**
 * Interface for an XML resource loader.
 * <p/>
 * We need an external loader in case we cannot place the XML source in the classpath
 * or the file system.
 * @author Uwe Finke
 */
public interface ResourceLoader {

  /**
   * Provides an <tt>org.xml.sax.InputSource</tt> for an XML resource. 
   * @param resourceName the XML resource name
   * @return the <tt>InputSource</tt> which will be parsed
   * @throws ConfigException
   */
  public InputSource loadResource(String resourceName) throws ConfigException;
  
}
