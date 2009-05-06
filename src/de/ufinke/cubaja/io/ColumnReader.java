// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

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
   * @return <code>true</code> when a row was successfully read, <code>false</code> when there were no more rows to process.
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
   * Returns column content as string.
   * @param columnName 
   * @return string
   * @throws Exception
   */
  public String readString(String columnName) throws Exception;

  /**
   * Returns column content as string.
   * @param columnPosition
   * @return string
   * @throws Exception
   */
  public String readString(int columnPosition) throws Exception;

  /**
   * Returns column content as boolean.
   * @param columnName
   * @return boolean
   * @throws Exception
   */
  public boolean readBoolean(String columnName) throws Exception;

  /**
   * Returns column content as boolean.
   * @param columnPosition
   * @return boolean
   * @throws Exception
   */
  public boolean readBoolean(int columnPosition) throws Exception;

  /**
   * Returns column content as Boolean object.
   * @param columnName
   * @return boolean
   * @throws Exception
   */
  public Boolean readBooleanObject(String columnName) throws Exception;

  /**
   * Returns column content as Boolean object.
   * @param columnPosition
   * @return boolean
   * @throws Exception
   */
  public Boolean readBooleanObject(int columnPosition) throws Exception;

  /**
   * Returns column content as byte.
   * @param columnName
   * @return byte
   * @throws Exception
   */
  public byte readByte(String columnName) throws Exception;

  /**
   * Returns column content as byte.
   * @param columnPosition
   * @return byte
   * @throws Exception
   */
  public byte readByte(int columnPosition) throws Exception;

  /**
   * Returns column content as Byte object.
   * @param columnName
   * @return byte
   * @throws Exception
   */
  public Byte readByteObject(String columnName) throws Exception;

  /**
   * Returns column content as Byte object.
   * @param columnPosition
   * @return byte
   * @throws Exception
   */
  public Byte readByteObject(int columnPosition) throws Exception;

  /**
   * Returns column content as short.
   * @param columnName
   * @return short
   * @throws Exception
   */
  public short readShort(String columnName) throws Exception;

  /**
   * Returns column content as short.
   * @param columnPosition
   * @return short
   * @throws Exception
   */
  public short readShort(int columnPosition) throws Exception;

  /**
   * Returns column content as Short object.
   * @param columnName
   * @return short
   * @throws Exception
   */
  public Short readShortObject(String columnName) throws Exception;

  /**
   * Returns column content as Short object.
   * @param columnPosition
   * @return short
   * @throws Exception
   */
  public Short readShortObject(int columnPosition) throws Exception;

  /**
   * Returns column content as char.
   * @param columnName
   * @return char
   * @throws Exception
   */
  public char readChar(String columnName) throws Exception;

  /**
   * Returns column content as char.
   * @param columnPosition
   * @return char
   * @throws Exception
   */
  public char readChar(int columnPosition) throws Exception;

  /**
   * Returns column content as Character object.
   * @param columnName
   * @return char
   * @throws Exception
   */
  public Character readCharObject(String columnName) throws Exception;

  /**
   * Returns column content as Character object.
   * @param columnPosition
   * @return char
   * @throws Exception
   */
  public Character readCharObject(int columnPosition) throws Exception;

  /**
   * Returns column content as int.
   * @param columnName
   * @return int
   * @throws Exception
   */
  public int readInt(String columnName) throws Exception;

  /**
   * Returns column content as int.
   * @param columnPosition
   * @return int
   * @throws Exception
   */
  public int readInt(int columnPosition) throws Exception;

  /**
   * Returns column content as Integer object.
   * @param columnName
   * @return int
   * @throws Exception
   */
  public Integer readIntObject(String columnName) throws Exception;

  /**
   * Returns column content as Integer object.
   * @param columnPosition
   * @return int
   * @throws Exception
   */
  public Integer readIntObject(int columnPosition) throws Exception;

  /**
   * Returns column content as long.
   * @param columnName
   * @return long
   * @throws Exception
   */
  public long readLong(String columnName) throws Exception;

  /**
   * Returns column content as long.
   * @param columnPosition
   * @return long
   * @throws Exception
   */
  public long readLong(int columnPosition) throws Exception;

  /**
   * Returns column content as Long object.
   * @param columnName
   * @return long
   * @throws Exception
   */
  public Long readLongObject(String columnName) throws Exception;

  /**
   * Returns column content as Long object.
   * @param columnPosition
   * @return long
   * @throws Exception
   */
  public Long readLongObject(int columnPosition) throws Exception;

  /**
   * Returns column content as float.
   * @param columnName
   * @return float
   * @throws Exception
   */
  public float readFloat(String columnName) throws Exception;

  /**
   * Returns column content as float.
   * @param columnPosition
   * @return float
   * @throws Exception
   */
  public float readFloat(int columnPosition) throws Exception;

  /**
   * Returns column content as Float object.
   * @param columnName
   * @return float
   * @throws Exception
   */
  public Float readFloatObject(String columnName) throws Exception;

  /**
   * Returns column content as Float object.
   * @param columnPosition
   * @return float
   * @throws Exception
   */
  public Float readFloatObject(int columnPosition) throws Exception;

  /**
   * Returns column content as double.
   * @param columnName
   * @return double
   * @throws Exception
   */
  public double readDouble(String columnName) throws Exception;

  /**
   * Returns column content as double.
   * @param columnPosition
   * @return double
   * @throws Exception
   */
  public double readDouble(int columnPosition) throws Exception;

  /**
   * Returns column content as Double object.
   * @param columnName
   * @return double
   * @throws Exception
   */
  public Double readDoubleObject(String columnName) throws Exception;

  /**
   * Returns column content as Double object.
   * @param columnPosition
   * @return double
   * @throws Exception
   */
  public Double readDoubleObject(int columnPosition) throws Exception;

  /**
   * Returns column content as BigDecimal.
   * @param columnName
   * @return BigDecimal
   * @throws Exception
   */
  public BigDecimal readBigDecimal(String columnName) throws Exception;

  /**
   * Returns column content as BigDecimal.
   * @param columnPosition
   * @return BigDecimal
   * @throws Exception
   */
  public BigDecimal readBigDecimal(int columnPosition) throws Exception;

  /**
   * Returns column content as BigInteger.
   * @param columnName
   * @return BigInteger
   * @throws Exception
   */
  public BigInteger readBigInteger(String columnName) throws Exception;

  /**
   * Returns column content as BigInteger.
   * @param columnPosition
   * @return BigInteger
   * @throws Exception
   */
  public BigInteger readBigInteger(int columnPosition) throws Exception;

  /**
   * Returns column content as Date.
   * @param columnName
   * @return Date
   * @throws Exception
   */
  public Date readDate(String columnName) throws Exception;

  /**
   * Returns column content as Date.
   * @param columnPosition
   * @return Date
   * @throws Exception
   */
  public Date readDate(int columnPosition) throws Exception;

  /**
   * Returns a column content as Enum constant.
   * If the column content start with a digit, 
   * the constant is derived using the position of the Enum constant value array.
   * Otherwise, the constant is identified by name.
   * @param <E> Enum type
   * @param columnName
   * @param clazz Enum class
   * @return Enum constant 
   * @throws Exception
   */
  public <E extends Enum<E>> E readEnum(String columnName, Class<E> clazz) throws Exception;

  /**
   * Returns a column content as Enum constant.
   * If the column content start with a digit, 
   * the constant is derived using the position of the Enum constant value array.
   * Otherwise, the constant is identified by name.
   * @param <E> Enum type
   * @param columnPosition
   * @param clazz Enum class
   * @return Enum constant 
   * @throws Exception
   */
  public <E extends Enum<E>> E readEnum(int columnPosition, Class<E> clazz) throws Exception;

  /**
   * Returns a data object.
   * <p>
   * The data object class must have setter methods corresponding to 
   * column names. See description of method <code>createMethodName</code> 
   * of class <code>de.ufinke.cubaja.Util</code> for building method names from
   * column names.
   * The setter methods must have a void return type and exactly one parameter
   * of a type supported by one of the sources <code>read</code> 
   * methods.
   * @param <D> data type
   * @param clazz
   * @return data object
   * @throws Exception
   */
  public <D> D readObject(Class<? extends D> clazz) throws Exception;

  /**
   * Returns an <code>Iterable</code> over all rows.
   * The data object is created by <code>readObject</code>.
   * @param <D> data type
   * @param clazz
   * @return Iterable
   */
  public <D> Iterable<D> readAllRows(Class<? extends D> clazz);

}
