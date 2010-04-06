// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a setter / adder method as not applicable for automatic configuration.
 * <p>
 * All methods of an element node class 
 * which have names starting with <tt>set</tt> or <tt>add</tt>,
 * have a <tt>void</tt> return type and exactly one parameter,
 * are selected for automatic setting of attribute or element values.
 * When you have methods which meet these conditions but don't want to be
 * used them in automatic configuration processing, you should mark them
 * with this annotation.
 * @author Uwe Finke
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface NoConfig {

}
