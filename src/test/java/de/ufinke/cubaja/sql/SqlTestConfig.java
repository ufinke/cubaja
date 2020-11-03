package de.ufinke.cubaja.sql;

import de.ufinke.cubaja.config.*;

public class SqlTestConfig {

  private DatabaseConfig database;

  public SqlTestConfig() {

  }

  public DatabaseConfig getDatabase() {

    return database;
  }

  @Mandatory
  public void setDatabase(DatabaseConfig database) {

    this.database = database;
  }
}
