package de.ufinke.cubaja;

import java.util.*;
import java.io.*;
import org.junit.*;

public class TestClass {

  static protected Properties properties;
  
  public TestClass() {
  
  }
  
  @BeforeClass
  static public void loadProperties() throws Exception {
    
    properties = new Properties();
    InputStream stream = new BufferedInputStream(TestClass.class.getResourceAsStream("test.properties"));
    properties.load(stream);
    stream.close();
  }
  
  protected String getResourceName(String resource) {
    
    return getClass().getPackage().getName().replace('.', '/') + "/" + resource;
  }
  
}
