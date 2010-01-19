// Copyright (c) 2006 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import de.ufinke.cubaja.io.ColumnReader;
import de.ufinke.cubaja.io.RowIterator;
import de.ufinke.cubaja.util.Text;

/**
 * Wrapper for <code>select</code> statements and result sets.
 * An instance is created by an appropriate <code>Database</code> method.
 * @author Uwe Finke
 */
public class Query extends PreparedSql implements ColumnReader {

  static private final Text text = new Text(Query.class);
  
  private ResultSet resultSet;
  private ResultSetMetaData metaData;
  private int rowCount;
  
  private ObjectFactoryGenerator generator;
  private Class<?> dataClass;
  private ObjectFactory objectFactory; 
  
  Query(PreparedStatement statement, Sql sql, DatabaseConfig config) {
  
    super(statement, sql, config);
  }
  
  private void execute() throws SQLException {
    
    closeResultSet();
    resultSet = statement.executeQuery();
    resetChanged();
    rowCount = 0;
  }
  
  private void checkExec() throws SQLException {

    if (resultSet == null || isChanged()) {
      execute();
    }
  }
  
  private void checkRow() throws SQLException {
    
    if (resultSet == null) {
      throw new SQLException(text.get("noRow"));
    }
  }
  
  /**
   * Returns the result set's meta data.
   * @return meta data
   * @throws SQLException
   */
  public ResultSetMetaData getMetaData() throws SQLException {
    
    checkExec();
    if (metaData == null) {
      metaData = resultSet.getMetaData();
    }
    return metaData;
  }
  
  /**
   * Closes the result set.
   * @throws SQLException
   */
  public void closeResultSet() throws SQLException {
    
    if (resultSet != null) {
      resultSet.close();
      resultSet = null;
      metaData = null;
    }
  }
  
  /**
   * Closes the result set and the statement.
   */
  public void close() throws SQLException {

    closeResultSet();
    
    if (statement != null) {
      statement.close();
      statement = null;
    }
  }
  
  public int getColumnCount() throws SQLException {

    checkExec();
    return getMetaData().getColumnCount();
  }
  
  public int getColumnPosition(String columnName) throws SQLException {

    checkExec();
    
    Integer position = resultSet.findColumn(columnName); 
    if (position == null) {
      throw new SQLException(text.get("columnNotFound", columnName));
    }
    return position;
  }

  public int getRowCount() {

    return rowCount;
  }

  /**
   * Fetches the next row.
   * The result set is closed automatically when the last row had been retrieved.
   */
  public boolean nextRow() throws SQLException {

    checkExec();
    
    boolean hasNext = resultSet.next();
    
    if (hasNext) {
      rowCount++;
    } else {
      closeResultSet();
    }
    
    return hasNext;
  }

  public BigDecimal readBigDecimal(String columnName) throws SQLException {

    return readBigDecimal(getColumnPosition(columnName));
  }

  public BigDecimal readBigDecimal(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getBigDecimal(columnPosition);
  }

  public BigInteger readBigInteger(String columnName) throws SQLException {

    return readBigInteger(getColumnPosition(columnName));
  }

  public BigInteger readBigInteger(int columnPosition) throws SQLException {

    return readBigDecimal(columnPosition).toBigInteger();
  }

  public boolean readBoolean(String columnName) throws SQLException {

    return readBoolean(getColumnPosition(columnName));
  }

  public boolean readBoolean(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getBoolean(columnPosition);
  }

  public Boolean readBooleanObject(String columnName) throws SQLException {

    return readBooleanObject(getColumnPosition(columnName));
  }

  public Boolean readBooleanObject(int columnPosition) throws SQLException {

    checkRow();
    boolean result = resultSet.getBoolean(columnPosition);
    return resultSet.wasNull() ? null : Boolean.valueOf(result);
  }

  public byte readByte(String columnName) throws SQLException {

    return readByte(getColumnPosition(columnName));
  }

  public byte readByte(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getByte(columnPosition);
  }

  public Byte readByteObject(String columnName) throws SQLException {

    return readByteObject(getColumnPosition(columnName));
  }

  public Byte readByteObject(int columnPosition) throws SQLException {

    checkRow();
    byte result = resultSet.getByte(columnPosition);
    return resultSet.wasNull() ? null : Byte.valueOf(result);
  }

  public char readChar(String columnName) throws SQLException {

    return readChar(getColumnPosition(columnName));
  }

