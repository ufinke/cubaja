package de.ufinke.cubaja.csv;

import java.text.*;
import de.ufinke.cubaja.config.*;

public class ColConfig {

  private String name;
  private String header;
  private int position;
  private String nullValue;
  private Boolean trim;
  private Character decimalChar;
  private SimpleDateFormat dateFormat;
  
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

  public void setPosition(int position) {

    this.position = position;
  }

  public String getNullValue() {

    return nullValue;
  }

  public void setNullValue(String nullValue) {

    this.nullValue = nullValue;
  }

  public Boolean getTrim() {

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
