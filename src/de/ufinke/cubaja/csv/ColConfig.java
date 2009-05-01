// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.text.*;
import de.ufinke.cubaja.config.*;
import de.ufinke.cubaja.util.*;

public class ColConfig {

  static private final Text text = new Text(ColConfig.class);
  
  private String name;
  private String header;
  private int position;
  private String nullValue;
  private Boolean trim;
  private Character decimalChar;
  private SimpleDateFormat dateFormat;
  
  private CsvConfig csvConfig;

  public ColConfig() {

    nullValue = "";
  }
  
  void setCsvConfig(CsvConfig csvConfig) {
    
    this.csvConfig = csvConfig;
  }

  public String getName() {

    return name;
  }

  @Mandatory
  public void setName(String name) {

    this.name = name;
  }

  public String getHeader() {

    return header;
  }

  public void setHeader(String header) {

    this.header = header;
  }

  public int getPosition() {

    return position;
  }
  
  void setInternalPosition(int position) {
    
    this.position = position;
  }
  
  public void setPosition(int position) throws ConfigException {

    if (position < 1) {
      throw new ConfigException(text.get("invalidPosition"));
    }
    this.position = position;
  }

  public String getNullValue() {

    return nullValue;
  }

  public void setNullValue(String nullValue) {

    this.nullValue = nullValue;
  }

  public Boolean isTrim() {

    if (trim == null) {
      trim = csvConfig.getTrim();
    }
    return trim;
  }

  public void setTrim(Boolean trim) {

    this.trim = trim;
  }

  public Character getDecimalChar() {

    if (decimalChar == null) {
      decimalChar = csvConfig.getDecimalChar();
    }
    return decimalChar;
  }

  public void setDecimalChar(Character decimalChar) {

    this.decimalChar = decimalChar;
  }

  public SimpleDateFormat getDateFormat() {

    if (dateFormat == null) {
      dateFormat = csvConfig.getDateFormat();
    }
    return dateFormat;
  }

  public void setDatePattern(String datePattern) {

    this.dateFormat = new SimpleDateFormat(datePattern);
  }
}
