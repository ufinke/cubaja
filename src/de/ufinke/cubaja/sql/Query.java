// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.*;
import java.util.Date;
import de.ufinke.cubaja.io.ColumnReader;

public class Query extends PreparedSql implements ColumnReader {

  private ResultSet resultSet;
  
  Query(PreparedStatement statement, Sql sql) {
  
    super(statement, sql);
  }
  
  public ResultSetMetaData getMetaData() throws SQLException {
    
    return (resultSet == null) ? null : resultSet.getMetaData();
  }
  
  public void execute() throws SQLException {
    
    closeResultSet();
    resultSet = statement.executeQuery();
  }
  
  public void closeResultSet() throws SQLException {
    
    if (resultSet != null) {
      resultSet.close();
      resultSet = null;
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

    // TODO Auto-generated method stub
    return 0;
  }

  public int getColumnPosition(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public int getRowCount() {

    // TODO Auto-generated method stub
    return 0;
  }

  public boolean nextRow() throws SQLException {

    // TODO Auto-generated method stub
    return false;
  }

  public <D> Iterable<D> readAllRows(Class<? extends D> clazz) {

    // TODO Auto-generated method stub
    return null;
  }

  public BigDecimal readBigDecimal(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public BigDecimal readBigDecimal(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public BigInteger readBigInteger(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public BigInteger readBigInteger(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public boolean readBoolean(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return false;
  }

  public boolean readBoolean(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return false;
  }

  public Boolean readBooleanObject(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public Boolean readBooleanObject(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public byte readByte(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public byte readByte(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public Byte readByteObject(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public Byte readByteObject(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public char readChar(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public char readChar(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public Character readCharObject(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public Character readCharObject(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public String[] readColumns() throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public Date readDate(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public Date readDate(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public double readDouble(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public double readDouble(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public Double readDoubleObject(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public Double readDoubleObject(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public <E extends Enum<E>> E readEnum(String columnName, Class<E> clazz) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public <E extends Enum<E>> E readEnum(int columnPosition, Class<E> clazz) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public float readFloat(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public float readFloat(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public Float readFloatObject(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public Float readFloatObject(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public int readInt(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public int readInt(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public Integer readIntObject(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public Integer readIntObject(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public long readLong(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public long readLong(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public Long readLongObject(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public Long readLongObject(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public <D> D readObject(Class<? extends D> clazz) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public short readShort(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public short readShort(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return 0;
  }

  public Short readShortObject(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public Short readShortObject(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public String readString(String columnName) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

  public String readString(int columnPosition) throws SQLException {

    // TODO Auto-generated method stub
    return null;
  }

}
