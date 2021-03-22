// Copyright (c) 2006 - 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import static de.ufinke.cubaja.sql.DatabaseEventType.CLOSE;
import static de.ufinke.cubaja.sql.DatabaseEventType.COMMIT;
import static de.ufinke.cubaja.sql.DatabaseEventType.EXECUTE;
import static de.ufinke.cubaja.sql.DatabaseEventType.PREPARE_QUERY;
import static de.ufinke.cubaja.sql.DatabaseEventType.PREPARE_UPDATE;
import static de.ufinke.cubaja.sql.DatabaseEventType.REGISTER;
import static de.ufinke.cubaja.sql.DatabaseEventType.ROLLBACK;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.util.Text;

/**
 * Wrapper for a database connection.
 * <p>
 * If not specified otherwise by configuration,
 * autocommit is <code>false</code>.
 * This is different from default JDBC behaviour.
 * <p>
 * If <code>log='true'</code> is specified 
 * in the configuration, statements will be logged
 * using the Apache CommonsLogging framework.
 * In the log message, each <code>Database</code>
 * instance has a unique id number.
 * @author Uwe Finke
 */
public class Database implements DatabaseEventListener {

  static private volatile int id = 0;
  
  static private synchronized Integer getId() {
    
    return Integer.valueOf(++id);
  }
  
  static private final Text text = Text.getPackageInstance(Database.class);
  
  private Connection connection;
  private DatabaseConfig config;
  private Integer myId;
  private String url;
  private String user;
  private List<DatabaseEventListener> eventListenerList;
  private Log logger;
  
  /**
   * Uses existing connection with default configuration attributes.
   * @param connection JDBC connection
   * @throws SQLException when an exception occurs during SQL execution
   */
  public Database(Connection connection) throws SQLException {

    this(connection, new DatabaseConfig());
  }
  
  /**
   * Connects to a database using configuration attributes.
   * @param config configuration
   * @throws SQLException  when an exception occurs during SQL execution
   */
  public Database(DatabaseConfig config) throws SQLException {
    
    this(config.createConnection(), config);
  }
  
  /**
   * Uses existing connection with specific configuration attributes.
   * @param connection JDBC connection
   * @param config configuration
   * @throws SQLException when an exception occurs during SQL execution
   */
  public Database(Connection connection, DatabaseConfig config) throws SQLException {
        
    eventListenerList = new ArrayList<DatabaseEventListener>();
    
    url = connection.getMetaData().getURL();
    user = connection.getMetaData().getUserName();
    myId = getId();
    
    this.connection = connection;
    this.config = config;
    
    connection.setAutoCommit(config.isAutoCommit());
    if (config.getTransactionIsolation() != null) {
      connection.setTransactionIsolation(config.getTransactionIsolation().getLevel());
    }
    
    if (config.isLog()) {
      logger = LogFactory.getLog(Database.class);
      addEventListener(this);
    }
        
    if (config.getExecute() != null) {
      execute(config.getExecute());
    }
  }
  
  /**
   * Adds a listener.
   * A listener will be notified about database actions.
   * The actions are the call of this method (a registration event with connection data will be fired), 
   * commit, rollback, close, execute (for every contained statement)
   * and prepare of query and update statements.  
   * @param listener event listener
   * @throws SQLException when an exception occurs during SQL execution
   */
  public void addEventListener(DatabaseEventListener listener) throws SQLException {
    
    DatabaseEvent event = new DatabaseEvent();
    event.setType(REGISTER);
    event.setDatabaseId(myId);
    event.setConnection(connection);
    event.setText((user == null) ? text.get("connectedNoUser", myId, url) : text.get("connectedUser", myId, url, user));
    
    fireEvent(listener, event);
    
    eventListenerList.add(listener);
  }
  
  /**
   * Returns the underlaying <code>connection</code> instance.
   * @return JDBC connection
   */
  public Connection getConnection() {
    
    return connection;
  }

