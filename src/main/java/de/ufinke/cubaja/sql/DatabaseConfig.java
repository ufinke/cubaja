// Copyright (c) 2006 - 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import de.ufinke.cubaja.config.Mandatory;
import de.ufinke.cubaja.util.Text;
import de.ufinke.cubaja.util.WarnMode;

/**
 * Configuration parameters needed for a database connection.
 * <table class="striped">
 * <caption style="text-align:left">XML attributes and subelements</caption>
 * <thead>
 * <tr>
 * <th scope="col" style="text-align:left">Name</th>
 * <th scope="col" style="text-align:left">Description</th>
 * <th scope="col" style="text-align:center">A/E</th>
 * <th scope="col" style="text-align:center">M</th>
 * <th scope="col" style="text-align:center">U</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>driver</code></td>
 * <td style="text-align:left;vertical-align:top">driver class</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>url</code></td>
 * <td style="text-align:left;vertical-align:top">URL</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>user</code></td>
 * <td style="text-align:left;vertical-align:top">user ID</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>password</code></td>
 * <td style="text-align:left;vertical-align:top">password</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>excecute</code></td>
 * <td style="text-align:left;vertical-align:top">statement that should be executed immediately after a <code>Database</code> instance has been created (e.g. to set the default schema)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>property</code></td>
 * <td style="text-align:left;vertical-align:top">any property; see <code>DatabaseConfig.PropertyConfig</code></td>
 * <td style="text-align:center;vertical-align:top">E</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>autoCommit</code></td>
 * <td style="text-align:left;vertical-align:top">set to <code>true</code> when a commit should follow each single statement automatically (as native JDBC connections do); default is <code>false</code> (as database systems with transaction capabilities do)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>transactionIsolation</code></td>
 * <td style="text-align:left;vertical-align:top">transaction isolation level; supported values are <code>read_committed</code>, <code>read_uncommitted</code>, <code>repeatable_read</code>, <code>serializable</code></td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>log</code></td>
 * <td style="text-align:left;vertical-align:top">set to <code>true</code> when actions should be logged; default is <code>false</code></td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>fetchSize</code></td>
 * <td style="text-align:left;vertical-align:top">number of result rows fetched in a single block operation; default is <code>4095</code> (some database systems ignore this value and set an optimum fetchSize automatically)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>batchSize</code></td>
 * <td style="text-align:left;vertical-align:top">maximum number of native <code>addBatch</code> operations; default is <code>4095</code> (an <code>Update</code> instance calls <code>executeBatch</code> automatically when this limit is reached)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>warnMode</code></td>
 * <td style="text-align:left;vertical-align:top">action when there is no setter for result column; valid values are <code>ignore</code>, <code>warn</code> (the default) or <code>error</code></td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * </tbody>
 * </table>
 * <p>
 * <code>A/E</code>: attribute or subelement<br>
 * <code>M</code>: mandatory<br>
 * <code>U</code>: unique
 * </p>
 * @author Uwe Finke
 */
public class DatabaseConfig {

  static private Text text = Text.getPackageInstance(DatabaseConfig.class);
  
  /**
   * <p>
   * Property sub-element of a database configuration element.
   * </p>
   * <table class="striped">
   * <caption style="text-align:left">XML attributes and subelements</caption>
   * <thead>
   * <tr>
   * <th scope="col" style="text-align:left">Name</th>
   * <th scope="col" style="text-align:left">Description</th>
   * <th scope="col" style="text-align:center">A/E</th>
   * <th scope="col" style="text-align:center">M</th>
   * <th scope="col" style="text-align:center">U</th>
   * </tr>
   * </thead>
   * <tbody>
   * <tr>
   * <td style="text-align:left;vertical-align:top"><code>name</code></td>
   * <td style="text-align:left;vertical-align:top">property name</td>
   * <td style="text-align:center;vertical-align:top">A</td>
   * <td style="text-align:center;vertical-align:top">x</td>
   * <td style="text-align:center;vertical-align:top">x</td>
   * </tr>
   * <tr>
   * <td style="text-align:left;vertical-align:top"><code>value</code></td>
   * <td style="text-align:left;vertical-align:top">property value</td>
   * <td style="text-align:center;vertical-align:top">A</td>
   * <td style="text-align:center;vertical-align:top">x</td>
   * <td style="text-align:center;vertical-align:top">x</td>
   * </tr>
   * </tbody>
   * </table>
   * <p>
   * <code>A/E</code>: attribute or subelement<br>
   * <code>M</code>: mandatory<br>
   * <code>U</code>: unique
   * </p>
   * @author Uwe Finke
   */
  static public class PropertyConfig {

    private String name;
    private String value;

    /**
     * Constructor.
     */
    public PropertyConfig() {

    }

    String getName() {

      return name;
    }

    /**
     * Sets the name.
     * @param name the property name
     */
    public void setName(String name) {

      this.name = name;
    }

    String getValue() {

      return value;
    }

    /**
     * Sets the value.
     * @param value the property value
     */
    public void setValue(String value) {

      this.value = value;
    }
  }

  private String driver;
  private String url;
  private String execute;
  private Properties properties;
  private boolean autoCommit;
  private TransactionIsolation transactionIsolation;
  private int fetchSize;
  private int batchSize;
  private boolean log;
  private WarnMode warnMode;
  private ExecFilter execFilter;

  /**
   * Constructor.
   */
  public DatabaseConfig() {

    properties = new Properties();
    autoCommit = false;
    fetchSize = 4095;
    batchSize = 4095;
    log = false;
    warnMode = WarnMode.WARN;
    execFilter = new DefaultExecFilter();
  }

