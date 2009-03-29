// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a setter / adder method as not applicable for automatic configuration.
 * <p/>
 * All methods of an element node class 
 * that have names starting with <code>set</code> or <code>add</code>,
 * have a <code>void</code> return type and exactly one parameter,
 * are selected for automatic setting of attribute or element values.
 * When we have methods which meet these conditions but we don't want to be
 * used them in automatic configuration processing, we must mark them
 * with this annotation.
 * @author Uwe Finke
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface NoConfig {

}
