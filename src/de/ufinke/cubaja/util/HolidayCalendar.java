// Copyright (c) 2007 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_WEEK;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HolidayCalendar {

  private final HolidayConfig config;
  private Map<Integer, BitSet> map;
  
  public HolidayCalendar(HolidayConfig config) {
    
    this.config = config;
    map = new ConcurrentHashMap<Integer, BitSet>();
  }
  
  public boolean isHoliday(Date date) {
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    return isHoliday(cal);
  }
  
  public boolean isHoliday(Calendar cal) {
    
    int year = cal.get(YEAR);
    BitSet set = map.get(year);
    if (set == null) {
      set = createSet(year);
      map.put(year, set);
    }
    return set.get(cal.get(DAY_OF_YEAR));
  }
  
  public boolean isWorkday(Date date) {
    
    return ! isHoliday(date);
  }
  
  public boolean isWorkday(Calendar cal) {
    
    return ! isHoliday(cal);
  }
  
  private BitSet createSet(int year) {
    
    BitSet set = new BitSet(367);
    Calendar cal = Calendar.getInstance();
    Calendar easter = null;
    
    for (HolidayConfig.HolidayEntryConfig entry : config.getEntryList()) {
      switch (entry.getType()) {
        case DATE:
          addDate(cal, year, set, entry);
          break;
        case FIX:
          addFix(cal, year, set, entry);
          break;
        case WEEKDAY:
          addWeekday(cal, year, set, entry);
          break;
        case EASTER:
          if (easter == null) {
            easter = computeEaster(year);
          }
          addEaster(cal, easter, set, entry);
          break;
      }
    }
    
    return set;
  }
  
  private void addDate(Calendar cal, int year, BitSet set, HolidayConfig.HolidayEntryConfig entry) {
    
    HolidayConfig.DateConfig config = (HolidayConfig.DateConfig) entry;
    
    cal.setTime(config.getDate());
    
    if (year != cal.get(YEAR)) {
      return;
    }
    if (! isValid(cal, entry)) {
      return;
    }
        
    set.set(cal.get(DAY_OF_YEAR));
  }
  
  private void addFix(Calendar cal, int year, BitSet set, HolidayConfig.HolidayEntryConfig entry) {
    
    HolidayConfig.FixConfig config = (HolidayConfig.FixConfig) entry;
    
    cal.clear();
    cal.set(YEAR, year);
    cal.set(MONTH, config.getMonth() - 1);
    cal.set(DAY_OF_MONTH, config.getDay());
    
    if (! isValid(cal, entry)) {
      return;
    }
    
    set.set(cal.get(DAY_OF_YEAR));
  }
  
  private void addWeekday(Calendar cal, int year, BitSet set, HolidayConfig.HolidayEntryConfig entry) {
    
    HolidayConfig.WeekdayConfig config = (HolidayConfig.WeekdayConfig) entry;    
    
    cal.clear();
    cal.set(YEAR, year);
    cal.set(MONTH, 0);
    cal.set(DAY_OF_MONTH, 1);
    if (entry.getValidFrom() != null) {
      if (Util.compare(entry.getValidFrom(), cal.getTime()) > 0) {
        cal.setTime(entry.getValidFrom());
      }
    }
    
    Calendar limit = Calendar.getInstance();
    limit.clear();
    limit.set(YEAR, year);
    limit.set(MONTH, 11);
    limit.set(DAY_OF_MONTH, 31);
    if (entry.getValidTo() != null) {
      if (Util.compare(entry.getValidTo(), limit.getTime()) < 0) {
        limit.setTime(entry.getValidTo());
      }
    }
    
    while (cal.compareTo(limit) <= 0) {
      int dayOfWeek = cal.get(DAY_OF_WEEK);
      for (Weekday weekday : config.getDays()) {
        if (weekday.getCalendarConstant() == dayOfWeek) {
          set.set(cal.get(DAY_OF_YEAR));
        }
      }
      cal.add(DATE, 1);
    }
  }
  
  private void addEaster(Calendar cal, Calendar easter, BitSet set, HolidayConfig.HolidayEntryConfig entry) {
    
    HolidayConfig.EasterConfig config = (HolidayConfig.EasterConfig) entry;
    
    cal.clear();
    cal.set(YEAR, easter.get(YEAR));
    cal.set(MONTH, easter.get(MONTH) - 1);
    cal.set(DAY_OF_MONTH, easter.get(DAY_OF_MONTH));
    cal.add(DATE, config.getOffset());
    
    if (! isValid(cal, entry)) {
      return;
    }
    
    set.set(cal.get(DAY_OF_YEAR));
  }
  
  private boolean isValid(Calendar cal, HolidayConfig.HolidayEntryConfig entry) {
    
    Date date = null;
    
    Date from = entry.getValidFrom();
    if (from != null) {
      date = cal.getTime();
      if (Util.compare(from, date) > 0) {
        return false;
      }
    }
    
    Date to = entry.getValidTo();
    if (to != null) {
      if (date == null) {
        date = cal.getTime();
      }
      if (Util.compare(to, date) < 0) {
        return false;
      }
    }
    
    return true;
  }
  
  private Calendar computeEaster(int year) {
    
    int i = year % 19; 
    int j = year / 100; 
    int k = year % 100; 

    int l = (19 * i + j - (j / 4) - ((j - ((j + 8) / 25) + 1) / 3) + 15) % 30; 
    int m = (32 + 2 * (j % 4) + 2 * (k / 4) - l - (k % 4)) % 7; 
    int n = l + m - 7 * ((i + 11 * l + 22 * m) / 451) + 114; 

    int month = n / 31; 
    int day = (n % 31) + 1;
    
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(YEAR, year);
    cal.set(MONTH, month);
    cal.set(DAY_OF_MONTH, day);
    return cal;
  }
}
