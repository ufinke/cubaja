// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A stopwatch to measure elapsed time.
 * <p>
 * @author Uwe Finke
 */
public class Stopwatch {

  static private Log logger = LogFactory.getLog(Stopwatch.class);
  static private Text text = Text.getPackageInstance(Stopwatch.class);
  
  private long startMillis;
  private String message;
  
  /**
   * Takes the start time.
   */
  public Stopwatch() {
  
    startMillis = System.currentTimeMillis();
  }
  
  /**
   * Takes the start time and logs a start message.
   * <p>
   * A subsequent call to <tt>elapsedMillis</tt>
   * will also log an end message together with the elapsed time.
   * @param message
   */
  public Stopwatch(String message) {
    
    this();
    
    this.message = message;
    
    StringBuilder sb = new StringBuilder(message.length() + 8);
    sb.append(message);
    sb.append(" - ");
    sb.append(text.get("stopwatchStart"));
    logger.debug(sb);
  }
  
  /**
   * Returns the elapsed time in milliseconds.
   * This method may be called more than once.
   * The start time is always the instantiation time of this object.
   * If there has been a message defined in the constructor, 
   * this method will log an end message together with the elapsed time.
   * @return millis
   */
  public long elapsedMillis() {
    
    long elapsed = System.currentTimeMillis() - startMillis;
    
    if (message != null) {
      StringBuilder sb = new StringBuilder(message.length() + 48);
      sb.append(message);
      sb.append(" - ");
      sb.append(text.get("stopwatchEnd"));
      sb.append(' ');
      sb.append(format(elapsed));
      sb.append(')');
      logger.debug(sb);
    }
    
    return elapsed;
  }
  
  /**
   * Formats a duration given in milliseconds.
   * <p>
   * The format is <tt><i>N</i>d <i>N</i>h <i>N</i>m <i>N</i>s <i>N</i>ms</tt>
   * where <tt>d</tt> are days, <tt>h</tt> are hours, <tt>m</tt>
   * are minutes, <tt>s</tt> are seconds and <tt>ms</tt> are milliseconds.
   * Seconds and larger units are part of the result only when the value of the
   * highest unit is not <tt>0</tt>. 
   * @param millis
   * @return formatted string
   */
  public String format(long millis) {
    
    StringBuilder sb = new StringBuilder(32);

    long ms = millis % 1000;
    if (millis >= 1000) {
      millis /= 1000;
      long s = millis % 60;
      if (millis >= 60) {
        millis /= 60;
        long m = millis % 60;
        if (millis >= 60) {
          millis /= 60;
          long h = millis % 24;
          if (millis >= 24) {
            millis /= 24;
            long d = millis;
            sb.append(d);
            sb.append("d ");
          }
          sb.append(h);
          sb.append("h ");
        }
        sb.append(m);
        sb.append("m ");
      }
      sb.append(s);
      sb.append("s ");
    }
    sb.append(ms);
    sb.append("ms");
    
    return sb.toString();
  }
}
