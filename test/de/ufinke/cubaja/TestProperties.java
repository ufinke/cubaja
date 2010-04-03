package de.ufinke.cubaja;

import java.util.*;
import java.io.*;

public class TestProperties {

  private Properties properties;
  
  public TestProperties() throws Exception {
  
    properties = new Properties();
    BufferedInputStream stream = new BufferedInputStream(getClass().getResourceAsStream("test.properties"));
    properties.load(stream);
    stream.close();
  }
  
  public String getProperty(String name) {
    
    return properties.getProperty(name);
  }
}
