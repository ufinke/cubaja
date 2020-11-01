// Copyright (c) 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.Connection;

public class DatabaseEvent {

  private DatabaseEventType type;
  private String text;
  private Integer databaseId;
  private String statement;
  private Connection connection;

  DatabaseEvent() {

  }

  public DatabaseEventType getType() {

    return type;
  }

  public String getText() {

    return text;
  }

  public Integer getDatabaseId() {

    return databaseId;
  }

  public String getStatement() {

    return statement;
  }

  void setType(DatabaseEventType type) {

    this.type = type;
  }

  void setText(String text) {

    this.text = text;
  }

  void setDatabaseId(Integer databaseId) {

    this.databaseId = databaseId;
  }

  void setStatement(String statement) {

    this.statement = statement;
  }

  public Connection getConnection() {

    return connection;
  }

  void setConnection(Connection connection) {

    this.connection = connection;
  }

}
