// Copyright (c) 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.SQLException;

public interface ExecFilter {

  public String filterExecStatement(String input) throws SQLException;
}
