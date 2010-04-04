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
  
  @Test
  public void addWorkdays() {
    
    Day day = new Day(2010, 4, 3);
    day.addWorkdays(5, holidays);
    assertEquals(2010, day.year());
    assertEquals(4, day.month());
    assertEquals(12, day.day());
    
    day = new Day(2010, 4, 1);
    day.addWorkdays(-5, holidays);
    assertEquals(2010, day.year());
    assertEquals(3, day.month());
    assertEquals(25, day.day());
  }
  
  @Test
  public void addYears() {
    
    Day day = new Day(2010, 4, 3);
    day.addYears(4);
    assertEquals(2014, day.year());
    assertEquals(4, day.month());
    assertEquals(3, day.day());
  }
  
  @Test
  public void adjustFirstDayOfMonth() {
    
    Day day = new Day(2059, 1, 15);
    day.adjustFirstDayOfMonth();
    assertEquals(2059, day.year());
    assertEquals(1, day.month());
    assertEquals(1, day.day());
  }
  
  @Test
  public void adjustFirstDayOfYear() {
    
    Day day = new Day(2056, 12, 6);
    day.adjustFirstDayOfMonth();
    assertEquals(2056, day.year());
    assertEquals(12, day.month());
    assertEquals(1, day.day());
  }
  
  @Test
  public void adjustLastDayOfMonth() {
    
    Day day = new Day(2059, 2, 15);
    day.adjustLastDayOfMonth();
    assertEquals(2059, day.year());
    assertEquals(2, day.month());
    assertEquals(28, day.day());
  }
  
  @Test
  public void adjustLastDayOfYear() {
    
    Day day = new Day(2056, 2, 6);
    day.adjustLastDayOfYear();
    assertEquals(2056, day.year());
    assertEquals(12, day.month());
    assertEquals(31, day.day());
  }
  
}
