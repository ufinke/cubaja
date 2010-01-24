// Copyright (c) 2007 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.io.*;
import java.text.*;

public class Period implements Iterable<Date>, Externalizable, Comparable<Period> {

  Date start;
  Date end;
  
  public Period(Date start, Date end) {
  
    this.start = start;
    this.end = end;
  }
  
  public Period(Calendar start, Calendar end) {
    
    this.start = start.getTime();
    this.end = end.getTime();
  }
  
  public boolean equals(Object object) {
    
    if (object instanceof Period) {
      Period other = (Period) object;
      return start.equals(other.start) && end.equals(other.end);
    }
    return false;
  }
  
  public int hashCode() {
    
    return start.hashCode() + end.hashCode();
  }
  
  public String toString() {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    StringBuilder sb = new StringBuilder(50);
    sb.append(sdf.format(start));
    sb.append(" - ");
    sb.append(sdf.format(end));
    return sb.toString();
  }
  
  public int compareTo(Period other) {
    
    int result = Util.compare(start, other.start);
    if (result == 0) {
      result = Util.compare(end, other.end);
    }
    return result;
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
        
        cal.add(Calendar.DATE, 1);
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

  public void writeExternal(ObjectOutput out) throws IOException {

    out.writeLong(start.getTime());
    out.writeLong(end.getTime());
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    start = new Date(in.readLong());
    end = new Date(in.readLong());
  }
}
