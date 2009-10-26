// Copyright (c) 2007 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

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
  
  Date start;
  Date end;
  
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
  
  public Period addMonth(int count) {
    
    Calendar cal = Calendar.getInstance();
    
    cal.setTime(start);
    cal.add(Calendar.MONTH, count);
    Date start = cal.getTime();
    
    cal.setTime(end);
    cal.add(Calendar.DATE, 1);
    cal.add(Calendar.MONTH, count);
    cal.add(Calendar.DATE, -1);
    Date end = cal.getTime();
    
    return new Period(start, end);
  }
  
  public Period addYear(int count) {
    
    Calendar cal = Calendar.getInstance();
    
    cal.setTime(start);
    cal.add(Calendar.YEAR, count);
    Date start = cal.getTime();
    
    cal.setTime(end);
    cal.add(Calendar.DATE, 1);
    cal.add(Calendar.YEAR, count);
    cal.add(Calendar.DATE, -1);
    Date end = cal.getTime();
    
    return new Period(start, end);
  }
  
  public Iterable<Date> iterateDays(final int step) {
    
    return new Iterable<Date>() {

      public Iterator<Date> iterator() {

        return new Iterator<Date>() {
          
          private Calendar cal = initCal();
          private Date nextDate;

          private Calendar initCal() {
          
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);
            
            if (Util.compare(start, end) <= 0) {
              nextDate = start;
            }
            
            return cal;
          }
          
          public boolean hasNext() {

            return nextDate != null;
          }

          public Date next() {

            Date result = nextDate;
            
            cal.add(Calendar.DATE, step);
            nextDate = cal.getTime();
            if (nextDate.compareTo(end) > 0) {
              nextDate = null;
            }
            
            return result;
          }

          public void remove() throws UnsupportedOperationException {
            
            throw new UnsupportedOperationException();
          }          
        };
      }
    };
  }
  
}
