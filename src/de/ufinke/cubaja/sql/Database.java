// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.*;
import org.apache.commons.logging.*;
import de.ufinke.cubaja.util.*;

public class Database {

  static private int id = 0;
  
  static private synchronized int getId() {
    
    return ++id;
  }
  
  static private final Text text = new Text(Database.class);
  
  private Connection connection;
  private Integer myId;
  private Log logger;
  
  public Database(Connection connection) throws SQLException {

    this(connection, new DatabaseConfig());
  }
  
  public Database(DatabaseConfig config) throws SQLException {
    
    this(config.createConnection(), config);
  }
  
  public Database(Connection connection, DatabaseConfig config) throws SQLException {
    
    if (config.isLog()) {
    
      myId = getId();
      logger = LogFactory.getLog(Database.class);
      
      String url = connection.getMetaData().getURL();
      String user = connection.getMetaData().getUserName();
      if (user == null) {
        logger.debug(text.get("connectedNoUser", myId, url));
      } else {
        logger.debug(text.get("connectedUser", myId, url, user));
      }
    }
    
    this.connection = connection;
    
    connection.setAutoCommit(config.isAutoCommit());
    
    if (config.getExecute() != null) {
      execute(config.getExecute());
    }
  }
  
  void execute(String sql, int... acceptedSqlCodes) throws SQLException {

    if (logger != null) {
      logger.debug(text.get("execute", myId, sql));
    }
    
    try {
      Statement statement = connection.createStatement();
      statement.execute(sql);
    } catch (SQLException e) {
      int sqlCode = e.getErrorCode();
      for (int i = 0; i < acceptedSqlCodes.length; i++) {
        if (acceptedSqlCodes[i] == sqlCode) {
          return;
        }
      }
      throw e;
    }
  }
  
  PreparedStatement prepareStatement(String sql) throws SQLException {
    
    if (logger != null) {
      logger.debug(text.get("prepare", myId, sql));
    }
    
    return connection.prepareStatement(sql);
  }
  
  public Sql createSql() throws SQLException {
    
    return new Sql(this);
  }
  
  public void commit() throws SQLException {
    
    if (logger != null) {
      logger.debug(text.get("commit", myId));
    }
    
    connection.commit();
  }
  
  public void rollback() throws SQLException {
    
    if (logger != null) {
      logger.debug(text.get("rollback", myId));
    }
    
    connection.rollback();
  }
  
  public void close() throws SQLException {

    if (logger != null) {
      logger.debug(text.get("close", myId));
    }
    
    connection.close();
    connection = null;
  }
}
