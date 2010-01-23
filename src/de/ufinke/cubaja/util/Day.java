// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.*;
import static java.util.Calendar.*;
import java.io.*;

public class Day implements Cloneable, Comparable<Day>, Externalizable {

  private GregorianCalendar calendar;
  
  public Day() {
    
    calendar = new GregorianCalendar();
    stripTime();
  }
  
  public Day(Date date) {
    
    calendar = new GregorianCalendar();
    calendar.setTime(date);
    stripTime();
  }
  
  public Day(GregorianCalendar calendar) {
    
    this.calendar = (GregorianCalendar) calendar.clone();
    stripTime();
  }
  
  public Day(long millis) {
    
    calendar = new GregorianCalendar();
    calendar.setTimeInMillis(millis);
    stripTime();
  }
  
  public Day(int year, int month, int day) {
    
    calendar = new GregorianCalendar();
    calendar.clear();
    calendar.set(YEAR, year);
    calendar.set(MONTH, month - 1);
    calendar.set(DAY_OF_MONTH, day);
  }
  
  private void stripTime() {
    
    calendar.clear(HOUR);
    calendar.clear(MINUTE);
    calendar.clear(SECOND);
    calendar.clear(MILLISECOND);
  }
  
  public String toString() {
    
    return Util.format(getDate(), "yyyy-MM-dd");
  }
  
  public Day clone() {
    
    return new Day(calendar);
  }
  
  public boolean equals(Object object) {
    
    if (object instanceof Calendar) {
      Calendar other = (Calendar) object;
      return calendar.equals(other);
    }
    return false;
  }
  
  public int hashCode() {
    
    return calendar.hashCode();
  }
  
  public int compareTo(Day other) {
    
    return Util.compare(calendar, other.calendar);
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {

    out.writeShort(calendar.get(YEAR));
    out.writeByte(calendar.get(MONTH));
    out.writeByte(calendar.get(DAY_OF_MONTH));
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    calendar = new GregorianCalendar(in.readShort(), in.readByte(), in.readByte());
  }
  
  public int getYear() {
    
    return calendar.get(YEAR);
  }
  
  public int getMonth() {
    
    return calendar.get(MONTH) + 1;
  }
  
  public int getDay() {
    
    return calendar.get(DAY_OF_MONTH);
  }
  
  public Date getDate() {
    
    return calendar.getTime();
  }
  
  public GregorianCalendar getCalendar() {
    
    return (GregorianCalendar) calendar.clone();
  }
  
  public java.sql.Date getSqlDate() {
    
    return new java.sql.Date(calendar.getTimeInMillis());
  }
  
  public Weekday getWeekday() {
    
    return Weekday.getWeekday(calendar);
  }
  
  public String format(String pattern) {
    
    return Util.format(getDate(), pattern);
  }
  
  public boolean isWorkday(HolidayConfig config) {
    
    return isWorkday(config.getHolidayCalendar());
  }
  
  public boolean isWorkday(HolidayCalendar holidays) {
    
    return holidays.isWorkday(calendar);
  }
  
  public boolean isHoliday(HolidayConfig config) {
    
    return isHoliday(config.getHolidayCalendar());
  }
  
  public boolean isHoliday(HolidayCalendar holidays) {
    
    return holidays.isHoliday(calendar);
  }
  
  public boolean isFirstDayOfMonth() {
    
    return calendar.get(DAY_OF_MONTH) == 1;
  }
  
  public boolean isLastDayOfMonth() {
    
    return calendar.get(DAY_OF_MONTH) == calendar.getActualMaximum(DAY_OF_MONTH);
  }
  
  public boolean isFirstDayOfYear() {
    
    return calendar.get(MONTH) == 0 && calendar.get(DAY_OF_MONTH) == 1;
  }
  
  public boolean isLastDayOfYear() {
    
    return calendar.get(MONTH) == 11 && calendar.get(DAY_OF_MONTH) == 31;
  }
  
  public void addDays(int count) {
    
    calendar.add(DATE, count);
  }
  
  public void addWorkdays(int count, HolidayConfig config) {
    
    addWorkdays(count, config.getHolidayCalendar());
  }
  
  public void addWorkdays(int count, HolidayCalendar holidays) {
    
    int step = (count < 0) ? -1 : 1;
    count = Math.abs(count);
    while (count > 0) {
      calendar.add(DATE, step);
      if (holidays.isWorkday(calendar)) {
        count--;
      }
    }
  }
  
  public void addMonths(int count, boolean retainLastDayOfMonth) {
    
    boolean adjustLastDay = retainLastDayOfMonth && isLastDayOfMonth();
    calendar.add(MONTH, count);
    if (adjustLastDay) {
      adjustLastDayOfMonth();
    }
  }
  
  public void addYears(int count, boolean retainLastDayOfMonth) {
    
    boolean adjustLastDay = retainLastDayOfMonth && isLastDayOfMonth();
    calendar.add(YEAR, count);
    if (adjustLastDay) {
      adjustLastDayOfMonth();
    }
  }
  
  public void adjustNextWeekday(Weekday weekday) {
    
    int difference = weekday.getCalendarConstant() - calendar.get(DAY_OF_WEEK);
    if (difference == 0) {
      return;
    } else if (difference < 0) {
      difference += 7;
    }
    addDays(difference);
  }
  
  public void adjustPreviousWeekday(Weekday weekday) {
    
    int difference = weekday.getCalendarConstant() - calendar.get(DAY_OF_WEEK);
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
      addDays(1);
    }
  }
  
  public void adjustPreviousWorkday(HolidayConfig config) {
    
    adjustPreviousWorkday(config.getHolidayCalendar());
  }
  
  public void adjustPreviousWorkday(HolidayCalendar holidays) {
    
    while (isHoliday(holidays)) {
      addDays(-1);
    }
  }
  
  public void adjustNextHoliday(HolidayConfig config) {
    
    adjustNextHoliday(config.getHolidayCalendar());
  }
  
  public void adjustNextHoliday(HolidayCalendar holidays) {
    
    while (isWorkday(holidays)) {
      addDays(1);
    }
  }
  
  public void adjustPreviousHoliday(HolidayConfig config) {
    
    adjustPreviousHoliday(config.getHolidayCalendar());
  }
  
  public void adjustPreviousHoliday(HolidayCalendar holidays) {
    
    while (isWorkday(holidays)) {
      addDays(-1);
    }
  }
  
  public void adjustFirstDayOfMonth() {
    
    calendar.set(DAY_OF_MONTH, 1);
  }
  
  public void adjustLastDayOfMonth() {
    
    calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH));
  }
  
  public void adjustFirstDayOfYear() {
    
    calendar.set(MONTH, 0);
    calendar.set(DAY_OF_MONTH, 1);
  }
  
  public void adjustLastDayOfYear() {
    
    calendar.set(MONTH, 11);
    calendar.set(DAY_OF_MONTH, 31);
  }
  
}
