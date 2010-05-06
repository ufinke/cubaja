// Copyright (c) 2006 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.util.Text;

/**
 * Wrapper for a database connection.
 * <p>
 * If not specified otherwise by configuration,
 * autocommit is <tt>false</tt>.
 * This is different from default JDBC behaviour.
 * <p>
 * If <tt>log='true'</tt> is specified 
 * in the configuration, statements will be logged
 * using the Apache CommonsLogging framework.
 * In the log message, each <tt>Database</tt>
 * instance has a unique id number.
 * @author Uwe Finke
 */
public class Database {

  static private volatile int id = 0;
  
  static private synchronized int getId() {
    
    return ++id;
  }
  
  static private final Text text = new Text(Database.class);
  
  private Connection connection;
  private DatabaseConfig config;
  private Integer myId;
  private Log logger;
  
  /**
   * Uses existing connection with default configuration attributes.
   * @param connection
   * @throws SQLException
   */
  public Database(Connection connection) throws SQLException {

    this(connection, new DatabaseConfig());
  }
  
  /**
   * Connects to a database using configuration attributes.
   * @param config
   * @throws SQLException
   */
  public Database(DatabaseConfig config) throws SQLException {
    
    this(config.createConnection(), config);
  }
  
  /**
   * Uses existing connection with specific configuration attributes.
   * @param connection
   * @param config
   * @throws SQLException
   */
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
    this.config = config;
    
    connection.setAutoCommit(config.isAutoCommit());
    