  public char readChar(int columnPosition) throws SQLException {

    String s = readString(columnPosition);
    if (s == null || s.length() == 0) {
      return ' ';
    } else {
      return s.charAt(0);
    }
  }

  public Character readCharObject(String columnName) throws SQLException {

    return readCharObject(getColumnPosition(columnName));
  }

  public Character readCharObject(int columnPosition) throws SQLException {

    String s = readString(columnPosition);
    if (s == null || s.length() == 0) {
      return null;
    } else {
      return Character.valueOf(s.charAt(0));
    }
  }

  /**
   * Reads a date column identified by name as <code>java.sql.Date</code>.
   * @param columnName
   * @return Date
   * @throws SQLException
   */
  public java.sql.Date readSqlDate(String columnName) throws SQLException {

    return readSqlDate(getColumnPosition(columnName));
  }

  /**
   * Reads a date column identified by position as <code>java.sql.Date</code>.
   * @param columnPosition
   * @return Date
   * @throws SQLException
   */
  public java.sql.Date readSqlDate(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getDate(columnPosition);
  }
  
  /**
   * Reads a timestamp column identified by name as <code>Timestamp</code>.
   * @param columnName
   * @return Timestamp
   * @throws SQLException
   */
  public Timestamp readSqlTimestamp(String columnName) throws SQLException {

    return readSqlTimestamp(getColumnPosition(columnName));
  }

  /**
   * Reads a timestamp column identified by position as <code>Timestamp</code>.
   * @param columnPosition
   * @return Timestamp
   * @throws SQLException
   */
  public Timestamp readSqlTimestamp(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getTimestamp(columnPosition);
  }

  /**
   * Reads a time column identified by name as <code>Time</code>.
   * @param columnName
   * @return Time
   * @throws SQLException
   */
  public Time readSqlTime(String columnName) throws SQLException {

    return readSqlTime(getColumnPosition(columnName));
  }

  /**
   * Reads a time column identified by position as <code>Time</code>.
   * @param columnPosition
   * @return Time
   * @throws SQLException
   */
  public Time readSqlTime(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getTime(columnPosition);
  }

  /**
   * Reads a date column identified by name as <code>java.util.Date</code>.
   * @param columnName
   * @return Date
   * @throws SQLException
   */
  public java.util.Date readDate(String columnName) throws SQLException {
    
    return readDate(getColumnPosition(columnName));
  }
  
  /**
   * Reads a date column identified by position as <code>java.util.Date</code>.
   * @param columnPosition
   * @return Date
   * @throws SQLException
   */
  public java.util.Date readDate(int columnPosition) throws SQLException {
    
    java.sql.Date date = readSqlDate(columnPosition);
    return (date == null) ? null : new java.util.Date(date.getTime());
  }

  /**
   * Reads a timestamp column identified by name as <code>java.util.Date</code>.
   * @param columnName
   * @return Timestamp
   * @throws SQLException
   */
  public java.util.Date readTimestamp(String columnName) throws SQLException {
    
    return readTimestamp(getColumnPosition(columnName));
  }
  
  /**
   * Reads a timestamp column identified by position as <code>java.util.Date</code>.
   * @param columnPosition
   * @return Timestamp
   * @throws SQLException
   */
  public java.util.Date readTimestamp(int columnPosition) throws SQLException {

    Timestamp timestamp = readSqlTimestamp(columnPosition);
    return (timestamp == null) ? null : new java.util.Date(timestamp.getTime());
  }

  /**
   * Reads a time column identified by name as <code>java.util.Date</code>.
   * @param columnName
   * @return Time
   * @throws SQLException
   */
  public java.util.Date readTime(String columnName) throws SQLException {
    
    return readTime(getColumnPosition(columnName));
  }
  
  /**
   * Reads a time column identified by position as <code>java.util.Date</code>.
   * @param columnPosition
   * @return java.util.Date
   * @throws SQLException
   */
  public java.util.Date readTime(int columnPosition) throws SQLException {

    Time time = readSqlTime(columnPosition);
    return (time == null) ? null : new java.util.Date(time.getTime());
  }

  public double readDouble(String columnName) throws SQLException {

    return readDouble(getColumnPosition(columnName));
  }

  public double readDouble(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getDouble(columnPosition);
  }

  public Double readDoubleObject(String columnName) throws SQLException {

    return readDoubleObject(getColumnPosition(columnName));
  }

