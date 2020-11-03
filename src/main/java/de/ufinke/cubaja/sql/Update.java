// Copyright (c) 2006 - 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Wrapper for <tt>insert</tt>, <tt>update</tt> or <tt>delete</tt> statements.
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
   * Calls the <tt>PreparedStatement</tt>'s <tt>executeUpdate</tt> method.
   * @return number of concerned rows.
   * @throws SQLException
   */
  public int executeUpdate() throws SQLException {
    
    return statement.executeUpdate();
  }
  
  /**
   * Adds a row to a bulk operation.
   * <p>
   * Calls the
   * <tt>PreparedStatement</tt>'s <tt>addBatch</tt> method.
   * If the configuration's <tt>batchSize</tt> value
   * has been reached, this method  
   * calls the <tt>PreparedStatement</tt>'s <tt>executeBatch</tt> method
   * automatically.
   * <p>
   * If this <tt>Update</tt> is a member of a {@link BatchGroup},
   * the result will always be an empty array.
   * @return array of number of concerned rows (result from <tt>executeBatch</tt>)
   * @throws SQLException
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
   * Writes the rows supplied by <tt>addBatch</tt> to the database.
   * Calls the <tt>PreparedStatement</tt>'s <tt>executeBatch</tt> method.
   * <p>
   * If this <tt>Update</tt> is a member of a {@link BatchGroup},
   * the result will always be an empty array.
   * @return array of number of concerned rows (not including the result of intermediate calls triggered automatically by <tt>addBatch</tt>)
   * @throws SQLException
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
   * @throws SQLException
   */
  public void clearBatch() throws SQLException {
    
    statement.clearBatch();
    intervalBatchCount = 0;
  }
  
  /**
   * Returns the total number of <tt>addBatch</tt> calls.
   * After a direct call to <tt>executeBatch</tt> the counter
   * is reset on the first subsequent call to <tt>addBatch</tt>.
   * @return number of requests
   */
  public int getBatchCount() {
    
    return totalBatchCount + intervalBatchCount;
  }
}
