// Copyright (c) 2007 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class Period {

  /**
   * Creates a <code>Date</code> object from year, month and day.
   * Note that, in contrary to <code>java.util.Calendar</code>,
   * january is month <code>1</code>.
   * @param year
   * @param month
   * @param dayOfMonth
   * @return date
   */
  static public Date createDate(int year, int month, int dayOfMonth) {
    
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month - 1);
    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    return cal.getTime();
  }

  /**
   * Returns a <code>Date</code> without time components.
   * @param date
   * @return date
   */
  static public Date stripTime(Date date) {
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }
  
  static public Date today() {
    
    return stripTime(new Date());
  }
  
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
