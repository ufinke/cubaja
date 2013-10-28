// Copyright (c) 2013, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import de.ufinke.cubaja.util.Text;

public class SequenceConfig {

  static Text text = Text.getPackageInstance(SequenceConfig.class);

  private DatabaseConfig database;
  private String tableName;
  private String seqName;
  private int blockSize;
  private boolean log;

  public SequenceConfig() {

  }

  public SequenceConfig clone() {

    SequenceConfig clone = new SequenceConfig();

    clone.database = database.clone();
    clone.tableName = tableName;
    clone.seqName = seqName;
    clone.blockSize = blockSize;
    clone.log = log;

    return clone;
  }

  public DatabaseConfig getDatabase() throws Exception {

    if (database == null) {
      throw new SequenceException(text.get("sequenceNoDatabase"));
    }

    return database;
  }

  public void setDatabase(DatabaseConfig database) {

    this.database = database;
  }

  public String getTableName() throws Exception {

    if (tableName == null) {
      throw new SequenceException(text.get("sequenceNoTableName"));
    }

    return tableName;
  }

  public void setTableName(String tableName) {

    this.tableName = tableName;
  }

  public String getSeqName() throws Exception {

    if (seqName == null) {
      throw new SequenceException(text.get("sequenceNoSeqName"));
    }

    return seqName;
  }

  public void setSeqName(String seqName) {

    this.seqName = seqName;
  }

  public int getBlockSize() {

    if (blockSize == 0) {
      blockSize = 10000;
    }

    return blockSize;
  }

  public void setBlockSize(int blockSize) {

    this.blockSize = blockSize;
  }

  public boolean isLog() {

    return log;
  }

  public void setLog(boolean log) {

    this.log = log;
  }
}
