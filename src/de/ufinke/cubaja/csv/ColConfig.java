// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.text.*;
import de.ufinke.cubaja.config.*;
import de.ufinke.cubaja.util.*;
import java.util.*;

public class ColConfig {

  static private final Text text = new Text(ColConfig.class);
  
  private String name;
  private String header;
  private int position;
  private Boolean trim;
  private Character decimalChar;
  private SimpleDateFormat dateFormat;
  private String[] trueValues;
  private List<ReplaceConfig> replaceList;
  
  private CsvConfig csvConfig;

  public ColConfig() {

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
  
  public String[] getTrueValues() {

    if (trueValues == null) {
      trueValues = csvConfig.getTrueValues();
    }
    return trueValues;
  }

  public void setTrueValues(String[] trueValues) {
  
    this.trueValues = trueValues;
  }
  
  public void addReplace(ReplaceConfig replace) {
    
    if (replaceList == null) {
      replaceList = new ArrayList<ReplaceConfig>();
    }
    replaceList.add(replace);
  }
  
  public List<ReplaceConfig> getReplaceList() {
    
    return replaceList;
  }
}
