package de.ufinke.cubaja.config.properties;

import org.junit.*;
import de.ufinke.cubaja.config.*;
import static org.junit.Assert.*;

public class EnvironmentTest {

  @Test
  public void environmentTest() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setResourceLoader(new FileResourceLoader("src/test/resources/de/ufinke/cubaja/config/properties"));
    configurator.setProcessEscape(false);
    configurator.setName("environment_config");
    
    TestConfig config = configurator.configure(new TestConfig());
    assertNotNull(config.getContent());
  }
  
}
