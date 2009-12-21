// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Wrapper for <code>insert</code>, <code>update</code> or <code>delete</code> statements.
 * An instance is created by an appropriate <code>Database</code> method.
 * @author Uwe Finke
 */
public class Update extends PreparedSql {

  static private final int[] EMPTY_UPDATE_COUNT = new int[0];
  
  private int batchSize;
  private int intervalBatchCount;
  private int totalBatchCount;
  private boolean resetBatchCount;
  
  Update(PreparedStatement statement, Sql sql, DatabaseConfig config) {
    
    super(statement, sql, config);
    
    batchSize = config.getBatchSize();
  }

  /**
   * Executes the statement immediately.
   * Calls the <code>PreparedStatement</code>'s <code>executeUpdate</code> method.
   * @return number of concerned rows.
   * @throws SQLException
   */
  public int executeUpdate() throws SQLException {
    
    return statement.executeUpdate();
  }
  
  /**
   * Adds a row to a bulk operation.
   * If the configuration's <code>batchSize</code> value
   * has been reached, this method  
   * calls the <code>PreparedStatement</code>'s <code>executeBatch</code> method
   * automatically.
   * After that, the
   * <code>PreparedStatement</code>'s <code>addBatch</code> method is called.
   * @return number of concerned rows (result from <code>executeBatch</code>)
   * @throws SQLException
   */
  public int[] addBatch() throws SQLException {

    if (resetBatchCount) {
      totalBatchCount = 0;
      resetBatchCount = false;
    }
    
    int[] updateCount = EMPTY_UPDATE_COUNT;
    
    if (intervalBatchCount == batchSize) {
      updateCount = executeBatch(false);
    }
    intervalBatchCount++;
    
    statement.addBatch();
    
    return updateCount;
  }
  
  /**
   * Writes the rows supplied by <code>addBatch</code> to the database.
   * Calls the <code>PreparedStatement</code>'s <code>executeBatch</code> method.
   * @return number of concerned rows (not including the result of intermediate calls triggered automatically by <code>addBatch</code>)
   * @throws SQLException
   */
  public int[] executeBatch() throws SQLException {

    return executeBatch(true);
  }
  
  private int[] executeBatch(boolean directCall) throws SQLException {
    
    int[] updateCount = EMPTY_UPDATE_COUNT;
    
    if (intervalBatchCount > 0) {
      updateCount = statement.executeBatch();      
      totalBatchCount += intervalBatchCount;
      intervalBatchCount = 0;
    }
    
    return updateCount;
  }
  
  /**
   * Returns the total number of <code>addBatch</code> calls.
   * After a direct call to <code>executeBatch</code> the counter
   * is reset on the first subsequent call to <code>addBatch</code>.
   * @return number of requests
   */
  public int getBatchCount() {
    
    return totalBatchCount + intervalBatchCount;
  }
}
