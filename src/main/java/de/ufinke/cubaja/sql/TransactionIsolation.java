// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import static java.sql.Connection.TRANSACTION_READ_COMMITTED;
import static java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;
import static java.sql.Connection.TRANSACTION_REPEATABLE_READ;
import static java.sql.Connection.TRANSACTION_SERIALIZABLE;

/**
 * Enum wrapper for <tt>Connection</tt> transaction isolation constants.
 * @author Uwe Finke
 */
public enum TransactionIsolation {

  READ_UNCOMMITTED(TRANSACTION_READ_UNCOMMITTED),
  READ_COMMITTED(TRANSACTION_READ_COMMITTED),
  REPEATABLE_READ(TRANSACTION_REPEATABLE_READ),
  SERIALIZABLE(TRANSACTION_SERIALIZABLE);
  
  private int level;
  
  private TransactionIsolation(int level) {
    
    this.level = level;
  }
  
  /**
   * Returns the transaction isolation constant.
   * @return transaction isolation
   */
  public int getLevel() {
    
    return level;
  }
}
