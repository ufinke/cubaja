// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Provider for a time or date property.
 * @author Uwe Finke
 */
public class TimestampProvider implements NamedPropertyProvider {

  /**
   * Constructor.
   */
  public TimestampProvider() {
    
  }
  
  /**
   * Returns a string with the actual date / time.
   * <p/>
   * We can specify the format by an optional <tt>parm</tt> 
   * with name <tt>pattern</tt>.
   * The default format is <tt>yyyyMMdd_HHmmss</tt>.
   * @return property value
   */
  public String getProperty(String name, Map<String, String> parms) {
    
    String pattern = parms.get("pattern");
    if (pattern == null) {
      pattern = "yyyyMMdd_HHmmss";
    }
    
    return new SimpleDateFormat(pattern).format(new Date());
  }
}