  /**
   * Executes SQL provided as string immediately.
   * <p>
   * There may be more than one SQL statement; each
   * statement separated by semicolon.
   * <p>
   * You may optionally specify any number of SQL codes which are expected
   * and should not throw an <code>SQLException</code>. This is
   * useful e.g. for <code>drop</code> statements.
   * The SQL codes are vendor specific. 
   * @param sql SQL statement 
   * @param acceptedSqlCodes SQL codes which are not treated as errors
   * @throws SQLException when an exception occurs during SQL execution
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
   * The file names extension must be <code>sql</code> (lower case).
   * The <code>sqlResource</code> parameter contains only the
   * plain file name without extension and without path.
   * <p>
   * There may be more than one SQL statement; each
   * statement separated by semicolon.
   * <p>
   * You may optionally specify any number of SQL codes which are expected
   * and should not throw an <code>SQLException</code>. This is
   * useful e.g. for <code>drop</code> statements.
   * The SQL codes are vendor specific. 
   * @param packageClass class which is located in the same package as the resource file 
   * @param sqlResource resource file with SQL statement in it
   * @param acceptedSqlCodes SQL codes which are not treated as errors
   * @throws SQLException when an exception occurs during SQL execution
   * @throws IOException when the resource could not be loaded
   */
  public void execute(Class<?> packageClass, String sqlResource, int... acceptedSqlCodes) throws SQLException, IOException {
    
    execute(new Sql(packageClass, sqlResource), acceptedSqlCodes);
  }
  