    if (config.getExecute() != null) {
      execute(config.getExecute());
    }
  }

  /**
   * Executes SQL provided as string immediately.
   * <p>
   * There may be more than one SQL statement; each
   * statement separated by semicolon.
   * <p>
   * You may optionally specify any number of SQL codes which are expected
   * and should not throw an <tt>SQLException</tt>. This is
   * useful e.g. for <tt>drop</tt> statements.
   * The SQL codes are vendor specific. 
   * @param sql
   * @param acceptedSqlCodes
   * @throws SQLException
   */
  public void execute(String sql, int... acceptedSqlCodes) throws SQLException {
  
    execute(new Sql(sql), acceptedSqlCodes);
  }
  
  /**
   * Executes SQL provided as resource immediately.
   * <p>
   * The SQL must be written in a separate file within a java source package
   * (usually the package where the class which uses the SQL belongs to).
   * We have to specify a class within that package as parameter. 
   * This may be any class, but usually it will be the class which uses
   * the SQL.
   * The file names extension must be <tt>sql</tt> (lower case).
   * The <tt>sqlResource</tt> parameter contains only the
   * plain file name without extension and without path.
   * <p>
   * There may be more than one SQL statement; each
   * statement separated by semicolon.
   * <p>
   * You may optionally specify any number of SQL codes which are expected
   * and should not throw an <tt>SQLException</tt>. This is
   * useful e.g. for <tt>drop</tt> statements.
   * The SQL codes are vendor specific. 
   * @param packageClass
   * @param sqlResource
   * @param acceptedSqlCodes
   * @throws SQLException
   * @throws IOException
   */
  public void execute(Class<?> packageClass, String sqlResource, int... acceptedSqlCodes) throws SQLException, IOException {
    
    execute(new Sql(packageClass, sqlResource), acceptedSqlCodes);
  }
  
  /**
   * Executes SQL provided as <tt>Sql</tt> instance immediately.
   * <p>
   * There may be more than one SQL statement; each
   * statement separated by semicolon.
   * <p>
   * You may optionally specify any number of SQL codes which are expected
   * and should not throw an <tt>SQLException</tt>. This is
   * useful e.g. for <tt>drop</tt> statements.
   * The SQL codes are vendor specific. 
   * @param sql
   * @param acceptedSqlCodes
   * @throws SQLException
   */
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
        boolean accepted = false;
        int i = 0;
        while ((! accepted) && (i < acceptedSqlCodes.length)) {
          accepted = (acceptedSqlCodes[i] == sqlCode);
          i++;
        }
        if (! accepted) {
          try {
            statement.close();
          } catch (SQLException ignore) {
          }
          throw e;
        }
      }
    }
    
    statement.close();
  }
  
  /**
   * Creates a <tt>Query</tt> instance with SQL provided as string.
   * @param sql
   * @return Query
   * @throws SQLException
   */
  public Query createQuery(String sql) throws SQLException {
    
    return createQuery(new Sql(sql));
  }
  
  /**
   * Creates a <tt>Query</tt> instance with SQL provided as resource.
   * <p>
   * The SQL must be written in a separate file within a java source package
   * (usually the package where the class which uses the SQL belongs to).
   * We have to specify a class within that package as parameter. 
   * This may be any class, but usually it will be the class which uses
   * the SQL.
   * The file names extension must be <tt>sql</tt> (lower case).
   * The <tt>sqlResource</tt> parameter contains only the
   * plain file name without extension and without path.
   * @param packageClass
   * @param sqlResource
   * @return Query
   * @throws SQLException
   * @throws IOException
   */
  public Query createQuery(Class<?> packageClass, String sqlResource) throws SQLException, IOException {
    
    return createQuery(new Sql(packageClass, sqlResource));
  }
  
  /**
   * Creates a <tt>Query</tt> instance with SQL provided as <tt>Sql</tt> object.
   * @param sql
   * @return Query
   * @throws SQLException
   */
  public Query createQuery(Sql sql) throws SQLException {

    String stm = sql.getSingleStatement();
    
    if (logger != null) {
      logger.debug(text.get("prepare", myId, stm));
    }
    
    PreparedStatement ps = connection.prepareStatement(stm);
    ps.setFetchSize(config.getFetchSize());
    return new Query(ps, sql, config);
  }
  
  /**
   * Returns a single result object from a query.
   * @param <D>
   * @param sql
   * @param clazz Class of result object
   * @return result object
   * @throws SQLException
   */
  public <D> D select(String sql, Class<? extends D> clazz) throws SQLException {
    
    return createQuery(sql).select(clazz);
  }
  
  /**
   * Creates an <tt>Update</tt> instance with SQL provided as string.
   * The SQL statement may be either <tt>insert</tt>, <tt>update</tt>
   * or <tt>delete</tt>.
   * @param sql
   * @return Update
   * @throws SQLException
   */
  public Update createUpdate(String sql) throws SQLException {
    
    return createUpdate(new Sql(sql));
  }
  
  /**
   * Creates an <tt>Update</tt> instance with SQL provided as resource.
   * The SQL statement may be either <tt>insert</tt>, <tt>update</tt>
   * or <tt>delete</tt>.
   * <p>
   * The SQL must be written in a separate file within a java source package
   * (usually the package where the class which uses the SQL belongs to).
   * We have to specify a class within that package as parameter. 
   * This may be any class, but usually it will be the class which uses
   * the SQL.
   * The file names extension must be <tt>sql</tt> (lower case).
   * The <tt>sqlResource</tt> parameter contains only the
   * plain file name without extension and without path.
   * @param packageClass
   * @param sqlResource
   * @return Update
   * @throws SQLException
   * @throws IOException
   */
  public Update createUpdate(Class<?> packageClass, String sqlResource) throws SQLException, IOException {
    
    return createUpdate(new Sql(packageClass, sqlResource));
  }
  
  /**
   * Creates an <tt>Update</tt> instance with SQL provided as <tt>Sql</tt> object.
   * @param sql
   * @return Update
   * @throws SQLException
   */
  public Update createUpdate(Sql sql) throws SQLException {

    String stm = sql.getSingleStatement();
    
    if (logger != null) {
      logger.debug(text.get("prepare", myId, stm));
    }
    
    return new Update(connection.prepareStatement(stm), sql, config);
  }
  
  /**
   * Executes a commit.
   * @throws SQLException
   */
  public void commit() throws SQLException {
    
    if (logger != null) {
      logger.debug(text.get("commit", myId));
    }
    
    connection.commit();
  }
  
  /**
   * Exceutes a rollback.
   * @throws SQLException
   */
  public void rollback() throws SQLException {
    
    if (logger != null) {
      logger.debug(text.get("rollback", myId));
    }
    
    connection.rollback();
  }
  
  /**
   * Closes the connection.
   * @throws SQLException
   */
  public void close() throws SQLException {

    if (logger != null) {
      logger.debug(text.get("close", myId));
    }
    
    connection.close();
    connection = null;
  }
}
