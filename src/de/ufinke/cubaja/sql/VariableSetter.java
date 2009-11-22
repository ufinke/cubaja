// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

public interface VariableSetter {

  public void setVariables(PreparedSql ps, Object dataObject) throws Exception;
}
