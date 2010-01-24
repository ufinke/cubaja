// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.*;
import java.io.*;

public class Day extends GregorianCalendar implements Externalizable {

  public Day() {
    
    stripTime();
  }
  
  public Day(Date date) {
    
    setTime(date);
    stripTime();
  }
  
  public Day(Calendar calendar) {

    setTimeInMillis(calendar.getTimeInMillis());
    stripTime();
  }
  
  public Day(long millis) {
    
    setTimeInMillis(millis);
    stripTime();
  }
  
  public Day(int year, int month, int day) {
    
    set(YEAR, year);
    set(MONTH, month - 1);
    set(DAY_OF_MONTH, day);
    stripTime();
  }
  
  private void stripTime() {
    
    set(HOUR, 0);
    set(MINUTE, 0);
    set(SECOND, 0);
    set(MILLISECOND, 0);
  }
  
  public String toString() {
    
    return Util.format(getTime(), "yyyy-MM-dd");
  }

  public Day clone() {
    
    return new Day(getTimeInMillis());
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {

    out.writeShort(get(YEAR));
    out.writeByte(get(MONTH));
    out.writeByte(get(DAY_OF_MONTH));
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    clear();
    set(YEAR, in.readShort());
    set(MONTH, in.readByte());
    set(DAY_OF_MONTH, in.readByte());
  }
  
  public int year() {
    
    return get(YEAR);
  }
  
  public int month() {
    
    return get(MONTH) + 1;
  }
  
  public int day() {
    
    return get(DAY_OF_MONTH);
  }
  
  public Date date() {
    
    return getTime();
  }

  public Calendar calendar() {
    
    return clone();
  }
  
  public java.sql.Date getSqlDate() {
    
    return new java.sql.Date(getTimeInMillis());
  }
  
  public Weekday getWeekday() {
    
    return Weekday.getWeekday(this);
  }
  
  public String format(String pattern) {
    
    return Util.format(getTime(), pattern);
  }
  
  public boolean isWorkday(HolidayConfig config) {
    
    return isWorkday(config.getHolidayCalendar());
  }
  
  public boolean isWorkday(HolidayCalendar holidays) {
    
    return holidays.isWorkday(this);
  }
  
  public boolean isHoliday(HolidayConfig config) {
    
    return isHoliday(config.getHolidayCalendar());
  }
  
  public boolean isHoliday(HolidayCalendar holidays) {
    
    return holidays.isHoliday(this);
  }
  
  public boolean isFirstDayOfMonth() {
    
    return get(DAY_OF_MONTH) == 1;
  }
  
  public boolean isLastDayOfMonth() {
    
    return get(DAY_OF_MONTH) == getActualMaximum(DAY_OF_MONTH);
  }
  
  public boolean isFirstDayOfYear() {
    
    return get(MONTH) == 0 && get(DAY_OF_MONTH) == 1;
  }
  
  public boolean isLastDayOfYear() {
    
    return get(MONTH) == 11 && get(DAY_OF_MONTH) == 31;
  }
  
  public void addDays(int count) {
    
    add(DATE, count);
  }
  
  public void addWorkdays(int count, HolidayConfig config) {
    
    addWorkdays(count, config.getHolidayCalendar());
  }
  
  public void addWorkdays(int count, HolidayCalendar holidays) {
    
    int step = (count < 0) ? -1 : 1;
    count = Math.abs(count);
    while (count > 0) {
      add(DATE, step);
      if (holidays.isWorkday(this)) {
        count--;
      }
    }
  }
  
  public void addMonths(int count) {
    
    add(MONTH, count);
  }
  
  public void addMonths(int count, boolean retainLastDayOfMonth) {
    
    boolean adjustLastDay = retainLastDayOfMonth && isLastDayOfMonth();
    add(MONTH, count);
    if (adjustLastDay) {
      adjustLastDayOfMonth();
    }
  }
  
  public void addYears(int count) {
    
    add(YEAR, count);
  }
  
  public void addYears(int count, boolean retainLastDayOfMonth) {
    
    boolean adjustLastDay = retainLastDayOfMonth && isLastDayOfMonth();
    add(YEAR, count);
    if (adjustLastDay) {
      adjustLastDayOfMonth();
    }
  }
  
  public void adjustNextWeekday(Weekday weekday) {
    
    int difference = weekday.getCalendarConstant() - get(DAY_OF_WEEK);
    if (difference == 0) {
      return;
    } else if (difference < 0) {
      difference += 7;
    }
    addDays(difference);
  }
  
  public void adjustPreviousWeekday(Weekday weekday) {
    
    int difference = weekday.getCalendarConstant() - get(DAY_OF_WEEK);
    if (difference == 0) {
      return;
    } else if (difference > 0) {
      difference -= 7;
    }
    addDays(difference);
  }
  
  public void adjustNextWorkday(HolidayConfig config) {
    
    adjustNextWorkday(config.getHolidayCalendar());
  }
  
  public void adjustNextWorkday(HolidayCalendar holidays) {
    
    while (isHoliday(holidays)) {
      add(DATE, 1);
    }
  }
  
  public void adjustPreviousWorkday(HolidayConfig config) {
    
    adjustPreviousWorkday(config.getHolidayCalendar());
  }
  
  public void adjustPreviousWorkday(HolidayCalendar holidays) {
    
    while (isHoliday(holidays)) {
      add(DATE, -1);
    }
  }
  
  public void adjustNextHoliday(HolidayConfig config) {
    
    adjustNextHoliday(config.getHolidayCalendar());
  }
  
  public void adjustNextHoliday(HolidayCalendar holidays) {
    
    while (isWorkday(holidays)) {
      add(DATE, 1);
    }
  }
  
  public void adjustPreviousHoliday(HolidayConfig config) {
    
    adjustPreviousHoliday(config.getHolidayCalendar());
  }
  
  public void adjustPreviousHoliday(HolidayCalendar holidays) {
    
    while (isWorkday(holidays)) {
      add(DATE, -1);
    }
  }
  
  public void adjustFirstDayOfMonth() {
    
    set(DAY_OF_MONTH, 1);
  }
  
  public void adjustLastDayOfMonth() {
    
    set(DAY_OF_MONTH, getActualMaximum(DAY_OF_MONTH));
  }
  
  public void adjustFirstDayOfYear() {
    
    set(MONTH, 0);
    set(DAY_OF_MONTH, 1);
  }
  
  public void adjustLastDayOfYear() {
    
    set(MONTH, 11);
    set(DAY_OF_MONTH, 31);
  }
  
  public int dayCount(Calendar until) {
    
    if (get(YEAR) == until.get(YEAR)) {
      return until.get(DAY_OF_YEAR) - get(DAY_OF_YEAR);
    }
    
    boolean swap = compareTo(until) > 0;
    
    Calendar from = swap ? until : this;
    Calendar to = swap ? this : until;
    
    Day calc = new Day(from);
    calc.adjustLastDayOfYear();
    int count = calc.get(DAY_OF_YEAR) - from.get(DAY_OF_YEAR);
    calc.add(DATE, 1);
    while (calc.get(YEAR) != to.get(YEAR)) {
      count += calc.getActualMaximum(DAY_OF_YEAR);
      calc.add(YEAR, 1);
    }
    count += to.get(DAY_OF_YEAR);
    
    if (swap) {
      count *= -1;
    }
    return count;
  }
  
}