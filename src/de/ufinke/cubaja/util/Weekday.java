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
  
  private Weekday(int calendarConstant) {
    
    this.calendarConstant = calendarConstant;
  }
  
  public int getCalendarConstant() {
    
    return calendarConstant;
  }
  
  /**
   * Returns a date's weekday.
   * @param date
   * @return weekday
   */
  static public Weekday getWeekday(Date date) {
    
    if (weekdayMap == null) {
      createWeekdayMap();
    }
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
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
