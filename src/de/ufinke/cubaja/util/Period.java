// Copyright (c) 2007 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.Calendar;
import java.util.Date;

public class Period {

  public static Period createMonth(Date date) {
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    Date start = cal.getTime();
    
    cal.add(Calendar.MONTH, 1);
    cal.add(Calendar.DATE, -1);
    Date end = cal.getTime();
    
    return new Period(start, end);
  }
  
  public static Period createYear(Date date) {
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    
    cal.set(Calendar.DAY_OF_YEAR, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    Date start = cal.getTime();
    
    cal.add(Calendar.YEAR, 1);
    cal.add(Calendar.DATE, -1);
    Date end = cal.getTime();
    
    return new Period(start, end);
  }
  
  private Date start;
  private Date end;
  
  public Period(Date start, Date end) {
  
    this.start = start;
    this.end = end;
  }
  
  public boolean contains(Date date) {
    
    return Util.compare(start, date) <= 0
        && Util.compare(end, date) >= 0;
  }

  public Date getStart() {
  
    return start;
  }
  
  public Date getEnd() {
  
    return end;
  }
  
  public Period nextMonth() {
    
    Calendar cal = Calendar.getInstance();
    
    cal.setTime(start);
    cal.add(Calendar.MONTH, 1);
    Date start = cal.getTime();
    
    cal.setTime(end);
    cal.add(Calendar.DATE, 1);
    cal.add(Calendar.MONTH, 1);
    cal.add(Calendar.DATE, -1);
    Date end = cal.getTime();
    
    return new Period(start, end);
  }
  
}
