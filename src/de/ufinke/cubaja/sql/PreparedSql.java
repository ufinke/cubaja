// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.PreparedStatement;

public class PreparedSql {

  protected PreparedStatement statement;
  
  protected PreparedSql() {
  
  }

  String prepareString(String sql) {
    
    //TODO
    return sql;
  }
  
  void setStatement(PreparedStatement statement) {
    
    this.statement = statement;
  }
}