  /**
   * Executes SQL provided as <code>Sql</code> instance immediately.
   * <p>
   * There may be more than one SQL statement; each
   * statement separated by semicolon.
   * <p>
   * You may optionally specify any number of SQL codes which are expected
   * and should not throw an <code>SQLException</code>. This is
   * useful e.g. for <code>drop</code> statements.
   * The SQL codes are vendor specific. 
   * @param sql SQL instance with interpreted SQL statement
   * @param acceptedSqlCodes SQL codes which are not treated as errors
   * @throws SQLException when an exception occurs during SQL execution
   */
  public void execute(Sql sql, int... acceptedSqlCodes) throws SQLException {

    if (sql.hasVariables()) {
      throw new SQLException(text.get("execVariables"));
    }
    
    Statement statement = connection.createStatement();

    for (String stm : sql.getStatements()) {
      
      stm = config.getExecFilter().filterExecStatement(stm);
      
      if (eventListenerList.size() > 0) {
        DatabaseEvent event = new DatabaseEvent();
        event.setType(EXECUTE);
        event.setDatabaseId(myId);
        event.setStatement(stm);
        event.setText(text.get("execute", myId, stm));
        fireEvent(event);
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
   * Creates a <code>Query</code> instance with SQL provided as string.
   * @param sql SQL statement
   * @return Query
   * @throws SQLException when an exception occurs during SQL execution
   */
  public Query createQuery(String sql) throws SQLException {
    
    return createQuery(new Sql(sql));
  }
  
  /**
   * Creates a <code>Query</code> instance with SQL provided as resource.
   * <p>
   * The SQL must be written in a separate file within a java source package
   * (usually the package where the class which uses the SQL belongs to).
   * We have to specify a class within that package as parameter. 
   * This may be any class, but usually it will be the class which uses
   * the SQL.
   * The file names extension must be <code>sql</code> (lower case).
   * The <code>sqlResource</code> parameter contains only the
   * plain file name without extension and without path.
   * @param packageClass class which is located in the same package as the resource file 
   * @param sqlResource resource file with SQL statement in it
   * @return Query
   * @throws SQLException when an exception occurs during SQL execution
   * @throws IOException when the resource could not be loaded
   */
  public Query createQuery(Class<?> packageClass, String sqlResource) throws SQLException, IOException {
    
    return createQuery(new Sql(packageClass, sqlResource));
  }
  
  /**
   * Creates a <code>Query</code> instance with SQL provided as <code>Sql</code> object.
   * @param sql SQL instance with interpreted SQL statement
   * @return Query
   * @throws SQLException when an exception occurs during SQL execution
   */
  public Query createQuery(Sql sql) throws SQLException {

    String stm = sql.getSingleStatement();
    
    if (eventListenerList.size() > 0) {
      DatabaseEvent event = new DatabaseEvent();
      event.setType(PREPARE_QUERY);
      event.setDatabaseId(myId);
      event.setStatement(stm);
      event.setText(text.get("prepare", myId, stm));
      fireEvent(event);
    }
    
    PreparedStatement ps = connection.prepareStatement(stm);
    ps.setFetchSize(config.getFetchSize());
    return new Query(ps, sql, config);
  }
  
  /**
   * Returns a single result object from a query.
   * @param <D> type or super type of the result object
   * @param sql SQL statement
   * @param clazz Class of result object
   * @return result object
   * @throws SQLException when an exception occurs during SQL execution
   */
  public <D> D select(String sql, Class<? extends D> clazz) throws SQLException {

    return select(new Sql(sql), clazz);
  }
 
  /**
   * Returns a single result object from a query.
   * @param <D> type or super type of the result object
   * @param sql SQL instance with interpreted SQL statement
   * @param clazz Class of result object
   * @return result object
   * @throws SQLException when an exception occurs during SQL execution
   */
  public <D> D select(Sql sql, Class<? extends D> clazz) throws SQLException {
   
    Query query = createQuery(sql);
    D result = query.select(clazz);
    query.close();
    return result;
  }
  
  /**
   * Creates an <code>Update</code> instance with SQL provided as string.
   * The SQL statement may be either <code>insert</code>, <code>update</code>
   * or <code>delete</code>.
   * @param sql SQL statement
   * @return prepared DDL statement
   * @throws SQLException when an exception occurs during SQL execution
   */
  public Update createUpdate(String sql) throws SQLException {
    
    return createUpdate(new Sql(sql));
  }
  
  /**
   * Creates an <code>Update</code> instance with SQL provided as resource.
   * The SQL statement may be either <code>insert</code>, <code>update</code>
   * or <code>delete</code>.
   * <p>
   * The SQL must be written in a separate file within a java source package
   * (usually the package where the class which uses the SQL belongs to).
   * We have to specify a class within that package as parameter. 
   * This may be any class, but usually it will be the class which uses
   * the SQL.
   * The file names extension must be <code>sql</code> (lower case).
   * The <code>sqlResource</code> parameter contains only the
   * plain file name without extension and without path.
   * @param packageClass class which is located in the same package as the resource file 
   * @param sqlResource resource file with SQL statement in it
   * @return prepared DDL statement
   * @throws SQLException when an exception occurs during SQL execution
   * @throws IOException when the resource could not be loaded
   */
  public Update createUpdate(Class<?> packageClass, String sqlResource) throws SQLException, IOException {
    
    return createUpdate(new Sql(packageClass, sqlResource));
  }
  
  /**
   * Creates an <code>Update</code> instance with SQL provided as <code>Sql</code> object.
   * @param sql SQL instance with interpreted SQL statement
   * @return prepared DDL statement
   * @throws SQLException when an exception occurs during SQL execution
   */
  public Update createUpdate(Sql sql) throws SQLException {

    String stm = sql.getSingleStatement();
    
    if (eventListenerList.size() > 0) {
      DatabaseEvent event = new DatabaseEvent();
      event.setType(PREPARE_UPDATE);
      event.setDatabaseId(myId);
      event.setStatement(stm);
      event.setText(text.get("prepare", myId, stm));
      fireEvent(event);
    }
    
    return new Update(connection.prepareStatement(stm), sql, config);
  }
  
  /**
   * Executes a commit.
   * @throws SQLException when an exception occurs during SQL execution
   */
  public void commit() throws SQLException {
    
    if (eventListenerList.size() > 0) {
      DatabaseEvent event = new DatabaseEvent();
      event.setType(COMMIT);
      event.setDatabaseId(myId);
      event.setText(text.get("commit", myId));
      fireEvent(event);
    }
    
    connection.commit();
  }
  
  /**
   * Exceutes a rollback.
   * @throws SQLException when an exception occurs during SQL execution
   */
  public void rollback() throws SQLException {
    
    if (eventListenerList.size() > 0) {
      DatabaseEvent event = new DatabaseEvent();
      event.setType(ROLLBACK);
      event.setDatabaseId(myId);
      event.setText(text.get("rollback", myId));
      fireEvent(event);
    }
    
    connection.rollback();
  }
  
  /**
   * Closes the connection.
   * @throws SQLException when an exception occurs during SQL execution
   */
  public void close() throws SQLException {

    if (eventListenerList.size() > 0) {
      DatabaseEvent event = new DatabaseEvent();
      event.setType(CLOSE);
      event.setDatabaseId(myId);
      event.setText(text.get("close", myId));
      fireEvent(event);
    }
    
    connection.close();
    connection = null;
  }
  
  private void fireEvent(DatabaseEvent event) throws SQLException {
    
    for (DatabaseEventListener listener : eventListenerList) {
      fireEvent(listener, event);
    }
  }
  
  private void fireEvent(DatabaseEventListener listener, DatabaseEvent event) throws SQLException {
    
    try {
      event.setConnection(connection);
      listener.handleDatabaseEvent(event);
    } catch (Throwable t) {
      SQLException e = new SQLException(text.get("event", t.getClass().getName()));
      e.initCause(t);
      throw e;
    }
  }
  
  public void handleDatabaseEvent(DatabaseEvent event) {
    
    logger.debug(event.getText());
  }
}
