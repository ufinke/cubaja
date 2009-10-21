// Copyright (c) 2007 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.Date;

public class Period {

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
  
}
