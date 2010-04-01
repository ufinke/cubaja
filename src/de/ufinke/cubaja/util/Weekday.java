// Copyright (c) 2007 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Weekday definitions.
 * @author Uwe Finke
 */
public enum Weekday {

  SUNDAY   (Calendar.SUNDAY),
  MONDAY   (Calendar.MONDAY),
  TUESDAY  (Calendar.TUESDAY),
  WEDNESDAY(Calendar.WEDNESDAY),
  THURSDAY (Calendar.THURSDAY),
  FRIDAY   (Calendar.FRIDAY),
  SATURDAY (Calendar.SATURDAY);
  
  private int calendarConstant;
  private String string;
  
  private Weekday(int calendarConstant) {
    
    this.calendarConstant = calendarConstant;
  }

  /**
   * Returns the corresponding <tt>Calendar</tt> constant.
   * @return constant
   */
  public int getCalendarConstant() {
    
    return calendarConstant;
  }
  
  /**
   * Returns the weekday's name.
   * The name is localized according to resource bundle entries.
   */
  public String toString() {
    
    if (string == null) {
      if (text == null) {
        text = new Text(Weekday.class);
      }
      string = text.get(name()); 
    }
    
    return string;
  }
  
  static private Text text;
  
  /**
   * Returns a date's weekday.
   * @param date
   * @return weekday
   */
  static public Weekday getWeekday(Date date) {
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return getWeekday(cal);
  }
  
  /**
   * Returns the calendar value's weekday.
   * @param cal
   * @return weekday
   */
  static public Weekday getWeekday(Calendar cal) {
    
    if (weekdayMap == null) {
      createWeekdayMap();
    }
    
    return weekdayMap[cal.get(Calendar.DAY_OF_WEEK)];
  }
  
  static private Weekday[] weekdayMap;
  
  static private synchronized void createWeekdayMap() {
    
    if (weekdayMap != null) {
      return;
    }
    
    weekdayMap = new Weekday[8];
    
    for (Weekday weekday : Weekday.values()) {
      weekdayMap[weekday.calendarConstant] = weekday;
    }
  }
  
}
