// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Common interface for classes which read rows and columns.
 * @author Uwe Finke
 */
public interface ColumnReader {

  /**
   * Returns the number of the current row.
   * @return row number
   */
  public int getRowCount();

  /**
   * Closes the source.
   * @throws Exception when any error occured
   */
  public void close() throws Exception;

  /**
   * Retrieves the next row.
   * @return <code>true</code> when a row was successfully read, <code>false</code> when there were no more rows to process.
   * @throws Exception when any error occured
   */
  public boolean nextRow() throws Exception;

  /**
   * Returns the position of a named column.
   * @param columnName name of column
   * @return column position
   * @throws Exception when any error occured
   */
  public int getColumnPosition(String columnName) throws Exception;

  /**
   * Returns all columns of the last retrieved row.
   * @return array with columns
   * @throws Exception when any error occured
   */
  public String[] readColumns() throws Exception;

  /**
   * Returns the number of columns in the last retrieved row.
   * @return column count
   * @throws Exception when any error occured
   */
  public int getColumnCount() throws Exception;

  /**
   * Returns column content as <code>String</code>.
   * @param columnName name of column
   * @return string
   * @throws Exception when any error occured
   */
  public String readString(String columnName) throws Exception;

  /**
   * Returns column content as <code>String</code>.
   * @param columnPosition position of column, starting with 1
   * @return string
   * @throws Exception when any error occured
   */
  public String readString(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>boolean</code>.
   * @param columnName name of column
   * @return boolean
   * @throws Exception when any error occured
   */
  public boolean readBoolean(String columnName) throws Exception;

  /**
   * Returns column content as <code>boolean</code>.
   * @param columnPosition position of column, starting with 1
   * @return boolean
   * @throws Exception when any error occured
   */
  public boolean readBoolean(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>Boolean</code> object.
   * @param columnName name of column
   * @return boolean
   * @throws Exception when any error occured
   */
  public Boolean readBooleanObject(String columnName) throws Exception;

  /**
   * Returns column content as <code>Boolean</code> object.
   * @param columnPosition position of column, starting with 1
   * @return boolean
   * @throws Exception when any error occured
   */
  public Boolean readBooleanObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>byte</code>.
   * @param columnName name of column
   * @return byte
   * @throws Exception when any error occured
   */
  public byte readByte(String columnName) throws Exception;

  /**
   * Returns column content as <code>byte</code>.
   * @param columnPosition position of column, starting with 1
   * @return byte
   * @throws Exception when any error occured
   */
  public byte readByte(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>Byte</code> object.
   * @param columnName name of column
   * @return byte
   * @throws Exception when any error occured
   */
  public Byte readByteObject(String columnName) throws Exception;

  /**
   * Returns column content as <code>Byte</code> object.
   * @param columnPosition position of column, starting with 1
   * @return byte
   * @throws Exception when any error occured
   */
  public Byte readByteObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>short</code>.
   * @param columnName name of column
   * @return short
   * @throws Exception when any error occured
   */
  public short readShort(String columnName) throws Exception;

  /**
   * Returns column content as <code>short</code>.
   * @param columnPosition position of column, starting with 1
   * @return short
   * @throws Exception when any error occured
   */
  public short readShort(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>Short</code> object.
   * @param columnName name of column
   * @return short
   * @throws Exception when any error occured
   */
  public Short readShortObject(String columnName) throws Exception;

  /**
   * Returns column content as <code>Short</code> object.
   * @param columnPosition position of column, starting with 1
   * @return short
   * @throws Exception when any error occured
   */
  public Short readShortObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>char</code>.
   * @param columnName name of column
   * @return char
   * @throws Exception when any error occured
   */
  public char readChar(String columnName) throws Exception;

  /**
   * Returns column content as <code>char</code>.
   * @param columnPosition position of column, starting with 1
   * @return char
   * @throws Exception when any error occured
   */
  public char readChar(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>Character</code> object.
   * @param columnName name of column
   * @return char
   * @throws Exception when any error occured
   */
  public Character readCharObject(String columnName) throws Exception;

  /**
   * Returns column content as <code>Character</code> object.
   * @param columnPosition position of column, starting with 1
   * @return char
   * @throws Exception when any error occured
   */
  public Character readCharObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>int</code>.
   * @param columnName name of column
   * @return int
   * @throws Exception when any error occured
   */
  public int readInt(String columnName) throws Exception;

  /**
   * Returns column content as <code>int</code>.
   * @param columnPosition position of column, starting with 1
   * @return int
   * @throws Exception when any error occured
   */
  public int readInt(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>Integer</code> object.
   * @param columnName name of column
   * @return int
   * @throws Exception when any error occured
   */
  public Integer readIntObject(String columnName) throws Exception;

  /**
   * Returns column content as <code>Integer</code> object.
   * @param columnPosition position of column, starting with 1
   * @return int
   * @throws Exception when any error occured
   */
  public Integer readIntObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>long</code>.
   * @param columnName name of column
   * @return long
   * @throws Exception when any error occured
   */
  public long readLong(String columnName) throws Exception;

  /**
   * Returns column content as <code>long</code>.
   * @param columnPosition position of column, starting with 1
   * @return long
   * @throws Exception when any error occured
   */
  public long readLong(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>Long</code> object.
   * @param columnName name of column
   * @return long
   * @throws Exception when any error occured
   */
  public Long readLongObject(String columnName) throws Exception;

  /**
   * Returns column content as <code>Long</code> object.
   * @param columnPosition position of column, starting with 1
   * @return long
   * @throws Exception when any error occured
   */
  public Long readLongObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>float</code>.
   * @param columnName name of column
   * @return float
   * @throws Exception when any error occured
   */
  public float readFloat(String columnName) throws Exception;

  /**
   * Returns column content as <code>float</code>.
   * @param columnPosition position of column, starting with 1
   * @return float
   * @throws Exception when any error occured
   */
  public float readFloat(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>Float</code> object.
   * @param columnName name of column
   * @return float
   * @throws Exception when any error occured
   */
  public Float readFloatObject(String columnName) throws Exception;

  /**
   * Returns column content as <code>Float</code> object.
   * @param columnPosition position of column, starting with 1
   * @return float
   * @throws Exception when any error occured
   */
  public Float readFloatObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>double</code>.
   * @param columnName name of column
   * @return double
   * @throws Exception when any error occured
   */
  public double readDouble(String columnName) throws Exception;

  /**
   * Returns column content as <code>double</code>.
   * @param columnPosition position of column, starting with 1
   * @return double
   * @throws Exception when any error occured
   */
  public double readDouble(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>Double</code> object.
   * @param columnName name of column
   * @return double
   * @throws Exception when any error occured
   */
  public Double readDoubleObject(String columnName) throws Exception;

  /**
   * Returns column content as <code>Double</code> object.
   * @param columnPosition position of column, starting with 1
   * @return double
   * @throws Exception when any error occured
   */
  public Double readDoubleObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>BigDecimal</code>.
   * @param columnName name of column
   * @return BigDecimal
   * @throws Exception when any error occured
   */
  public BigDecimal readBigDecimal(String columnName) throws Exception;

  /**
   * Returns column content as <code>BigDecimal</code>.
   * @param columnPosition position of column, starting with 1
   * @return BigDecimal
   * @throws Exception when any error occured
   */
  public BigDecimal readBigDecimal(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>BigInteger</code>.
   * @param columnName name of column
   * @return BigInteger
   * @throws Exception when any error occured
   */
  public BigInteger readBigInteger(String columnName) throws Exception;

  /**
   * Returns column content as <code>BigInteger</code>.
   * @param columnPosition position of column, starting with 1
   * @return BigInteger
   * @throws Exception when any error occured
   */
  public BigInteger readBigInteger(int columnPosition) throws Exception;

  /**
   * Returns column content as <code>Date</code>.
   * @param columnName name of column
   * @return Date
   * @throws Exception when any error occured
   */
  public Date readDate(String columnName) throws Exception;

  /**
   * Returns column content as <code>Date</code>.
   * @param columnPosition position of column, starting with 1
   * @return Date
   * @throws Exception when any error occured
   */
  public Date readDate(int columnPosition) throws Exception;

  /**
   * Returns a data object.
   * <p>
   * The data object class must have setter methods corresponding to 
   * column names. See {@link de.ufinke.cubaja.util.Util#createMethodName} 
   * for details.
   * The setter methods must have a <code>void</code> return type and exactly one parameter
   * of a type supported by one of the sources <code>read</code> 
   * methods.
   * @param <D> data type
   * @param clazz data class
   * @return data object
   * @throws Exception when any error occured
   */
  public <D> D readRow(Class<? extends D> clazz) throws Exception;

  /**
   * Returns an <code>Iterable</code> over all rows.
   * The data object is created by {@link #readRow}.
   * @param <D> data type
   * @param clazz data class
   * @return Iterable
   */
  public <D> Iterable<D> cursor(Class<? extends D> clazz);

}
