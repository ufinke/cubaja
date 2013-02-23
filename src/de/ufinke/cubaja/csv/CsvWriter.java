// Copyright (c) 2007 - 2013, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.util.Text;
import de.ufinke.cubaja.util.Util;

/**
 * CSV writer.
 * @author Uwe Finke
 */
public class CsvWriter {

  static private Text text = Text.getPackageInstance(CsvWriter.class);
  
  private Writer out;
  private CsvConfig config;
  
  private RowFormatter formatter;
  private ColumnBuffer buffer;
  private ColConfig colConfig;
  
  private ObjectWriterGenerator generator;
  private Class<?> dataClass;
  private ObjectWriter objectWriter;
  
  private int rowCount;
  
  /**
   * Constructor with configuration.
   * If you use this constructor,
   * you have to set the configurations <tt>file</tt> property.
   * @param config
   * @throws IOException
   * @throws ConfigException
   * @throws CsvException
   */
  public CsvWriter(CsvConfig config) throws ConfigException, IOException, CsvException {
    
    this(config.getFile().createWriter(), config);
  }
  
  /**
   * Constructor with implicit default configuration.
   * With this constructor, there are no columns defined.
   * @param writer
   * @throws CsvException
   */
  public CsvWriter(Writer writer) throws IOException, CsvException {
    
    this(writer, new CsvConfig());
  }
  
  /**
   * Constructor with writer and configuration.
   * @param writer
   * @param config
   * @throws CsvException
   */
  public CsvWriter(Writer writer, CsvConfig config) throws IOException, CsvException {
  
    out = writer;
    this.config = config;
    
    formatter = config.getFormatter();
    formatter.init(out, config);
    buffer = new ColumnBuffer(config);
    
    if (config.hasHeaderRow()) {
      writeHeaderRow(config);
    }
  }
  
  private void writeHeaderRow(CsvConfig config) throws IOException, CsvException {

    for (ColConfig col : config.getColumnList()) {
      String header = col.getHeader();
      if (header == null) {
        header = col.getName();
      }
      write(col.getPosition(), header);
    }
    
    nextRow();
    rowCount--;
  }
  
  /**
   * Closes the underlaying writer.
   * @throws IOException
   */
  public void close() throws IOException, CsvException {
    
    formatter.finish();
    out.close();
    out = null;
  }
  
  /**
   * Advances to next row.
   * @throws IOException
   * @throws CsvException
   */
  public void nextRow() throws IOException, CsvException {
    
    buffer.writeRow(formatter);
    rowCount++;
  }
  
  /**
   * Returns the number of rows written so far.
   * @return row count
   */
  public int getRowCount() {
    
    return rowCount;
  }
  
  /**
   * Returns the position of a named column.
   * @param columnName
   * @return position
   * @throws CsvException
   */
  public int getColumnPosition(String columnName) throws CsvException {

    return config.getColumnPosition(columnName);
  }
  
  private void setColConfig(int position) {
    
    colConfig = config.getColConfig(position);
  }
  
  private void writeBuffer(int position, String value) throws CsvException {
    
    List<ReplaceConfig> replaceList = colConfig.getReplaceList();
    if (replaceList != null) {
      for (ReplaceConfig replace : replaceList) {
        value = value.replaceAll(replace.getRegex(), replace.getReplacement());
      }
    }
    
    if (colConfig.getEditor() != null) {
      value = colConfig.getEditor().editColumn(value, colConfig);
    }
    
    buffer.setColumn(position, value);
  }
  
  private void writeNull(int position) throws CsvException {
    
    writeBuffer(position, "");
  }
  
