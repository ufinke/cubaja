// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as receiver for character data content.
 * The method must have a <code>String</code> parameter.
 * @author Uwe Finke
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface CharData {

}
