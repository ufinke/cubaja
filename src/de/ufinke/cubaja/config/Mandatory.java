// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an element or attribute as mandatory.
 * <p/>
 * When we want to force specification of an element
 * or an attribute in the XML document,
 * we can mark the appropriate method in our
 * <code>ConfigNode</code> subclass 
 * with this annotation.
 * @author Uwe Finke
 */
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value=ElementType.METHOD)
public @interface Mandatory {

}
