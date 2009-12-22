// Copyright (c) 2007 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.util.Text;

public class CsvWriter {

  static private Text text = new Text(CsvWriter.class);
  
  private Writer out;
  private List<ColConfig> columnList;
  private Map<String, Integer> nameMap;
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
    
    columnList = config.getColumnList();
    
    initPositions();
    
    formatter = config.getFormatter();
    formatter.init(out, config);
    buffer = new ColumnBuffer(columnList.size());
    
    if (config.hasHeaderRow()) {
      writeHeaderRow();
    }
  }
  
  private void initPositions() {
    
    nameMap = new HashMap<String, Integer>();
    
    int nextPosition = 0; // first list entry is dummy / default
    
    for (ColConfig col : columnList) {
      
      int colPosition = col.getPosition();
      if (colPosition == 0) {
        col.setInternalPosition(nextPosition);
      }
      nextPosition = col.getPosition() + 1;

      if (! col.isDummyColumn()) {
        nameMap.put(col.getName(), col.getPosition());
      }      
    }
  }
  
  private void writeHeaderRow() throws IOException, CsvException {

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
    
    Integer index = nameMap.get(columnName);
    if (index == null) {
      throw new CsvException(text.get("undefinedName", columnName));
    }
    return index; 
  }
  
  private void setColConfig(int position) {
    
    int configIndex = (position < 1 || position >= columnList.size()) ? 0 : position;
    colConfig = columnList.get(configIndex);
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
