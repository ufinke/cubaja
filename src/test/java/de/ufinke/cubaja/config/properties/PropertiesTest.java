package de.ufinke.cubaja.config.properties;

import org.junit.*;
import de.ufinke.cubaja.config.*;
import static org.junit.Assert.*;

public class PropertiesTest {

  @Test
  public void errorTest() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setResourceLoader(new FileResourceLoader("test/de/ufinke/cubaja/config/properties"));
    configurator.setName("error_config");
    
    try {
      configurator.configure(new TestConfig());
    } catch (ConfigException e) {
      assertEquals("Property 'undefinedProperty' ist nicht definiert [error_config.xml, Zeile 4]", e.getMessage());
    }
    
  }
  
}
