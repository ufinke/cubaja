package de.ufinke.cubaja;

import java.util.*;
import java.io.*;

public class TestEnvironment {

  private String packageName;
  private Properties properties;
  
  public TestEnvironment(String packageName) throws Exception {
  
    properties = new Properties();
    InputStream stream = new BufferedInputStream(TestEnvironment.class.getResourceAsStream("test.properties"));
    properties.load(stream);
    stream.close();
    
    this.packageName = properties.getProperty("testHome") + "/de/ufinke/cubaja/" + packageName + "/";
  }
  
  public String getBaseName(String baseName) {
    
    return packageName + baseName;
  }
  
  public Properties getProperties() {
    
    return properties;
  }
}
