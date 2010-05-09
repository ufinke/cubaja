// Copyright (c) 2008 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Properties;
import org.xml.sax.InputSource;
import de.ufinke.cubaja.util.Text;

class ConfigPropertyProvider implements PropertyProvider {

  static private Text text = Text.getPackageInstance(ConfigPropertyProvider.class);
  
  private Properties properties;
  
  ConfigPropertyProvider() {
  
  }
  
  void load(ResourceLoader loader) throws ConfigException {
    
    String resourceName = "config.properties";
    boolean mandatory = false;
    
    properties = new Properties();
    
    try {      
      loadResourceProperties(loader.loadResource(resourceName), properties);
    } catch (ConfigException e) {
      if (mandatory) {
        throw new ConfigException(text.get("resourceNotFound", resourceName));
      }
    } catch (IOException ioe) {
      throw new ConfigException(text.get("propertiesLoadFailed", resourceName), ioe);
    }
  }
  
  private void loadResourceProperties(InputSource source, Properties props) throws IOException {
    
    InputStream stream = source.getByteStream();
    if (stream != null) {        
      props.load(stream);
      stream.close();
      return;
    }
    
    // Properties.load(Reader) not available in Java 1.5
    Reader reader = source.getCharacterStream();
    if (reader != null) {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(bos)));
      BufferedReader in = new BufferedReader(reader);
      String line = in.readLine();
      while (line != null) {
        out.println(line);
        line = in.readLine();
      }
      out.close();
      in.close();
      loadResourceProperties(new InputSource(new ByteArrayInputStream(bos.toByteArray())), props);
      return;
    }
    
    throw new IOException(text.get("noSource"));
  }
  
  public String getProperty(String key) {
    
    return properties.getProperty(key);
  }
  
}
