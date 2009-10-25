// Copyright (c) 2007 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.*;

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
    
  }
  
  public int getCalendarConstant() {
    
    return calendarConstant;
  }
  
}
