// Copyright (c) 2006 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

/**
 * Configuration exception.
 * @author Uwe Finke
 */
public class ConfigException extends Exception {

  /**
   * Constructor with message.
   * @param msg message text
   */
  public ConfigException(String msg) {
    
    super(msg);
  }
  
  /**
   * Constructor with cause.
   * @param t cause of exception
   */
  public ConfigException(Throwable t) {

    super(t);
  }

  /**
   * Constructor with message and cause.
   * @param msg message text.
   * @param t cause wrapped by this exception
   */
  public ConfigException(String msg, Throwable t) {
    
    super(msg, t);
  }

  /**
   * Returns message text.
   * If this object has no own message text, the
   * message text of the cause is returned.
   * @return message text
   */
  public String getMessage() {
    
    if (super.getMessage() == null && getCause() != null) {
      return getCause().getMessage();
    } else {
      return super.getMessage();
    }
  }
  
  /**
   * Returns localized message text.
   * If this object has no own localized message text, the
   * localized message text of the cause is returned.
   * @return message text
   */
  public String getLocalizedMessage() {
    
    if (super.getLocalizedMessage() == null && getCause() != null) {
      return getCause().getLocalizedMessage();
    } else {
      return super.getLocalizedMessage();
    }
  }
  
}
