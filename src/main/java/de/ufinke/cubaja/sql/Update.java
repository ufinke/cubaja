// Copyright (c) 2006 - 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Wrapper for <code>insert</code>, <code>update</code> or <code>delete</code> statements.
 * An instance is created by an appropriate {@link Database} method.
 * @author Uwe Finke
 */
public class Update extends PreparedSql {

  static private final int[] EMPTY_UPDATE_COUNT = new int[0];
  
  private int batchSize;
  private int intervalBatchCount;
  private int totalBatchCount;
  private boolean resetBatchCount;
  private BatchGroup batchGroup;
  
  Update(PreparedStatement statement, Sql sql, DatabaseConfig config) {
    
    super(statement, sql, config);
    
    batchSize = config.getBatchSize();
  }
  
  void setBatchGroup(BatchGroup batchGroup) {
    
    this.batchGroup = batchGroup;
  }

  /**
   * Executes the statement immediately.
   * Calls the <code>PreparedStatement</code>'s <code>executeUpdate</code> method.
   * @return number of concerned rows.
   * @throws SQLException when an exception occurs during SQL execution
   */
  public int executeUpdate() throws SQLException {
    
    return statement.executeUpdate();
  }
  
  /**
   * Adds a row to a bulk operation.
   * <p>
   * Calls the
   * <code>PreparedStatement</code>'s <code>addBatch</code> method.
   * If the configuration's <code>batchSize</code> value
   * has been reached, this method  
   * calls the <code>PreparedStatement</code>'s <code>executeBatch</code> method
   * automatically.
   * <p>
   * If this <code>Update</code> is a member of a {@link BatchGroup},
   * the result will always be an empty array.
   * @return array of number of concerned rows (result from <code>executeBatch</code>)
   * @throws SQLException when an exception occurs during SQL execution
   */
  public int[] addBatch() throws SQLException {

    if (resetBatchCount) {
      totalBatchCount = 0;
      resetBatchCount = false;
    }
    
    statement.addBatch();
    intervalBatchCount++;
    
    int[] updateCount = EMPTY_UPDATE_COUNT;    
    if (intervalBatchCount == batchSize) {
      if (batchGroup == null) {
        updateCount = doExecuteBatch();
      } else {
        batchGroup.executeBatch();
      }
    }    
    return updateCount;
  }
  
  /**
   * Writes the rows supplied by <code>addBatch</code> to the database.
   * Calls the <code>PreparedStatement</code>'s <code>executeBatch</code> method.
   * <p>
   * If this <code>Update</code> is a member of a {@link BatchGroup},
   * the result will always be an empty array.
   * @return array of number of concerned rows (not including the result of intermediate calls triggered automatically by <code>addBatch</code>)
   * @throws SQLException when an exception occurs during SQL execution
   */
  public int[] executeBatch() throws SQLException {

    resetBatchCount = true;
    
    if (batchGroup == null) {
      return doExecuteBatch();
    } else {
      batchGroup.executeBatch();
      return EMPTY_UPDATE_COUNT;
    }
  }
  
  int[] doExecuteBatch() throws SQLException {
    
    int[] updateCount = EMPTY_UPDATE_COUNT;
    
    if (intervalBatchCount > 0) {
      updateCount = statement.executeBatch();
      totalBatchCount += intervalBatchCount;
      intervalBatchCount = 0;
      statement.clearBatch();
    }
    
    return updateCount;
  }
  
  /**
   * Clears the buffer build by previous calls of addBatch.
   * @throws SQLException when an exception occurs during SQL execution
   */
  public void clearBatch() throws SQLException {
    
    statement.clearBatch();
    intervalBatchCount = 0;
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
