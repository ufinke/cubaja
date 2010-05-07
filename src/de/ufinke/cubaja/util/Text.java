// Copyright (c) 2008 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Combines <tt>ResourceBundle</tt> and <tt>MessageFormat</tt>.
 * @author Uwe Finke
 */
public class Text {

  private String bundleName;
  private ResourceBundle bundle;
  
  /**
   * Constructor with bundle name and locale.
   * @param bundleName bundle name
   * @param locale locale
   */
  public Text(String bundleName, Locale locale) {
  
    this.bundleName = bundleName;
    bundle = ResourceBundle.getBundle(bundleName, locale);
  }
  
  /**
   * Constructor with bundle name and default locale.
   * @param bundleName bundle name
   */
  public Text(String bundleName) {
    
    this(bundleName, Locale.getDefault());
  }
  
  /**
   * Constructor with the using class and a locale.
   * <p/>
   * The bundle name is derived from the class' package name
   * suffixed with '<tt>.text</tt>'.
   * @param usingClass the using class
   * @param locale locale
   */
  public Text(Class<?> usingClass, Locale locale) {
    
    this(usingClass.getPackage().getName() + ".text", locale);
  }
  
  /**
   * Constructor with the using class and the default locale.
   * <p/>
   * The bundle name is derived from the class' package name
   * suffixed with '<tt>.text</tt>'.
   * @param usingClass the using class
   */
  public Text(Class<?> usingClass) {
    
    this(usingClass, Locale.getDefault());
  }
  
  /**
   * Returns a formatted string.
   * @param key resourceBundle key
   * @param args arguments for MessageFormat
   * @return a string
   */
  public String get(String key, Object... args) {
    
    try {
      String rawText = bundle.getString(key);
      return args.length == 0 ? rawText : MessageFormat.format(rawText, args);
    } catch (MissingResourceException mre) {
      return getErrorText("Key not found", key, args);
    } catch (IllegalArgumentException iae) {
      return getErrorText("Exception raised by MessageFormat = " + iae.getMessage(), key, args);
    }
  }
  
  private String getErrorText(String reason, String key, Object... arguments) {
    
    StringBuilder sb = new StringBuilder(200);
    
    sb.append("?...? [Couldn't format text, reason: ");
    sb.append(reason);
    sb.append(", bundle=");
    sb.append(bundleName);
    sb.append(", key=");
    sb.append(key);
    sb.append(", arguments={");
    String separator = "";
    for (Object argument : arguments) {
      sb.append(separator);
      sb.append(argument);
      separator = ",";
    }
    sb.append("}]");
    
    return sb.toString();
  }
  
}
