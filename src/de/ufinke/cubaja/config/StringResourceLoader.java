// Copyright (c) 2007 - 2013, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.io.StringReader;
import java.util.List;
import org.xml.sax.InputSource;

/**
 * <tt>ResourceLoader</tt> for XML config data provided as String.
 * <p>
 * Note that <tt>StringResourceLoader</tt> always returns its string
 * and doesn't take care of complex configurations where e.g. a <tt>PropertyProvider</tt> looks for a resource.
 * An alternative way to provide XML strings is to use 
 * {@link DefaultRecourceLoader#addString(String, String) DefaultRecourceLoader.addString}.
 * @author Uwe Finke
 */
public class StringResourceLoader implements ResourceLoader {

  private StringBuilder buffer;
  
  /**
   * Constructor.
   */
  public StringResourceLoader() {
    
    buffer = new StringBuilder(500);
  }
  
  /**
   * Constructor with XML string.
   * @param xml the configuration XML
   */
  public StringResourceLoader(String xml) {
    
    this();
    append(xml);
  }
  
  /**
   * Appends one line of XML.
   * @param line a line
   */
  public void append(String line) {
    
    buffer.append(line);
    buffer.append('\n');
  }
  
  /**
   * Appends XML lines.
   * @param lines some lines
   */
  public void append(String[] lines) {
    
    for (String line : lines) {
      append(line);
    }
  }
  
  /**
   * Appends XML lines.
   * @param lines some lines
   */
  public void append(List<String> lines) {
    
    for (String line : lines) {
      append(line);
    }
  }
  
  public InputSource loadResource(String resourceName) throws ConfigException {
    
    return new InputSource(new StringReader(buffer.toString()));
  }
}
