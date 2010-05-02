package de.ufinke.cubaja.config.include;

import org.junit.*;
import de.ufinke.cubaja.config.Configurator;
import de.ufinke.cubaja.config.FileResourceLoader;
import static org.junit.Assert.*;

public class ConfiguratorTest {

  @Test
  public void basicTest() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setResourceLoader(new FileResourceLoader("test/de/ufinke/cubaja/config/include"));
    configurator.setName("inline_include_config");
    Config config = configurator.configure(new Config());
    
    assertEquals(1, config.getFooList().get(0).getBar());
    assertEquals(2, config.getFooList().get(1).getBar());
  }
  
}
