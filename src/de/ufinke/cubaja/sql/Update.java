// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.PreparedStatement;

public class Update extends PreparedSql {

  Update(PreparedStatement statement, Sql sql) {
    
    super(statement, sql);
  }
}
