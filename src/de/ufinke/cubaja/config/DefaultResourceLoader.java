// Copyright (c) 2008, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import org.xml.sax.InputSource;
import de.ufinke.cubaja.util.Text;


class DefaultResourceLoader implements ResourceLoader {

  static private Text text = new Text(DefaultResourceLoader.class);
  
  DefaultResourceLoader() {
    
  }

  public InputSource loadResource(String resourceName) throws ConfigException {

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
    
    InputStream stream = null;
    try {
      stream = new BufferedInputStream(new FileInputStream(fileName));
    } catch (Exception e) {
    }
    return stream;
  }
}
