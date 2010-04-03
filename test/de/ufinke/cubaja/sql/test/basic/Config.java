package de.ufinke.cubaja.sql.test.basic;

import de.ufinke.cubaja.config.*;
import de.ufinke.cubaja.sql.*;

public class Config {

  private DatabaseConfig database;

  public Config() {

  }

  public DatabaseConfig getDatabase() {

    return database;
  }

  @Mandatory
  public void setDatabase(DatabaseConfig database) {

    this.database = database;
  }
}
