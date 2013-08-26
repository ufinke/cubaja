// Copyright (c) 2013, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.ResultSetMetaData;
import de.ufinke.cubaja.util.Text;

public class Sequence {

  static private Text text = Text.getPackageInstance(Sequence.class);
  
  private SequenceConfig config;
  
  private Database database;
  
  private Query select;
  private Update lock;
  private Update delete;
  private Update update;
  private Update insert;
  
  private int selectFirstValuePos;
  private int selectLastValuePos;
  private int deleteOldValuePos;
  private int updateOldValuePos;
  private int updateNewValuePos;
  private int insertFirstValuePos;
  private int insertLastValuePos;
  
  private long currentValue;
  private long currentLimit;
  private long maxValue;
  
  public Sequence(SequenceConfig config) throws Exception {

    this.config = config.clone();
    
    open();
  }
  
  private void open() throws Exception {
    
    database = new Database(config.getDatabase());
    
    Sql sqlSelect = new Sql(getClass(), "sequence_select");
    sqlSelect.resolve("tableName", config.getTableName());
    sqlSelect.resolve("seqName", config.getSeqName());
    select = database.createQuery(sqlSelect);
    selectFirstValuePos = select.getColumnPosition("first_value");
    selectLastValuePos = select.getColumnPosition("last_value");
    
    Sql sqlLock = new Sql(getClass(), "sequence_lock");
    sqlLock.resolve("tableName", config.getTableName());
    sqlLock.resolve("seqName", config.getSeqName());
    lock = database.createUpdate(sqlLock);
    
    Sql sqlDelete = new Sql(getClass(), "sequence_delete");
    sqlDelete.resolve("tableName", config.getTableName());
    sqlDelete.resolve("seqName", config.getSeqName());
    delete = database.createUpdate(sqlDelete);
    deleteOldValuePos = delete.getVariablePositions("old_value")[0];
    
    Sql sqlUpdate = new Sql(getClass(), "sequence_update");
    sqlUpdate.resolve("tableName", config.getTableName());
    sqlUpdate.resolve("seqName", config.getSeqName());
    update = database.createUpdate(sqlUpdate);
    updateOldValuePos = update.getVariablePositions("old_value")[0];
    updateOldValuePos = update.getVariablePositions("old_value")[0];
    
    Sql sqlInsert = new Sql(getClass(), "sequence_insert");
    sqlInsert.resolve("tableName", config.getTableName());
    sqlInsert.resolve("seqName", config.getSeqName());
    insert = database.createUpdate(sqlInsert);
    insertFirstValuePos = insert.getVariablePositions("first_value")[0];
    insertLastValuePos = insert.getVariablePositions("last_value")[0];
    
    ResultSetMetaData meta = select.getMetaData();
    int precision = Math.min(18, meta.getPrecision(insertLastValuePos));
    maxValue = 1;
    for (int i = 0; i < precision; i++) {
      maxValue *= 10;
    }
    maxValue--;
    
    execLock('o');
    execSelect();
    database.commit();
  }
  
  public void close() throws Exception {
    
    execLock('c');
    if (currentValue < currentLimit) {
      execInsert(currentValue, currentLimit);
    }
    database.commit();
    
    select.close();
    lock.close();
    delete.close();
    update.close();
    insert.close();
    
    database.close();
  }
  
  public long nextValue() throws Exception {

    if (currentValue == currentLimit) {
      execLock('n');
      execSelect();
      database.commit();
    }
    
    return ++currentValue;
  }
  
  private void execLock(char flag) throws Exception {
    
    lock.setChar("lock_flag", flag);
    lock.executeUpdate();
  }
  
  private void execSelect() throws Exception {
    
    if (select.nextRow()) {
      currentValue = select.readLong(selectFirstValuePos);
      currentLimit = select.readLong(selectLastValuePos);
      select.closeResultSet();
      if (currentValue == currentLimit) {
        throw new SequenceException(text.get("sequenceExhausted", Long.valueOf(maxValue), config.getSeqName()));
      } else if (currentLimit > currentValue + config.getBlockSize()) {
        currentLimit = currentValue + config.getBlockSize();
        execUpdate(currentValue, currentLimit);
      } else if (currentLimit == maxValue) {
        execUpdate(currentValue, currentLimit);
      } else {
        execDelete(currentValue);
      }
    } else {
      currentValue = 0;
      currentLimit = config.getBlockSize();
      execInsert(currentLimit, maxValue);
    }
  }
  
  private void execDelete(long oldValue) throws Exception {
    
    delete.setLong(deleteOldValuePos, oldValue);
    delete.executeUpdate();
  }
  
  private void execUpdate(long oldValue, long newValue) throws Exception {
    
    update.setLong(updateOldValuePos, oldValue);
    update.setLong(updateNewValuePos, newValue);
    update.executeUpdate();
  }
  
  private void execInsert(long firstValue, long lastValue) throws Exception {
    
    insert.setLong(insertFirstValuePos, firstValue);
    insert.setLong(insertLastValuePos, lastValue);
    insert.executeUpdate();
  }
}
