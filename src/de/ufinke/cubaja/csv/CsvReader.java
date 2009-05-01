// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.*;
import java.util.*;
import de.ufinke.cubaja.util.*;
import de.ufinke.cubaja.config.*;

public class CsvReader {

  static private final Text text = new Text(CsvReader.class);
  
  private CsvConfig config;
  private LineNumberReader in;
  
  private boolean eof;
  private String line;
  private int currentIndex;
    
  private ColConfig colConfig;
  private List<ColConfig> columnList;
  private Map<String, Integer> nameMap;
  private LineParser parser;
  
  public CsvReader(CsvConfig config) throws IOException, ConfigException, CsvException {
    
    this(config.createReader(), config);
  }

  public CsvReader(Reader reader) throws CsvException {
  
    this(reader, new CsvConfig());
  }
  
  public CsvReader(Reader reader, CsvConfig config) throws CsvException {
  
    this.config = config;
    in = new LineNumberReader(reader);
    in.setLineNumber(1);

    columnList = config.getColumnList();
    
    readHeaderLine();
    initPositions();
    initParser();
  }
  
  private void readHeaderLine() {
    
    if (! config.hasHeaderLine()) {
      return;
    }
    
    if (! nextLine()) {
      return;
    }
    
    String[] headers = readColumns();
    Map<String, Integer> headerMap = new HashMap<String, Integer>();
    for (int i = 0; i < headers.length; i++) {
      headerMap.put(headers[i], i + 1);
    }
    
    for (ColConfig col : columnList) {
      if (col.getPosition() == 0 && col.getHeader() != null) {
        Integer position = headerMap.get(col.getHeader());
        if (position == null) {
          throw new CsvException(text.get("headerNotFound", col.getHeader()), 1, line);
        }
        col.setInternalPosition(position);
      }
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
      
      nameMap.put(col.getName(), col.getPosition());
    }
  }
  
  private void initParser() {
    
    parser = config.getParser();
    parser.init(config);
  }
  
  public void close() throws IOException {
    
    in.close();
    in = null;
    eof = true;
  }
  
  private void checkEOF() throws CsvException {
    
    if (eof) {
      throw new CsvException("eof");
    }
  }
  
  public boolean nextLine() throws CsvException {
    
    try {      
      line = in.readLine();
      parser.setLine(line, in.getLineNumber());
      currentIndex = 0;
    } catch (IOException e) {
      throw new CsvException(text.get("ioException", in.getLineNumber()), e, in.getLineNumber(), null);
    }
    
    eof = (line == null);
    return ! eof;
  }
  
  public boolean isEmptyLine() throws CsvException {

    checkEOF();
    return line.length() < parser.getColumnCount();
  }
  
  public String getLine() throws CsvException {
    
    checkEOF();
    return line;
  }
  
  private String getColumn(int index) {
    
    currentIndex = index;
    
    String s = parser.getColumn(index);
    
    int configIndex = (index < 1 || index >= columnList.size()) ? 0 : index;
    colConfig = columnList.get(configIndex);
    
    if (colConfig.isTrim()) {
      s = s.trim();
    }
    
    if (s.length() == 0) {
      s = colConfig.getNullValue();
    }
    
    return s;
  }
  
  private int getColumnIndex(String columnName) throws CsvException {
    
    Integer index = nameMap.get(columnName);
    if (index == null) {
      throw new CsvException(text.get("undefinedName", columnName));
    }
    return index; 
  }
  
  public String[] readColumns() throws CsvException {
    
    int count = parser.getColumnCount();
    String[] col = new String[count];
    
    int i = 0;
    while (i < count) {
      col[i] = getColumn(++i);
    }
    
    return col;
  }
  
  public int getColumnCount() throws CsvException {
    
    checkEOF();
    return parser.getColumnCount();
  }
  
  public String readString() throws CsvException {
    
    return readString(++currentIndex);
  }
  
  public String readString(String columnName) throws CsvException {
    
    return readString(getColumnIndex(columnName));
  }
  
  public String readString(int columnPosition) throws CsvException {

    return getColumn(columnPosition);
  }
  
  public <D> D readObject(Class<? extends D> clazz) throws CsvException {
    
    checkEOF();
    //TODO
    return null;
  }
  
  public <D> Iterable<D> iterator(Class<? extends D> clazz) {
    
    return new ObjectIterator<D>(this, clazz);
  }
  
}
