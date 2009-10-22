// Copyright (c) 2007 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HolidayCalendar {

  private HolidayConfig config;
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
  
  private BitSet createSet(int year) {
    
    BitSet set = new BitSet(367);
    Calendar cal = Calendar.getInstance();
    Calendar easter = null;
    
    for (HolidayConfig.HolidayEntryConfig entry : config.getEntryList()) {
      switch (entry.getType()) {
        case DATE:
          addDate(year, set, entry);
          break;
        case FIX:
          addFix(year, set, entry);
          break;
        case WEEKDAY:
          addWeekday(year, set, entry);
          break;
        case EASTER:
          if (easter == null) {
            easter = computeEaster(year);
          }
          addEaster(easter, set, entry);
          break;
      }
    }
    
    return set;
  }
  
  private void addDate(int year, BitSet set, HolidayConfig.HolidayEntryConfig entry) {
    
    HolidayConfig.DateConfig config = (HolidayConfig.DateConfig) entry;
    if (! isValid(config.getDate(), entry)) {
      return;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(config.getDate());
    if (year != cal.get(YEAR)) {
      return;
    }
    set.set(cal.get(DAY_OF_YEAR));
  }
  
  private void addFix(int year, BitSet set, HolidayConfig.HolidayEntryConfig entry) {
    
    HolidayConfig.FixConfig config = (HolidayConfig.FixConfig) entry;
    
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(YEAR, year);
    cal.set(MONTH, config.getMonth() - 1);
    cal.set(DAY_OF_MONTH, config.getDay());
    
    Date date = cal.getTime();
    if (! isValid(date, entry)) {
      return;
    }
    
    set.set(cal.get(DAY_OF_MONTH));
  }
  
  private void addWeekday(int year, BitSet set, HolidayConfig.HolidayEntryConfig entry) {
    
    HolidayConfig.WeekdayConfig config = (HolidayConfig.WeekdayConfig) entry;
  }
  
  private void addEaster(Calendar easter, BitSet set, HolidayConfig.HolidayEntryConfig entry) {
    
    HolidayConfig.EasterConfig config = (HolidayConfig.EasterConfig) entry;
  }
  
  private boolean isValid(Date date, HolidayConfig.HolidayEntryConfig entry) {
    
    Date from = entry.getValidFrom();
    if (from != null) {
      if (Util.compare(from, date) > 0) {
        return false;
      }
    }
    
    Date to = entry.getValidTo();
    if (to != null) {
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
