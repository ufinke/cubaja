/**
 * Simplified use of JDBC.
 * <p>
 * The central class is {@link de.ufinke.cubaja.sql.Database Database}.
 * In an application which doesn't depend on existing <tt>Connection</tt> instances, 
 * use the constructor with a {@link de.ufinke.cubaja.sql.DatabaseConfig DatabaseConfig} parameter
 * and call {@link de.ufinke.cubaja.sql.Database#createQuery createQuery} 
 * to create <tt>select</tt> statements
 * or {@link de.ufinke.cubaja.sql.Database#createUpdate createUpdate}
 * to create data manipulation (<tt>insert</tt>, <tt>update</tt> or <tt>delete</tt>) statements.
 * You may also call {@link de.ufinke.cubaja.sql.Database#execute execute}
 * to execute an SQL statement immediately.
 * <p>
 * Copyright (c) 2006 - 2010, Uwe Finke. All rights reserved.
 * <br>
 * Subject to 
 * {@link <a href="http://www.opensource.org/licenses/bsd-license.php">BSD License</a>}. 
 * See <tt>license.txt</tt> distributed with this library.
 */
package de.ufinke.cubaja.sql;