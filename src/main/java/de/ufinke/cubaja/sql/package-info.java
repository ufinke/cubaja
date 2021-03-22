/**
 * <p>
 * Simplified use of JDBC.
 * </p><p>
 * The central class is {@link Database}.
 * In an application which does not depend on existing <code>Connection</code> instances, 
 * use the constructor with a {@link DatabaseConfig} parameter
 * and call {@link de.ufinke.cubaja.sql.Database#createQuery createQuery} 
 * to create <code>select</code> statements
 * or {@link de.ufinke.cubaja.sql.Database#createUpdate createUpdate}
 * to create data manipulation (<code>insert</code>, <code>update</code> or <code>delete</code>) statements!
 * You may also call {@link de.ufinke.cubaja.sql.Database#execute execute}
 * to execute an SQL statement immediately.
 * </p><p>
 * Copyright (c) 2006 - 2010, Uwe Finke. All rights reserved.
 * <br>
 * Subject to 
 * <a href="http://www.opensource.org/licenses/bsd-license.php">BSD License</a>. 
 * See <code>license.txt</code> distributed with this library.
 * </p>
 */
package de.ufinke.cubaja.sql;