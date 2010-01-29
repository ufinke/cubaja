// Copyright (c) 2007 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Period implements Externalizable, Comparable<Period> {

  Date begin;
  Date end;
  
  public Period(Date begin, Date end) {
  
    this.begin = begin;
    this.end = end;
  }
  
  public Period(Calendar begin, Calendar end) {
    
    this.begin = begin.getTime();
    this.end = end.getTime();
  }
  
  public boolean equals(Object object) {
    
    if (object instanceof Period) {
      Period other = (Period) object;
      return begin.equals(other.begin) && end.equals(other.end);
    }
    return false;
  }
  
  public int hashCode() {
    
    return begin.hashCode() + end.hashCode();
  }
  
  public String toString() {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    StringBuilder sb = new StringBuilder(50);
    sb.append(sdf.format(begin));
    sb.append(" - ");
    sb.append(sdf.format(end));
    return sb.toString();
  }
  
  public int compareTo(Period other) {
    
    int result = Util.compare(begin, other.begin);
    if (result == 0) {
      result = Util.compare(end, other.end);
    }
    return result;
  }
  
  public boolean contains(Date date) {
    
    return Util.compare(begin, date) <= 0
        && Util.compare(end, date) >= 0;
  }

  public Date getBegin() {
  
    return begin;
  }
  
  public Date getEnd() {
  
    return end;
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {

    out.writeLong(begin.getTime());
    out.writeLong(end.getTime());
  }

  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    begin = new Date(in.readLong());
    end = new Date(in.readLong());
  }
}