  public Double readDoubleObject(int columnPosition) throws SQLException {

    checkRow();
    double result = resultSet.getDouble(columnPosition);
    return resultSet.wasNull() ? null : Double.valueOf(result);
  }

  public float readFloat(String columnName) throws SQLException {

    return readFloat(getColumnPosition(columnName));
  }

  public float readFloat(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getFloat(columnPosition);
  }

  public Float readFloatObject(String columnName) throws SQLException {

    return readFloatObject(getColumnPosition(columnName));
  }

  public Float readFloatObject(int columnPosition) throws SQLException {

    checkRow();
    float result = resultSet.getFloat(columnPosition);
    return resultSet.wasNull() ? null : Float.valueOf(result);
  }

  public int readInt(String columnName) throws SQLException {

    return readInt(getColumnPosition(columnName));
  }

  public int readInt(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getInt(columnPosition);
  }

  public Integer readIntObject(String columnName) throws SQLException {

    return readIntObject(getColumnPosition(columnName));
  }

  public Integer readIntObject(int columnPosition) throws SQLException {

    checkRow();
    int result = resultSet.getInt(columnPosition);
    return resultSet.wasNull() ? null : Integer.valueOf(result);
  }

  public long readLong(String columnName) throws SQLException {

    return readLong(getColumnPosition(columnName));
  }

  public long readLong(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getLong(columnPosition);
  }

  public Long readLongObject(String columnName) throws SQLException {

    return readLongObject(getColumnPosition(columnName));
  }

  public Long readLongObject(int columnPosition) throws SQLException {

    checkRow();
    long result = resultSet.getLong(columnPosition);
    return resultSet.wasNull() ? null : Long.valueOf(result);
  }

  public short readShort(String columnName) throws SQLException {

    return readShort(getColumnPosition(columnName));
  }

  public short readShort(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getShort(columnPosition);
  }

  public Short readShortObject(String columnName) throws SQLException {

    return readShortObject(getColumnPosition(columnName));
  }

  public Short readShortObject(int columnPosition) throws SQLException {

    checkRow();
    short result = resultSet.getShort(columnPosition);
    return resultSet.wasNull() ? null : Short.valueOf(result);
  }

  public String readString(String columnName) throws SQLException {

    return readString(getColumnPosition(columnName));
  }

  public String readString(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getString(columnPosition);
  }
  
  /**
   * Reads a byte array column identified by name.
   * @param columnName
   * @return byte array
   * @throws SQLException
   */
  public byte[] readBytes(String columnName) throws SQLException {
    
    return readBytes(getColumnPosition(columnName));
  }
  
  /**
   * Reads a byte array column identified by position.
   * @param columnPosition
   * @return byte array
   * @throws SQLException
   */
  public byte[] readBytes(int columnPosition) throws SQLException {
    
    checkRow();
    return resultSet.getBytes(columnPosition);
  }

  /**
   * Reads a ASCII column identified by name. 
   * @param columnName
   * @return input stream
   * @throws SQLException
   */
  public InputStream readAsciiStream(String columnName) throws SQLException {
    
    return readAsciiStream(getColumnPosition(columnName));
  }
  
  /**
   * Reads a ASCII column identified by position.
   * @param columnPosition
   * @return input stream
   * @throws SQLException
   */
  public InputStream readAsciiStream(int columnPosition) throws SQLException {
    
    checkRow();
    return resultSet.getAsciiStream(columnPosition);
  }

  /**
   * Reads a binary column identified by name.
   * @param columnName
   * @return input stream
   * @throws SQLException
   */
  public InputStream readBinaryStream(String columnName) throws SQLException {
    
    return readBinaryStream(getColumnPosition(columnName));
  }
  
  /**
   * Reads a binary column identified by position.
   * @param columnPosition
   * @return input stream
   * @throws SQLException
   */
  public InputStream readBinaryStream(int columnPosition) throws SQLException {
    
    checkRow();
    return resultSet.getBinaryStream(columnPosition);
  }

  /**
   * Reads a character stream column identified by name.
   * @param columnName
   * @return reader
   * @throws SQLException
   */
  public Reader readCharacterStream(String columnName) throws SQLException {
    
    return readCharacterStream(getColumnPosition(columnName));
  }
  
  /**
   * Reads a character stream column identified by position.
   * @param columnPosition
   * @return reader
   * @throws SQLException
   */
  public Reader readCharacterStream(int columnPosition) throws SQLException {
    
    checkRow();
    return resultSet.getCharacterStream(columnPosition);
  }

