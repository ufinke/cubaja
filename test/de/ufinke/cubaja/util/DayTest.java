package de.ufinke.cubaja.util;

import org.junit.*;
import static org.junit.Assert.*;
import de.ufinke.cubaja.*;
import de.ufinke.cubaja.config.*;

public class DayTest {
  
  static private TestEnvironment environment;
  static private HolidayConfig holidays;
  
  @BeforeClass
  static public void environment() throws Exception {
    
    environment = new TestEnvironment("util");
    
    Configurator configurator = new Configurator();
    configurator.setBaseName(environment.getBaseName("holiday_config"));
    holidays = configurator.configure(new HolidayConfig());
  }
    
  @Test
  public void addDays() {
    
    Day day = new Day(2010, 4, 3);
    day.addDays(365);
    assertEquals(2011, day.year());
    assertEquals(4, day.month());
    assertEquals(3, day.day());
  }
  
  @Test
  public void addMonth() {
    
    Day day = new Day(2010, 12, 31);
    day.addMonths(2);
    assertEquals(2011, day.year());
    assertEquals(2, day.month());
    assertEquals(28, day.day());
    
    Day lastOfMonth = day.clone();
    
    day.addMonths(1);
    assertEquals(2011, day.year());
    assertEquals(3, day.month());
    assertEquals(28, day.day());

    day = lastOfMonth;
    day.addMonths(1, true);
    assertEquals(2011, day.year());
    assertEquals(3, day.month());
    assertEquals(31, day.day());
  }
  
}
