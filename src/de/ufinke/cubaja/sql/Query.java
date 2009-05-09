// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import de.ufinke.cubaja.io.*;
import de.ufinke.cubaja.util.Text;

public class Query extends PreparedSql implements ColumnReader {

  static private final Text text = new Text(Query.class);
  
  static private Set<Integer> enumNumerics = createEnumNumerics();
  
  static private Set<Integer> createEnumNumerics() {
  
    Set<Integer> set = new HashSet<Integer>();
    
    set.add(Types.BIGINT);
    set.add(Types.DECIMAL);
    set.add(Types.DOUBLE);
    set.add(Types.FLOAT);
    set.add(Types.INTEGER);
    set.add(Types.NUMERIC);
    set.add(Types.REAL);
    set.add(Types.SMALLINT);
    set.add(Types.TINYINT);
    
    return set;
  }
  
  private ResultSet resultSet;
  private ResultSetMetaData metaData;
  private int rowCount;
  private Map<String, Integer> columnMap;
  
  Query(PreparedStatement statement, Sql sql) {
  
    super(statement, sql);
  }
  
  private void execute() throws SQLException {
    
    closeResultSet();
    resultSet = statement.executeQuery();
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
  
  public ResultSetMetaData getMetaData() throws SQLException {
    
    checkExec();
    if (metaData == null) {
      metaData = resultSet.getMetaData();
    }
    return metaData;
  }
  
  public void closeResultSet() throws SQLException {
    
    if (resultSet != null) {
      resultSet.close();
      resultSet = null;
      metaData = null;
    }
  }
  
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
  
  private Map<String, Integer> getColumnMap() throws SQLException {
    
    if (columnMap == null) {
      int columnCount = getColumnCount();
      columnMap = new HashMap<String, Integer>(columnCount << 1);
      for (int i = 1; i <= columnCount; i++) {
        columnMap.put(getMetaData().getColumnLabel(i), i);
      }
    }
    
    return columnMap;
  }

  public int getColumnPosition(String columnName) throws SQLException {

    Integer position = getColumnMap().get(columnName);
    if (position == null) {
      throw new SQLException(text.get("columnNotFound", columnName));
    }
    return position;
  }

  public int getRowCount() {

    return rowCount;
  }

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

  public <D> Iterable<D> readAllRows(Class<? extends D> clazz) {

    return new RowIterator<D>(this, clazz);
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

  public String[] readColumns() throws SQLException {

    int colCount = getColumnCount();
    String[] columns = new String[colCount];
    
    int i = 0;
    while (i < colCount) {      
      columns[i] = readString(++i);
    }
    
    return columns;
  }

  public Date readDate(String columnName) throws SQLException {

    return readDate(getColumnPosition(columnName));
  }

  public Date readDate(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getDate(columnPosition);
  }

  public Date readTimestamp(String columnName) throws SQLException {

    return readTimestamp(getColumnPosition(columnName));
  }

  public Date readTimestamp(int columnPosition) throws SQLException {

    checkRow();
    return new Date(resultSet.getTimestamp(columnPosition).getTime());
  }

  public Timestamp readSQLTimestamp(String columnName) throws SQLException {

    return readSQLTimestamp(getColumnPosition(columnName));
  }

  public Timestamp readSQLTimestamp(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getTimestamp(columnPosition);
  }

  public Time readSQLTime(String columnName) throws SQLException {

    return readSQLTime(getColumnPosition(columnName));
  }

  public Time readSQLTime(int columnPosition) throws SQLException {

    checkRow();
    return resultSet.getTime(columnPosition);
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

  public <E extends Enum<E>> E readEnum(String columnName, Class<E> clazz) throws SQLException {

    return readEnum(getColumnPosition(columnName), clazz);
  }

  public <E extends Enum<E>> E readEnum(int columnPosition, Class<E> clazz) throws SQLException {

    if (enumNumerics.contains(getMetaData().getColumnType(columnPosition))) {
      return readEnumOrdinal(columnPosition, clazz);
    } else {
      return readEnumConstant(columnPosition, clazz);
    }
  }

  private <E extends Enum<E>> E readEnumOrdinal(int columnPosition, Class<E> clazz) throws SQLException {
    
    int ordinal = readInt(columnPosition);
    if (resultSet.wasNull()) {
      return null;
    }
    
    try {
      return clazz.getEnumConstants()[ordinal];
    } catch (Exception e) {
      throw new SQLException(text.get("enumOrdinal", Integer.valueOf(ordinal)));
    }
  }

  private <E extends Enum<E>> E readEnumConstant(int columnPosition, Class<E> clazz) throws SQLException {
    
    String constant = readString(columnPosition);
    if (resultSet.wasNull()) {
      return null;
    }
    
    try {
      return Enum.valueOf(clazz, constant);
    } catch (Exception e) {
      try {
        return Enum.valueOf(clazz, constant.toUpperCase());
      } catch (Exception e2) {
        throw new SQLException(text.get("enumConstant", constant));
      }
    }
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

  @SuppressWarnings("unchecked")
  public <D> D readObject(Class<? extends D> clazz) throws SQLException {

    try {
      return (D) ObjectFactoryManager.getFactory(clazz, getColumnMap());
    } catch (SQLException sqle) {
      throw sqle;
    } catch (Exception e) {
      SQLException ex = new SQLException(text.get("createObject", e.toString()));
      ex.initCause(e);
      throw ex;
    }
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

  public <D> D select(Class<? extends D> clazz) throws SQLException {
    
    if (nextRow()) {
      D result = readObject(clazz);
      closeResultSet();
      return result;
    } else {
      return null;
    }
  }
}
