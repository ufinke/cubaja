// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.*;

/**
 * Factory needed internaly for <code>Query.readObject()</code>.
 * @author Uwe Finke
 */
public interface ObjectFactory {

  /**
   * Creates a data object.
   * @param query
   * @return data object
   * @throws CsvException
   */
  public Object createObject(Query query) throws SQLException;
}
