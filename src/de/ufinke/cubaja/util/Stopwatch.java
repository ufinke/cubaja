package de.ufinke.cubaja.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Stopwatch {

  static private Log logger = LogFactory.getLog(Stopwatch.class);
  
  private long startMillis;
  private String message;
  
  public Stopwatch() {
  
    startMillis = System.currentTimeMillis();
  }
  
  public Stopwatch(String message) {
    
    this();
    
    this.message = message;
    
    StringBuilder sb = new StringBuilder(message.length() + 8);
    sb.append(message);
    sb.append(" - start");
    logger.debug(sb);
  }
  
  public long elapsedMillis() {
    
    long elapsed = System.currentTimeMillis() - startMillis;
    
    if (message != null) {
      StringBuilder sb = new StringBuilder(message.length() + 48);
      sb.append(message);
      sb.append(" - end (elapsed: ");
      sb.append(format(elapsed));
      sb.append(')');
      logger.debug(sb);
    }
    
    return elapsed;
  }
  
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