  /**
   * Reads a BLOB column identified by name.
   * @param columnName
   * @return blob
   * @throws SQLException
   */
  public Blob readBlob(String columnName) throws SQLException {
    
    return readBlob(getColumnPosition(columnName));
  }
  
  /**
   * Reads a BLOB column identified by position.
   * @param columnPosition
   * @return blob
   * @throws SQLException
   */
  public Blob readBlob(int columnPosition) throws SQLException {
    
    checkRow();
    return resultSet.getBlob(columnPosition);
  }

  /**
   * Reads a CLOB column identified by name.
   * @param columnName
   * @return clob
   * @throws SQLException
   */
  public Clob readClob(String columnName) throws SQLException {
    
    return readClob(getColumnPosition(columnName));
  }
  
  /**
   * Reads a CLOB column identified by position.
   * @param columnPosition
   * @return clob
   * @throws SQLException
   */
  public Clob readClob(int columnPosition) throws SQLException {
    
    checkRow();
    return resultSet.getClob(columnPosition);
  }

  /**
   * Reads an array column identified by name.
   * @param columnName
   * @return array
   * @throws SQLException
   */
  public Array readArray(String columnName) throws SQLException {
    
    return readArray(getColumnPosition(columnName));
  }
  
  /**
   * Reads an array column identified by position.
   * @param columnPosition
   * @return array
   * @throws SQLException
   */
  public Array readArray(int columnPosition) throws SQLException {
    
    checkRow();
    return resultSet.getArray(columnPosition);
  }

  /**
   * Reads a Ref column identified by name.
   * @param columnName
   * @return ref
   * @throws SQLException
   */
  public Ref readRef(String columnName) throws SQLException {
    
    return readRef(getColumnPosition(columnName));
  }
  
  /**
   * Reads a Ref column identified by position.
   * @param columnPosition
   * @return ref
   * @throws SQLException
   */
  public Ref readRef(int columnPosition) throws SQLException {
    
    checkRow();
    return resultSet.getRef(columnPosition);
  }
  
  /**
   * Reads a URL column identified by name.
   * @param columnName
   * @return URL
   * @throws SQLException
   */
  public URL readURL(String columnName) throws SQLException {
    
    return readURL(getColumnPosition(columnName));
  }
  
  /**
   * Reads a URL column identified by position.
   * @param columnPosition
   * @return URL
   * @throws SQLException
   */
  public URL readURL(int columnPosition) throws SQLException {
    
    checkRow();
    return resultSet.getURL(columnPosition);
  }

  /**
   * Reads a object column identified by name.
   * @param columnName
   * @return object
   * @throws SQLException
   */
  public Object readObject(String columnName) throws SQLException {
    
    return readObject(getColumnPosition(columnName));
  }
  
  /**
   * Reads an object column identified by position.
   * @param columnPosition
   * @return object
   * @throws SQLException
   */
  public Object readObject(int columnPosition) throws SQLException {
    
    checkRow();
    return resultSet.getObject(columnPosition);
  }

  public String[] readColumns() throws SQLException {

    int colCount = getColumnCount();
    String[] columns = new String[colCount];
    
    int i = 0;
    while (i < colCount) {      
      columns[i] = readString(++i);
    }
    
    return columns;
  }

  @SuppressWarnings("unchecked")
  public <D> D readRow(Class<? extends D> clazz) throws SQLException {

    checkRow();
    
    try {
      if (dataClass != clazz) {
        if (generator == null) {
          generator = new ObjectFactoryGenerator(getMetaData(), config);
        }
        objectFactory = generator.getFactory(clazz);
        dataClass = clazz;
      }
      return (D) objectFactory.createObject(this);
    } catch (SQLException sqle) {
      throw sqle;
    } catch (Exception e) {
      SQLException ex = new SQLException(text.get("createObject", e.toString()));
      ex.initCause(e);
      throw ex;
    }
  }

  public <D> Iterable<D> readAllRows(Class<? extends D> clazz) {

    return new RowIterator<D>(this, clazz);
  }

  /**
   * Reads a single row and closes the result set.
   * @param <D>
   * @param clazz the data object's class
   * @return data object
   * @throws SQLException
   */
  public <D> D select(Class<? extends D> clazz) throws SQLException {
    
    if (nextRow()) {
      D result = readRow(clazz);
      closeResultSet();
      return result;
    } else {
      return null;
    }
  }
}
