// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

/**
 * Interface needed internally for bytecode generation.
 * @author Uwe Finke
 */
public interface VariableSetter {

  /**
   * Sets variables of a prepared statement to data objects values.
   * @param ps
   * @param dataObject
   * @throws Exception
   */
  public void setVariables(PreparedSql ps, Object dataObject) throws Exception;
}
