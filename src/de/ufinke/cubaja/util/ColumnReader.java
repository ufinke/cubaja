// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Common interface for classes that read rows and columns.
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
   * @throws Exception
   */
  public void close() throws Exception;

  /**
   * Retrieves the next row.
   * @return <tt>true</tt> when a row was successfully read, <tt>false</tt> when there were no more rows to process.
   * @throws Exception
   */
  public boolean nextRow() throws Exception;

  /**
   * Returns the position of a named column.
   * @param columnName
   * @return column position
   * @throws Exception
   */
  public int getColumnPosition(String columnName) throws Exception;

  /**
   * Returns all columns of the last retrieved row.
   * @return array with columns
   * @throws Exception
   */
  public String[] readColumns() throws Exception;

  /**
   * Returns the number of columns in the last retrieved row.
   * @return column count
   * @throws Exception
   */
  public int getColumnCount() throws Exception;

  /**
   * Returns column content as <tt>String</tt>.
   * @param columnName 
   * @return string
   * @throws Exception
   */
  public String readString(String columnName) throws Exception;

  /**
   * Returns column content as <tt>String</tt>.
   * @param columnPosition
   * @return string
   * @throws Exception
   */
  public String readString(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>boolean</tt>.
   * @param columnName
   * @return boolean
   * @throws Exception
   */
  public boolean readBoolean(String columnName) throws Exception;

  /**
   * Returns column content as <tt>boolean</tt>.
   * @param columnPosition
   * @return boolean
   * @throws Exception
   */
  public boolean readBoolean(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>Boolean</tt> object.
   * @param columnName
   * @return boolean
   * @throws Exception
   */
  public Boolean readBooleanObject(String columnName) throws Exception;

  /**
   * Returns column content as <tt>Boolean</tt> object.
   * @param columnPosition
   * @return boolean
   * @throws Exception
   */
  public Boolean readBooleanObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>byte</tt>.
   * @param columnName
   * @return byte
   * @throws Exception
   */
  public byte readByte(String columnName) throws Exception;

  /**
   * Returns column content as <tt>byte</tt>.
   * @param columnPosition
   * @return byte
   * @throws Exception
   */
  public byte readByte(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>Byte</tt> object.
   * @param columnName
   * @return byte
   * @throws Exception
   */
  public Byte readByteObject(String columnName) throws Exception;

  /**
   * Returns column content as <tt>Byte</tt> object.
   * @param columnPosition
   * @return byte
   * @throws Exception
   */
  public Byte readByteObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>short</tt>.
   * @param columnName
   * @return short
   * @throws Exception
   */
  public short readShort(String columnName) throws Exception;

  /**
   * Returns column content as <tt>short</tt>.
   * @param columnPosition
   * @return short
   * @throws Exception
   */
  public short readShort(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>Short</tt> object.
   * @param columnName
   * @return short
   * @throws Exception
   */
  public Short readShortObject(String columnName) throws Exception;

  /**
   * Returns column content as <tt>Short</tt> object.
   * @param columnPosition
   * @return short
   * @throws Exception
   */
  public Short readShortObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>char</tt>.
   * @param columnName
   * @return char
   * @throws Exception
   */
  public char readChar(String columnName) throws Exception;

  /**
   * Returns column content as <tt>char</tt>.
   * @param columnPosition
   * @return char
   * @throws Exception
   */
  public char readChar(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>Character</tt> object.
   * @param columnName
   * @return char
   * @throws Exception
   */
  public Character readCharObject(String columnName) throws Exception;

  /**
   * Returns column content as <tt>Character</tt> object.
   * @param columnPosition
   * @return char
   * @throws Exception
   */
  public Character readCharObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>int</tt>.
   * @param columnName
   * @return int
   * @throws Exception
   */
  public int readInt(String columnName) throws Exception;

  /**
   * Returns column content as <tt>int</tt>.
   * @param columnPosition
   * @return int
   * @throws Exception
   */
  public int readInt(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>Integer</tt> object.
   * @param columnName
   * @return int
   * @throws Exception
   */
  public Integer readIntObject(String columnName) throws Exception;

  /**
   * Returns column content as <tt>Integer</tt> object.
   * @param columnPosition
   * @return int
   * @throws Exception
   */
  public Integer readIntObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>long</tt>.
   * @param columnName
   * @return long
   * @throws Exception
   */
  public long readLong(String columnName) throws Exception;

  /**
   * Returns column content as <tt>long</tt>.
   * @param columnPosition
   * @return long
   * @throws Exception
   */
  public long readLong(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>Long</tt> object.
   * @param columnName
   * @return long
   * @throws Exception
   */
  public Long readLongObject(String columnName) throws Exception;

  /**
   * Returns column content as <tt>Long</tt> object.
   * @param columnPosition
   * @return long
   * @throws Exception
   */
  public Long readLongObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>float</tt>.
   * @param columnName
   * @return float
   * @throws Exception
   */
  public float readFloat(String columnName) throws Exception;

  /**
   * Returns column content as <tt>float</tt>.
   * @param columnPosition
   * @return float
   * @throws Exception
   */
  public float readFloat(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>Float</tt> object.
   * @param columnName
   * @return float
   * @throws Exception
   */
  public Float readFloatObject(String columnName) throws Exception;

  /**
   * Returns column content as <tt>Float</tt> object.
   * @param columnPosition
   * @return float
   * @throws Exception
   */
  public Float readFloatObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>double</tt>.
   * @param columnName
   * @return double
   * @throws Exception
   */
  public double readDouble(String columnName) throws Exception;

  /**
   * Returns column content as <tt>double</tt>.
   * @param columnPosition
   * @return double
   * @throws Exception
   */
  public double readDouble(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>Double</tt> object.
   * @param columnName
   * @return double
   * @throws Exception
   */
  public Double readDoubleObject(String columnName) throws Exception;

  /**
   * Returns column content as <tt>Double</tt> object.
   * @param columnPosition
   * @return double
   * @throws Exception
   */
  public Double readDoubleObject(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>BigDecimal</tt>.
   * @param columnName
   * @return BigDecimal
   * @throws Exception
   */
  public BigDecimal readBigDecimal(String columnName) throws Exception;

  /**
   * Returns column content as <tt>BigDecimal</tt>.
   * @param columnPosition
   * @return BigDecimal
   * @throws Exception
   */
  public BigDecimal readBigDecimal(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>BigInteger</tt>.
   * @param columnName
   * @return BigInteger
   * @throws Exception
   */
  public BigInteger readBigInteger(String columnName) throws Exception;

  /**
   * Returns column content as <tt>BigInteger</tt>.
   * @param columnPosition
   * @return BigInteger
   * @throws Exception
   */
  public BigInteger readBigInteger(int columnPosition) throws Exception;

  /**
   * Returns column content as <tt>Date</tt>.
   * @param columnName
   * @return Date
   * @throws Exception
   */
  public Date readDate(String columnName) throws Exception;

  /**
   * Returns column content as <tt>Date</tt>.
   * @param columnPosition
   * @return Date
   * @throws Exception
   */
  public Date readDate(int columnPosition) throws Exception;

  /**
   * Returns a data object.
   * <p>
   * The data object class must have setter methods corresponding to 
   * column names. See {@link de.ufinke.cubaja.util.Util#createMethodName createMethodName} 
   * for details.
   * The setter methods must have a <tt>void</tt> return type and exactly one parameter
   * of a type supported by one of the source's <tt>read</tt> 
   * methods.
   * @param <D> data type
   * @param clazz
   * @return data object
   * @throws Exception
   */
  public <D> D readRow(Class<? extends D> clazz) throws Exception;

  /**
   * Returns an <tt>Iterable</tt> over all rows.
   * The data object is created by {@link #readRow readRow}.
   * @param <D> data type
   * @param clazz
   * @return Iterable
   */
  public <D> Iterable<D> cursor(Class<? extends D> clazz);

}