  /**
   * Fills column identified by name with a <tt>String</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, String value) throws IOException, CsvException {
    
    write(config.getColumnPosition(columnName), value);
  }
  
  /**
   * Fills column identified by position with a <tt>String</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, String value) throws IOException, CsvException {

    setColConfig(position);
    
    if (value == null) {
      writeNull(position);
    } else {
      if (colConfig.isTrim()) {
        value = value.trim();
      }      
      writeBuffer(position, value);
    }
  }
  
  private void set(int position, boolean value) throws CsvException {
    
    writeBuffer(position, value ? colConfig.getTrueValue() : colConfig.getFalseValue());
  }

  /**
   * Fills column identified by name with a <tt>boolean</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, boolean value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>boolean</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, boolean value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  /**
   * Fills column identified by name with a <tt>Boolean</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, Boolean value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>Boolean</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, Boolean value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  private void set(int position, char value) throws CsvException {
    
    writeBuffer(position, String.valueOf(value));
  }

  /**
   * Fills column identified by name with a <tt>char</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, char value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>char</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, char value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  /**
   * Fills column identified by name with a <tt>Character</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, Character value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>Character</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, Character value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  private void set(int position, int value) throws CsvException {
    
    writeBuffer(position, Integer.toString(value));
  }

  /**
   * Fills column identified by name with a <tt>byte</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, byte value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>byte</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, byte value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  /**
   * Fills column identified by name with an <tt>short</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, short value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>short</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, short value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  /**
   * Fills column identified by name with an <tt>int</tt> (or <tt>byte</tt> or <tt>short</tt>).
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, int value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with an <tt>int</tt> (or <tt>byte</tt> or <tt>short</tt>).
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, int value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  /**
   * Fills column identified by name with a <tt>Byte</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, Byte value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>Byte</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, Byte value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  /**
   * Fills column identified by name with a <tt>Short</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, Short value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>Short</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, Short value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  /**
   * Fills column identified by name with an <tt>Integer</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, Integer value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with an <tt>Integer</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, Integer value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  private void set(int position, long value) throws CsvException {
    
    writeBuffer(position, Long.toString(value));
  }

  /**
   * Fills column identified by name with a <tt>long</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, long value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>long</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, long value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  /**
   * Fills column identified by name with a <tt>Long</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, Long value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>Long</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, Long value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  private void set(int position, double value) throws CsvException {
    
    Character decimalChar = colConfig.getWriterDecimalChar();    
    writeBuffer(position, Util.format(value, colConfig.getScale(), decimalChar, colConfig.isTrim()));
  }

  /**
   * Fills column identified by name with a <tt>double</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, double value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>double</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, double value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  /**
   * Fills column identified by name with a <tt>float</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, float value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>float</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, float value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  /**
   * Fills column identified by name with a <tt>Double</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, Double value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>Double</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, Double value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  /**
   * Fills column identified by name with a <tt>Float</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, Float value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>Float</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, Float value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  /**
   * Fills column identified by name with a <tt>BigDecimal</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, BigDecimal value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>BigDecimal</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, BigDecimal value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      writeBuffer(position, Util.format(value, colConfig.getScale(), colConfig.getWriterDecimalChar(), colConfig.isTrim()));
    }
  }
  
  /**
   * Fills column identified by name with a <tt>BigInteger</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, BigInteger value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>BigInteger</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, BigInteger value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      writeBuffer(position, value.toString());
    }
  }
  
  /**
   * Fills column identified by name with a <tt>Date</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, Date value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  /**
   * Fills column identified by position with a <tt>Date</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, Date value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      writeBuffer(position, colConfig.getDateFormat().format(value));
    }
  }
  
  /**
   * Fills column identified by name with an <tt>Enum</tt>.
   * @param columnName
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(String columnName, Enum<?> value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }
  
  /**
   * Fills column identified by position with an <tt>Enum</tt>.
   * @param position
   * @param value
   * @throws IOException
   * @throws CsvException
   */
  public void write(int position, Enum<?> value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      writeBuffer(position, value.name());
    }
  }
  
  /**
   * Fills columns from getter methods of data object.
   * @param dataObject
   * @throws CsvException
   */
  public void writeObject(Object dataObject) throws CsvException {
    
    Class<?> clazz = dataObject.getClass();
    
    try {
      if (dataClass != clazz) {
        if (generator == null) {
          generator = new ObjectWriterGenerator(config.getNameMap());
        }
        objectWriter = generator.getWriter(clazz);
        dataClass = clazz;
      }
      objectWriter.writeObject(this, dataObject);
    } catch (Exception e) {
      throw new CsvException(text.get("writeObject", clazz.getName()), e);
    }
  }
  
  /**
   * Fills columns from getter methods of data object and writes row.
   * This method just calls <tt>writeObject</tt>
   * and <tt>nextRow</tt>.
   * @param dataObject
   * @throws IOException
   * @throws CsvException
   */
  public void writeRow(Object dataObject) throws IOException, CsvException {
    
    writeObject(dataObject);
    nextRow();
  }
}
