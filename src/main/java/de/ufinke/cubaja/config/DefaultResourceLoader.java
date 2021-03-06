// Copyright (c) 2008 - 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.InputSource;
import de.ufinke.cubaja.util.Text;

/**
 * Default <code>ResourceLoader</code> implementation.
 * Loads config XML from a resource, from file system or from an internal map.
 * @author Uwe Finke
 */
public class DefaultResourceLoader implements ResourceLoader {

  static private Text text = Text.getPackageInstance(DefaultResourceLoader.class);
  
  private String base;
  private Map<String, String> stringMap;
  
  /**
   * Constructor.
   */
  public DefaultResourceLoader() {
  
    base = System.getProperty("de.ufinke.cubaja.config.base", "");
    checkBase();
  }
  
  void setBase(String base) {
    
    this.base = base;
    checkBase();
  }

  private void checkBase() {
    
    if (base == null) {
      base = "";
    }
    if (base.length() > 0 && base.charAt(base.length() - 1) != '/') {
      base = base + "/";
    }
  }  
  
  /**
   * Adds a string as XML source.
   * Note that <code>resourceName</code> should end with <code>.xml</code>
   * if the string will be used due to <code>Configurator.setName()</code>.
   * @param recoureName name which will be used as key to include the XML string
   * @param xml string with xml
   */
  public void addString(String recoureName, String xml) {
    
    if (xml == null) {
      xml = "";
    }
    
    if (stringMap == null) {
      stringMap = new HashMap<String, String>();
    }
    stringMap.put(recoureName, xml);
  }

  public InputSource loadResource(String resourceName) throws ConfigException {

    if (resourceName == null || resourceName.length() == 0) {
      throw new ConfigException(text.get("noResourceName"));
    }
    
    if (stringMap != null) {
      String xml = stringMap.get(resourceName);
      if (xml != null) {
        return new InputSource(xml);
      }
    }
    
    resourceName = base + resourceName;
    
    InputStream stream = getClass().getClassLoader().getResourceAsStream(resourceName);
    if (stream == null) {
      stream = openFile(resourceName);
    }
    if (stream == null) {
      throw new ConfigException(text.get("resourceNotFound", resourceName));
    }
    return new InputSource(stream);
  }
  
  private InputStream openFile(String fileName) {
    
    if (fileName.startsWith("file://")) {
      fileName = fileName.substring(7);
    }
    
    InputStream stream = null;
    try {
      stream = new BufferedInputStream(new FileInputStream(fileName));
    } catch (Exception e) {
    }
    return stream;
  }
}
