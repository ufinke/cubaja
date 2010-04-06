// Copyright (c) 2006 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import de.ufinke.cubaja.config.Mandatory;
import de.ufinke.cubaja.util.Text;

/**
 * Configuration parameters needed for a database connection.
 * <p>
 * XML attributes and subelements:
 * <blockquote>
 * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
 *   <tr bgcolor="#ccccff">
 *     <th align="left">Name</th>
 *     <th align="left">Description</th>
 *     <th align="center">A/E</th>
 *     <th align="center">M</th>
 *     <th align="center">U</th>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>driver</tt></td>
 *     <td align="left" valign="top">driver class</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top">x</td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>url</tt></td>
 *     <td align="left" valign="top">URL</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top">x</td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>user</tt></td>
 *     <td align="left" valign="top">user ID</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>password</tt></td>
 *     <td align="left" valign="top">password</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>excecute</tt></td>
 *     <td align="left" valign="top">statement that should be executed immediately after a <tt>Database</tt> instance has been created (e.g. to set the default schema)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>property</tt></td>
 *     <td align="left" valign="top">any property; see <tt>DatabaseConfig.PropertyConfig</tt></td>
 *     <td align="center" valign="top">E</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top"> </td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>autoCommit</tt></td>
 *     <td align="left" valign="top">set to <tt>true</tt> when a commit should follow each single statement automatically (as native JDBC connections do); default is <tt>false</tt> (as database systems with transaction capabilities do)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>log</tt></td>
 *     <td align="left" valign="top">set to <tt>true</tt> when actions should be logged; default is <tt>false</tt></td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>fetchSize</tt></td>
 *     <td align="left" valign="top">number of result rows fetched in a single block operation; default is <tt>4095</tt> (some database systems ignore this value and set an optimum fetchSize automatically)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>batchSize</tt></td>
 *     <td align="left" valign="top">maximum number of native <tt>addBatch</tt> operations; default is <tt>8191</tt> (an <tt>Update</tt> instance calls <tt>executeBatch</tt> automatically when this limit is reached)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 * </table>
 * <tt>A/E</tt>: attribute or subelement
 * <br/>
 * <tt>M</tt>: mandatory
 * <br/>
 * <tt>U</tt>: unique
 * </blockquote>
 * </p>
 * @author Uwe Finke
 */
public class DatabaseConfig {

  static private Text text = new Text(DatabaseConfig.class);
  
  /**
   * Property sub-element of a database configuration element.
   * <p>
   * XML attributes and subelements:
   * <blockquote>
   * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
   *   <tr bgcolor="#ccccff">
   *     <th align="left">Name</th>
   *     <th align="left">Description</th>
   *     <th align="center">A/E</th>
   *     <th align="center">M</th>
   *     <th align="center">U</th>
   *     </tr>
   *   <tr bgcolor="#eeeeff">
   *     <td align="left" valign="top"><tt>name</tt></td>
   *     <td align="left" valign="top">property name</td>
   *     <td align="center" valign="top">A</td>
   *     <td align="center" valign="top">x</td>
   *     <td align="center" valign="top">x</td>
   *     </tr>
   *   <tr bgcolor="#eeeeff">
   *     <td align="left" valign="top"><tt>value</tt></td>
   *     <td align="left" valign="top">property value</td>
   *     <td align="center" valign="top">A</td>
   *     <td align="center" valign="top">x</td>
   *     <td align="center" valign="top">x</td>
   *     </tr>
   * </table>
   * <tt>A/E</tt>: attribute or subelement
   * <br/>
   * <tt>M</tt>: mandatory
   * <br/>
   * <tt>U</tt>: unique
   * </blockquote>
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
  private int fetchSize;
  private int batchSize;
  private boolean log;
  private Database database;

  /**
   * Constructor.
   */
  public DatabaseConfig() {

    properties = new Properties();
    autoCommit = false;
    fetchSize = 4095;
    batchSize = 8191;
    log = false;
  }
  
  /**
   * Signals whether activities should be log.
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
   * on <tt>debug</tt> level.
   * @param log
   */
  public void setLog(boolean log) {
  
    this.log = log;
  }

  /**
   * Returns the autoCommit flag.
   * By default, autoCommit is <tt>false</tt>.
   * @return autoCommit flag
   */
  public boolean isAutoCommit() {
  
    return autoCommit;
  }
  
  /**
   * Sets the autoCommit flag.
   * @param autoCommit
   */
  public void setAutoCommit(boolean autoCommit) {
  
    this.autoCommit = autoCommit;
  }

  /**
   * Returns the fetchSize for queries.
   * By default, this configuration class returns <tt>4096</tt>.
   * @return fetch size
   */
  public int getFetchSize() {
  
    return fetchSize;
  }
  
  /**
   * Sets the fetchSize for queries.
   * @param fetchSize
   */
  public void setFetchSize(int fetchSize) {
  
    this.fetchSize = fetchSize;
  }
  
  /**
   * Returns the batchSize for updates.
   * By default, this configuration class returns <tt>8191</tt>.
   * @return batch size
   */
  public int getBatchSize() {
    
    return batchSize;
  }
  
  /**
   * Sets th batchSize for updates.
   * @param batchSize
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
   * The statement is executed during initialization of a <tt>Database</tt> instance.
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
   * The <tt>user</tt> and <tt>password</tt> parameters
   * are also part of the properties.
   * @return properties
   */
  public Properties getProperties() {
    
    return properties;
  }

  /**
   * Creates a database connection according to the parameters.
   * @return a database connection
   * @throws SQLException
   */
  public Connection createConnection() throws SQLException {

    if (driver != null) {
      try {
        Class.forName(driver);
      } catch (ClassNotFoundException ce) {
        throw new SQLException(text.get("driverNotFound", driver));
      }
    }

    Connection connection = null; 
    try {
      connection = DriverManager.getConnection(url, properties);
    } catch (SQLException sqle) {
      String user = properties.getProperty("user");
      String message = (user == null)
                     ? text.get("connectFailedUser", url, sqle.getMessage(), user)
                     : text.get("connectFailedNoUser", url, sqle.getMessage());
      SQLException ex = new SQLException(message);
      ex.initCause(sqle);
      throw ex;
    }
    
    return connection;
  }
  
  /**
   * Returns a <tt>Database</tt> instance.
   * The instance is created on the first call to this method.
   * Subsequent calls will return the same instance.
   * @return database instance with connection
   * @throws SQLException
   */
  public Database getDatabase() throws SQLException {
    
    if (database == null) {
      database = new Database(this);
    }
    return database;
  }
  
}
