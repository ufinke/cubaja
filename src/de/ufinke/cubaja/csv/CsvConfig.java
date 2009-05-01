// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.*;
import de.ufinke.cubaja.io.*;
import de.ufinke.cubaja.config.*;
import de.ufinke.cubaja.util.*;
import java.text.*;
import java.util.*;

public class CsvConfig {

  static private final Text text = new Text(CsvConfig.class);

  private String fileName;
  private String charset;
  private FileConfig fileConfig;

  private Character separator;
  private Character escapeChar;
  private LineParser parser;

  private Boolean trim;
  private Character decimalChar;
  private SimpleDateFormat dateFormat;

  private Boolean header;
  
  private List<ColConfig> columnList;
  private boolean headerDefined;

  public CsvConfig() {

    columnList = new ArrayList<ColConfig>();
    addCol(new ColConfig()); // default entry; positions start with 1
  }

  public void setFile(String fileName) {

    this.fileName = fileName;
  }

  public void setCharset(String charset) {

    this.charset = charset;
  }

  public Reader createReader() throws IOException, ConfigException {

    if (fileConfig == null) {
      if (fileName == null) {
        throw new ConfigException(text.get("noFileName"));
      }
      fileConfig = new FileConfig();
      fileConfig.setName(fileName);
      fileConfig.setCharset(charset);
    }

    return fileConfig.createReader();
  }

  public void setSeparator(Character separator) {

    this.separator = separator;
  }

  public Character getSeparator() {

    if (separator == null) {
      separator = Character.valueOf('\t');
    }
    return separator;
  }

  public void setEscapeChar(Character escapeChar) {

    this.escapeChar = escapeChar;
  }

  public Character getEscapeChar() {

    return escapeChar;
  }

  public void setParser(LineParser parser) {

    this.parser = parser;
  }

  public LineParser getParser() {

    if (parser == null) {
      parser = (escapeChar == null) ? new SimpleLineParser() : new EscapeLineParser();
    }
    return parser;
  }
  
  public Character getDecimalChar() {
  
    return decimalChar;
  }

  public void setDecimalChar(Character decimalChar) {
  
    this.decimalChar = decimalChar;
  }

  public void setDatePattern(String datePattern) {

    dateFormat = new SimpleDateFormat(datePattern);
  }

  public SimpleDateFormat getDateFormat() {

    if (dateFormat == null) {
      setDatePattern(text.get("datePattern"));
    }
    return dateFormat;
  }

  public Boolean getTrim() {

    if (trim == null) {
      trim = Boolean.FALSE;
    }
    return trim;
  }

  public void setTrim(Boolean trim) {

    this.trim = trim;
  }

  public boolean hasHeaderLine() {

    if (header == null) {
      setHeader(headerDefined);
    }
    return header.booleanValue();
  }

  public void setHeader(Boolean header) {

    this.header = header;
  }

  public void addCol(ColConfig column) {
    
    column.setCsvConfig(this);        
    headerDefined |= (column.getHeader() != null);
    columnList.add(column);
  }
  
  public List<ColConfig> getColumnList() {
    
    return columnList;
  }
  
}