  /**
   * Creates a clone of this object.
   */
  public DatabaseConfig clone() {

    DatabaseConfig clone = new DatabaseConfig();

    clone.driver = driver;
    clone.url = url;
    clone.execute = execute;
    clone.properties = (Properties) properties.clone();
    clone.autoCommit = autoCommit;
    clone.transactionIsolation = transactionIsolation;
    clone.fetchSize = fetchSize;
    clone.batchSize = batchSize;
    clone.log = log;
    clone.warnMode = warnMode;
    clone.execFilter = execFilter;

    return clone;
  }

  /**
   * Signals whether activities should be logged.
   * By default, logging is not enabled.
   * @return log flag
   */
  public boolean isLog() {
  
    return log;
  }

  /**
   * Sets the log flag.
   * If set, activities like establishing or closing a connection,
   * commit, rollback and statement creation are logged
   * on <code>debug</code> level.
   * @param log true or false
   */
  public void setLog(boolean log) {
  
    this.log = log;
  }  

  /**
   * Returns the warn mode.
   * @return warn mode
   */
  public WarnMode getWarnMode() {
  
    return warnMode;
  }

  /**
   * Sets the warn mode.
   * @param warnMode warn mode
   */
  public void setWarnMode(WarnMode warnMode) {
  
    this.warnMode = warnMode;
  }

  /**
   * Returns the autoCommit flag.
   * By default, autoCommit is <code>false</code>.
   * @return autoCommit flag
   */
  public boolean isAutoCommit() {
  
    return autoCommit;
  }
  
  /**
   * Sets the autoCommit flag.
   * @param autoCommit true or false
   */
  public void setAutoCommit(boolean autoCommit) {
  
    this.autoCommit = autoCommit;
  }

  /**
   * Returns the transaction isolation setting.
   * By default, there is no explicit setting.
   * @return transaction isolation
   */
  public TransactionIsolation getTransactionIsolation() {
  
    return transactionIsolation;
  }

  /**
   * Sets the transaction isolation level.
   * @param transactionIsolation transaction isolation characteristic
   */
  public void setTransactionIsolation(TransactionIsolation transactionIsolation) {
  
    this.transactionIsolation = transactionIsolation;
  }

  /**
   * Returns the fetchSize for queries.
   * By default, this configuration class returns <code>4096</code>.
   * @return fetch size
   */
  public int getFetchSize() {
  
    return fetchSize;
  }
  
  /**
   * Sets the fetchSize for queries.
   * @param fetchSize number of records to be transferred from database server to client program
   */
  public void setFetchSize(int fetchSize) {
  
    this.fetchSize = fetchSize;
  }
  
  /**
   * Returns the batchSize for updates.
   * By default, this configuration class returns <code>8191</code>.
   * @return batch size
   */
  public int getBatchSize() {
    
    return batchSize;
  }
  
  /**
   * Sets th batchSize for updates.
   * @param batchSize number of records to collect before executing DML statements
   */
  public void setBatchSize(int batchSize) {
    
    this.batchSize = batchSize;
  }

  /**
   * Returns the driver class name.
   * @return driver
   */
  public String getDriver() {

    return driver;
  }

  /**
   * Sets the driver class name.
   * @param driver driver class name
   */
  @Mandatory
  public void setDriver(String driver) {

    this.driver = driver;
  }

  /**
   * Returns the URL.
   * @return URL
   */
  public String getUrl() {

    return url;
  }

  /**
   * Sets the URL.
   * @param url URL
   */
  @Mandatory
  public void setUrl(String url) {

    this.url = url;
  }

  /**
   * Returns the user ID.
   * @return user id
   */
  public String getUser() {

    return properties.getProperty("user");
  }

  /**
   * Sets the user ID.
   * @param user user ID
   */
  public void setUser(String user) {

    properties.setProperty("user", user);
  }

  /**
   * Returns the password.
   * @return password
   */
  public String getPassword() {

    return properties.getProperty("password");
  }

  /**
   * Sets the password.
   * @param password password
   */
  public void setPassword(String password) {

    properties.setProperty("password", password);
  }

  /**
   * Returns the SQL to execute.
   * @return an SQL string
   */
  public String getExecute() {

    return execute;
  }

  /**
   * Sets an SQL statement.
   * The statement is executed during initialization of a <code>Database</code> instance.
   * Useful to set the current schema, for example.
   * @param execute an SQL statement
   */
  public void setExecute(String execute) {

    this.execute = execute;
  }
  
  /**
   * Adds an arbitrary property.
   * @param property a property
   */
  public void addProperty(PropertyConfig property) {
    
    properties.setProperty(property.getName(), property.getValue());
  }
  
  /**
   * Returns all properties.
   * The <code>user</code> and <code>password</code> parameters
   * are also part of the properties.
   * @return properties
   */
  public Properties getProperties() {
    
    return properties;
  }

  /**
   * Creates a database connection according to the parameters.
   * @return a database connection
   * @throws SQLException when an exception occurs during SQL execution
   */
  public Connection createConnection() throws SQLException {

    if (driver != null) {
      try {
        Class.forName(driver);
      } catch (ClassNotFoundException ce) {
        throw new SQLException(text.get("driverNotFound", driver));
      }
    }

    try {
      return DriverManager.getConnection(url, properties);
    } catch (Exception sqle) {
      String user = properties.getProperty("user");
      String message = (user == null) ? text.get("connectFailedNoUser", url, sqle.getMessage()) : text.get("connectFailedUser", url, sqle.getMessage(), user);
      SQLException ex = new SQLException(message);
      ex.initCause(sqle);
      throw ex;
    }
  }

  public ExecFilter getExecFilter() {

    return execFilter;
  }

  public void setExecFilter(ExecFilter execFilter) {

    this.execFilter = execFilter;
  }

}
