/**
 * <p>
 * Simplified use of JDBC.
 * </p>
 * <p>
 * The central class is <tt>Database</tt>.
 * In an application independent from existing database connections, 
 * use the constructor with a <tt>DatabaseConfig</tt> parameter
 * and call the <tt>createQuery</tt> for <tt>select</tt> statements
 * or <tt>createUpdate</tt> for <tt>insert</tt>, <tt>update</tt>
 * or <tt>delete</tt> statements.
 * You may also call <tt>execute</tt> to execute an SQL statement a
 * immediately.
 * </p>
 * <p>
 * Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
 * <br/>
 * Subject to 
 * {@link <a href="http://www.opensource.org/licenses/bsd-license.php">BSD License</a>}. 
 * See <tt>license.txt</tt> distributed with this package.
 * </p>
 */
package de.ufinke.cubaja.sql;