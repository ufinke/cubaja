// Copyright (c) 2007 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.util.Util;

public class CsvWriter {

  private Writer out;
  private CsvConfig config;
  
  private RowFormatter formatter;
  private ColumnBuffer buffer;
  private ColConfig colConfig;
  
  private int rowCount;
  
  public CsvWriter(CsvConfig config) throws ConfigException, IOException, CsvException {
    
    this(config.createWriter(), config);
  }
  
  public CsvWriter(Writer writer) throws IOException, CsvException {
    
    this(writer, new CsvConfig());
  }
  
  public CsvWriter(Writer writer, CsvConfig config) throws IOException, CsvException {
  
    out = writer;
    this.config = config;
    
    config.initPositions();
    
    formatter = config.getFormatter();
    formatter.init(out, config);
    buffer = new ColumnBuffer(config.getColumnList().size());
    
    if (config.hasHeaderRow()) {
      writeHeaderRow();
    }
  }
  
  private void writeHeaderRow() throws IOException, CsvException {

    List<ColConfig> columnList = config.getColumnList();
    
    for (int i = 1; i < columnList.size(); i++) {
      ColConfig col = columnList.get(i);
      String header = col.getHeader();
      if (header == null) {
        header = col.getName();
      }
      write(i, header);
    }
    
    nextRow();
    rowCount--;
  }
  
  public void close() throws IOException {
    
    out.close();
    out = null;
  }
  
  public void nextRow() throws IOException, CsvException {
    
    buffer.writeRow(formatter);
    rowCount++;
  }
  
  public int getRowCount() {
    
    return rowCount;
  }
  
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
  
  private void writeNull(int position) throws IOException, CsvException {
    
    writeBuffer(position, "");
  }
  
  public void write(String columnName, String value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }
  
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
  
  private void set(int position, boolean value) throws IOException, CsvException {
    
    writeBuffer(position, value ? colConfig.getTrueValue() : colConfig.getFalseValue());
  }

  public void write(String columnName, boolean value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, boolean value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  public void write(String columnName, Boolean value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, Boolean value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  private void set(int position, char value) throws IOException, CsvException {
    
    writeBuffer(position, String.valueOf(value));
  }

  public void write(String columnName, char value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, char value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  public void write(String columnName, Character value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, Character value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  private void set(int position, int value) throws IOException, CsvException {
    
    writeBuffer(position, Integer.toString(value));
  }

  public void write(String columnName, int value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, int value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  public void write(String columnName, Integer value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, Integer value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  private void set(int position, long value) throws IOException, CsvException {
    
    writeBuffer(position, Long.toString(value));
  }

  public void write(String columnName, long value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, long value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  public void write(String columnName, Long value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, Long value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  private void set(int position, double value) throws IOException, CsvException {
    
    writeBuffer(position, Util.format(value, colConfig.getScale(), colConfig.getDecimalChar(), colConfig.isTrim()));
  }

  public void write(String columnName, double value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, double value) throws IOException, CsvException {
    
    setColConfig(position);
    set(position, value);
  }

  public void write(String columnName, Double value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, Double value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  public void write(String columnName, Float value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, Float value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      set(position, value);
    }
  }
  
  public void write(String columnName, BigDecimal value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, BigDecimal value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      writeBuffer(position, Util.format(value, colConfig.getScale(), colConfig.getDecimalChar(), colConfig.isTrim()));
    }
  }
  
  public void write(String columnName, BigInteger value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, BigInteger value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      writeBuffer(position, value.toString());
    }
  }
  
  public void write(String columnName, Date value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }

  public void write(int position, Date value) throws IOException, CsvException {
    
    setColConfig(position);
    if (value == null) {
      writeNull(position);
    } else {
      writeBuffer(position, colConfig.getDateFormat().format(value));
    }
  }
  
}
