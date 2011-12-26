package de.ufinke.cubaja.util;

import org.junit.*;
import static org.junit.Assert.*;
import de.ufinke.cubaja.config.*;
import java.util.*;

public class DayTest {
  
  static private HolidayConfig holidays;
  
  @BeforeClass
  static public void environment() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setResourceLoader(new FileResourceLoader("test/de/ufinke/cubaja/util"));
    configurator.setName("holiday_config");
    holidays = configurator.configure(new HolidayConfig());
  }
    
  @Test
  public void dateConstructor() {
    
    Date date = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    Day dayA = new Day(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
    Day dayB = new Day(date);
    assertEquals(dayA, dayB);
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
  
  @Test
  public void adjustNextHoliday() {
    
    Day day = new Day(2010, 3, 30);
    day.adjustNextHoliday(holidays);
    assertEquals(2010, day.year());
    assertEquals(4, day.month());
    assertEquals(2, day.day());
  }
  
  @Test
  public void adjustNextWeekday() {
    
    Day day = new Day(2010, 3, 30);
    day.adjustNextWeekday(Weekday.MONDAY);
    assertEquals(2010, day.year());
    assertEquals(4, day.month());
    assertEquals(5, day.day());
  }
  
  @Test
  public void adjustNextWorkday() {
    
    Day day = new Day(2010, 4, 1);
    day.adjustNextWorkday(holidays);
    assertEquals(2010, day.year());
    assertEquals(4, day.month());
    assertEquals(1, day.day());
    
    day = new Day(2010, 4, 2);
    day.adjustNextWorkday(holidays);
    assertEquals(2010, day.year());
    assertEquals(4, day.month());
    assertEquals(6, day.day());
  }
  
  @Test
  public void adjustPreviousHoliday() {
    
    Day day = new Day(2010, 3, 30);
    day.adjustPreviousHoliday(holidays);
    assertEquals(2010, day.year());
    assertEquals(3, day.month());
    assertEquals(28, day.day());
  }
  
  @Test
  public void adjustPreviousWeekday() {
    
    Day day = new Day(2010, 3, 30);
    day.adjustPreviousWeekday(Weekday.MONDAY);
    assertEquals(2010, day.year());
    assertEquals(3, day.month());
    assertEquals(29, day.day());
  }
  
  @Test
  public void adjustPreviousWorkday() {
    
    Day day = new Day(2010, 4, 1);
    day.adjustPreviousWorkday(holidays);
    assertEquals(2010, day.year());
    assertEquals(4, day.month());
    assertEquals(1, day.day());
    
    day = new Day(2010, 4, 2);
    day.adjustPreviousWorkday(holidays);
    assertEquals(2010, day.year());
    assertEquals(4, day.month());
    assertEquals(1, day.day());
  }
  
  @Test
  public void compareDate() {
    
    Date date = new Date();
    
    Day day = new Day(date);
    assertEquals(0, day.compareTo(date));
    
    day.addDays(1);
    assertEquals(1, day.compareTo(date));
    
    day.addDays(-2);
    assertEquals(-1, day.compareTo(date));
  }
  
  @Test
  public void testClone() {
    
    Day day = new Day();
    Day clone = day.clone();
    assertEquals(day, clone);
  }
  
  @Test
  public void components() {
    
    Day day = new Day(2010, 4, 5);
    assertEquals(2010, day.year());
    assertEquals(4, day.month());
    assertEquals(5, day.day());
    assertEquals(Weekday.MONDAY, day.getWeekday());
  }
  
  @Test
  public void dayCount() {
    
    Day dayA = new Day(2010, 4, 5);
    
    Day dayB = new Day(2010, 5, 4);
    assertEquals(29, dayA.dayCount(dayB));
    
    Day dayC = new Day(2010, 3, 5);
    assertEquals(-31, dayA.dayCount(dayC));
    
    Day dayD = dayA.clone().addYears(2);
    assertEquals(731, dayA.dayCount(dayD));
  }
  
  @Test
  public void monthCount() {
    
    Day dayA = new Day(2010, 1, 31);
    
    Day dayB = new Day(2010, 2, 28);
    assertEquals(1, dayA.monthCount(dayB));
    
    Day dayC = new Day(2012, 2, 29);
    assertEquals(25, dayA.monthCount(dayC));
    assertEquals(-25, dayC.monthCount(dayA));
    
    Day dayD = new Day(2012, 1, 30);
    assertEquals(23, dayA.monthCount(dayD));
    
    Day dayE = new Day(2012, 3, 30);
    assertEquals(0, dayC.monthCount(dayE));
    
    Day dayF = new Day(2012, 5, 31);
    assertEquals(3, dayC.monthCount(dayF));
    
    Day dayG = new Day(2012, 4, 30);
    assertEquals(2, dayC.monthCount(dayG));
    
    Day dayH = new Day(2012, 4, 29);
    assertEquals(1, dayC.monthCount(dayH));
  }
  
  @Test
  public void yearCount() {
    
    Day dayA = new Day(2010, 12, 31);
    
    Day dayB = new Day(2012, 12, 31);
    assertEquals(2, dayA.yearCount(dayB));
    assertEquals(-2, dayB.yearCount(dayA));
    
    Day dayC = new Day(2012, 2, 29);
    Day dayD = new Day(2013, 2, 28);
    assertEquals(1, dayC.yearCount(dayD));
  }
  
  @Test
  public void firstDayOfMonth() {
    
    Day day = new Day(2010, 4, 5);
    assertFalse(day.isFirstDayOfMonth());
    
    day = new Day(2010, 4, 1);
    assertTrue(day.isFirstDayOfMonth());
  }
  
  @Test
  public void lastDayOfMonth() {
    
    Day day = new Day(2010, 4, 5);
    assertFalse(day.isLastDayOfMonth());
    
    day = new Day(2010, 4, 30);
    assertTrue(day.isLastDayOfMonth());
    
    day = new Day(2010, 2, 28);
    assertTrue(day.isLastDayOfMonth());
    
    day = new Day(2012, 2, 28);
    assertFalse(day.isLastDayOfMonth());
    
    day = new Day(2012, 2, 29);
    assertTrue(day.isLastDayOfMonth());
  }
  
  @Test
  public void workdayHoliday() {
    
    Day day = new Day(2010, 4, 5);
    assertTrue(day.isHoliday(holidays));
    assertFalse(day.isWorkday(holidays));
    
    day = new Day(2010, 4, 6);
    assertFalse(day.isHoliday(holidays));
    assertTrue(day.isWorkday(holidays));
  }
  
}
