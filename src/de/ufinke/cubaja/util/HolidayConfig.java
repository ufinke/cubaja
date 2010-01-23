// Copyright (c) 2007 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import de.ufinke.cubaja.config.Mandatory;

public class HolidayConfig {

  static enum Type {

    DATE, WEEKDAY, FIX, EASTER
  }

  static protected class HolidayEntryConfig {

    private Type type;
    private String name;
    private Date validFrom;
    private Date validTo;

    protected HolidayEntryConfig(Type type) {

      this.type = type;
    }

    Type getType() {

      return type;
    }

    public void setName(String name) {

      this.name = name;
    }

    public String getName() {

      if (name == null) {
        name = "";
      }
      return name;
    }

    public Date getValidFrom() {

      return validFrom;
    }

    public void setValidFrom(Date validFrom) {

      this.validFrom = validFrom;
    }

    public Date getValidTo() {

      return validTo;
    }

    public void setValidTo(Date validTo) {

      this.validTo = validTo;
    }
  }

  static public class DateConfig extends HolidayEntryConfig {

    private Date date;

    public DateConfig() {

      super(Type.DATE);
    }

    public Date getDate() {

      return date;
    }

    @Mandatory
    public void setDate(Date date) {

      this.date = date;
    }
  }

  static public class WeekdayConfig extends HolidayEntryConfig {

    private Weekday[] days;

    public WeekdayConfig() {

      super(Type.WEEKDAY);
    }

    public Weekday[] getDays() {

      return days;
    }

    @Mandatory
    public void setDays(Weekday[] days) {

      this.days = days;
    }
  }

  static public class FixConfig extends HolidayEntryConfig {

    private int day;
    private int month;

    public FixConfig() {

      super(Type.FIX);
    }

    public int getDay() {

      return day;
    }

    @Mandatory
    public void setDay(int day) {

      this.day = day;
    }

    public int getMonth() {

      return month;
    }

    @Mandatory
    public void setMonth(int month) {

      this.month = month;
    }
  }

  static public class EasterConfig extends HolidayEntryConfig {

    private int offset;

    public EasterConfig() {

      super(Type.EASTER);
    }

    public int getOffset() {

      return offset;
    }

    @Mandatory
    public void setOffset(int offset) {

      this.offset = offset;
    }
  }
  
  private List<HolidayEntryConfig> entryList;
  private HolidayCalendar calendar;
  
  public HolidayConfig() {
  
    entryList = new ArrayList<HolidayEntryConfig>();
  }
  
  public HolidayCalendar getHolidayCalendar() {
    
    if (calendar == null) {
      calendar = new HolidayCalendar(this);
    }
    return calendar;
  }
  
  public List<HolidayEntryConfig> getEntryList() {
    
    return entryList;
  }
  
  public void addDate(DateConfig dateConfig) {
    
    entryList.add(dateConfig);
  }
  
  public void addWeekday(WeekdayConfig weekdayConfig) {
    
    entryList.add(weekdayConfig);
  }
  
  public void addFix(FixConfig fixConfig) {
    
    entryList.add(fixConfig);
  }
  
  public void addEaster(EasterConfig easterConfig) {
    
    entryList.add(easterConfig);
  }
}
