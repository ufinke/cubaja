// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Defines the pattern of attribute value or element content strings.
 * </p><p>
 * When a <code>Pattern</code> annotation exists for a setter / adder
 * method of a basic parameter type, the XML attribute value or normalized element content string
 * is checked against the specified pattern by means of the <code>String</code>s
 * <code>matches</code> method.
 * If the value does not match the regular expression of the pattern, an exception will be thrown.
 * The message includes the hint. If no hint is specified, the original pattern string will be used.
 * </p><p>
 * For element nodes, there is no pattern check.
 * </p><p>
 * For <code>java.util.Date</code> parameter types, the pattern is not a regular expression.
 * It is used for parsing
 * and has to be formed according
 * to the rules of {@link java.text.SimpleDateFormat}.
 * </p><p>
 * For numeric parameter types, the pattern is not used for parsing but
 * can be used to limit values to an explicit number of digits.  
 * </p>
 * @author Uwe Finke
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface Pattern {

  /**
   * The pattern.
   * @return the pattern
   */
  public String value();
  
  /**
   * Optional user-friendly text describing the pattern.
   * @return a text used in exception messages
   */
  public String hint() default "";
}
