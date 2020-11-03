package de.ufinke.cubaja.config.datepattern;

import org.junit.*;
import de.ufinke.cubaja.config.*;
import de.ufinke.cubaja.util.Day;
import static org.junit.Assert.*;

public class DatepatternTest {

  @Test
  public void datepatternTest() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setResourceLoader(new FileResourceLoader("test/de/ufinke/cubaja/config/datepattern"));
    configurator.setName("datepattern_config");
    
    TestConfig config = configurator.configure(new TestConfig());
    
    assertEquals(new Day(2000, 12, 31).date(), config.getDateA());
    assertEquals(new Day(2001, 06, 15).date(), config.getDateB());
    assertEquals(new Day(2011, 04, 27).date(), config.getDateC());
  }
  
}
