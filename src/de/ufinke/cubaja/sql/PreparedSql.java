// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.PreparedStatement;

public class PreparedSql {

  protected PreparedStatement statement;
  protected Sql sql;
  
  protected PreparedSql(PreparedStatement statement, Sql sql) {
  
    this.statement = statement;
    this.sql = sql;
  }

}
