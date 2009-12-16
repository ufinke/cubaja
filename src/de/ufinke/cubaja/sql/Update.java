// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Update extends PreparedSql {

  static private final int[] EMPTY_UPDATE_COUNT = new int[0];
  
  private int batchSize;
  private int intervalBatchCount;
  private int totalBatchCount;
  
  Update(PreparedStatement statement, Sql sql, DatabaseConfig config) {
    
    super(statement, sql, config);
    
    batchSize = config.getBatchSize();
  }
  
  public int executeUpdate() throws SQLException {
    
    return statement.executeUpdate();
  }
  
  public int[] addBatch() throws SQLException {

    int[] updateCount = EMPTY_UPDATE_COUNT;
    
    if (intervalBatchCount == batchSize) {
      updateCount = executeBatch();
    }
    intervalBatchCount++;
    
    statement.addBatch();
    
    return updateCount;
  }
  
  public int[] executeBatch() throws SQLException {
    
    int[] updateCount = EMPTY_UPDATE_COUNT;
    
    if (intervalBatchCount > 0) {
      updateCount = statement.executeBatch();      
      totalBatchCount += intervalBatchCount;
      intervalBatchCount = 0;
    }
    
    return updateCount;
  }
  
  public int getBatchCount() {
    
    return totalBatchCount + intervalBatchCount;
  }
}
