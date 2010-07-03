// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import org.xml.sax.SAXException;

/**
 * Configuration exception.
 * @author Uwe Finke
 */
public class ConfigException extends SAXException {

  /**
   * Constructor with message.
   * @param msg message text
   */
  public ConfigException(String msg) {
    
    super(msg);
  }
  
  /**
   * Constructor with cause.
   * @param e cause of exception
   */
  public ConfigException(Exception e) {

    super(e);
  }

  /**
   * Constructor with message and cause.
   * @param msg message text.
   * @param t cause of exception
   */
  public ConfigException(String msg, Exception e) {
    
    super(msg, e);
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
