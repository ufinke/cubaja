/**
 * <p>
 * Simplified use of JDBC.
 * </p>
 * <p>
 * The central class is <code>Database</code>.
 * In an application independent from existing database connections, 
 * use the constructor with a <code>DatabaseConfig</code> parameter
 * and call the <code>createQuery</code> for <code>select</code> statements
 * or <code>createUpdate</code> for <code>insert</code>, <code>update</code>
 * or <code>delete</code> statements.
 * You may also call <code>execute</code> to execute an SQL statement a
 * immediately.
 * </p>
 * <p>
 * Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
 * <br/>
 * Subject to 
 * {@link <a href="http://www.opensource.org/licenses/bsd-license.php">BSD License</a>}. 
 * See <code>license.txt</code> distributed with this package.
 * </p>
 */
package de.ufinke.cubaja.sql;