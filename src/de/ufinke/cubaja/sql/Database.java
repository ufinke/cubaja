// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.util.Text;

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
  
  public void execute(String sql, int... acceptedSqlCodes) throws SQLException {
  
    execute(new Sql(sql), acceptedSqlCodes);
  }
  
  public void execute(Reader reader, int... acceptedSqlCodes) throws IOException, SQLException {
    
    execute(new Sql(reader));
  }
  
  public void execute(Class<?> packageClass, String sqlResource, int... acceptedSqlCodes) throws IOException, SQLException {
    
    execute(new Sql(packageClass, sqlResource));
  }
  
  public void execute(Sql sql, int... acceptedSqlCodes) throws SQLException {

    if (sql.hasVariables()) {
      throw new SQLException(text.get("execVariables"));
    }
    
    Statement statement = connection.createStatement();

    for (String stm : sql.getStatements()) {
      
      if (logger != null) {
        logger.debug(text.get("execute", myId, stm));
      }
      
      try {
        statement.execute(stm);
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
    
    statement.close();
  }
  
  public Query createQuery(String sql) throws SQLException {
    
    return createQuery(new Sql(sql));
  }
  
  public Query createQuery(Reader reader) throws IOException, SQLException {
    
    return createQuery(new Sql(reader));
  }
  
  public Query createQuery(Class<?> packageClass, String sqlResource) throws IOException, SQLException {
    
    return createQuery(new Sql(packageClass, sqlResource));
  }
  
  public Query createQuery(Sql sql) throws SQLException {

    String stm = sql.getSingleStatement();
    
    if (logger != null) {
      logger.debug(text.get("prepare", myId, stm));
    }
    
    return new Query(connection.prepareStatement(stm), sql);
  }
  
  public <D> D select(String sql, Class<? extends D> clazz) throws SQLException {
    
    return createQuery(sql).select(clazz);
  }
  
  public Update createUpdate(String sql) throws SQLException {
    
    return createUpdate(new Sql(sql));
  }
  
  public Update createUpdate(Reader reader) throws IOException, SQLException {
    
    return createUpdate(new Sql(reader));
  }
  
  public Update createUpdate(Class<?> packageClass, String sqlResource) throws IOException, SQLException {
    
    return createUpdate(new Sql(packageClass, sqlResource));
  }
  
  public Update createUpdate(Sql sql) throws SQLException {

    String stm = sql.getSingleStatement();
    
    if (logger != null) {
      logger.debug(text.get("prepare", myId, stm));
    }
    
    return new Update(connection.prepareStatement(stm), sql);
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
