// Copyright (c) 2007 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import de.ufinke.cubaja.config.ConfigException;

public class CsvWriter {

  private Writer out;
  private CsvConfig config;
  private RowFormatter formatter;
  private ColumnBuffer buffer;
  private ColConfig colConfig;
  
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
  }
  
  public void nextRow() throws IOException, CsvException {
    
    buffer.writeRow(formatter);
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
  
  public void write(String columnName, String value) throws IOException, CsvException {
    
    write(getColumnPosition(columnName), value);
  }
  
  public void write(int position, String value) throws IOException, CsvException {

    setColConfig(position);
    
    if (colConfig.isTrim()) {
      value = value.trim();
    }
    
    writeBuffer(position, value);
  }
  
}
