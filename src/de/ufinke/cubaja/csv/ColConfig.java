// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.config.Mandatory;
import de.ufinke.cubaja.util.Text;

/**
 * CSV column properties.
 * <p>
 * A defined column must have a name.
 * All other properties are optional.
 * <p>
 * When we don't set an explicit position,
 * the position is determined by the first matching header.
 * When we don't set a header,
 * the position is the position
 * of the previously defined column plus one.
 * The position of the first column is 1, not 0.
 * @author Uwe Finke
 */
public class ColConfig {

  static private final Text text = new Text(ColConfig.class);
  
  private String name;
  private String header;
  private int position;
  private Boolean trim;
  private Character decimalChar;
  private SimpleDateFormat dateFormat;
  private String[] trueValues;
  private ColumnEditor editor;
  private List<ReplaceConfig> replaceList;
  
  private CsvConfig csvConfig;

  /**
   * Constructor.
   */
  public ColConfig() {

  }
  
  void setCsvConfig(CsvConfig csvConfig) {
    
    this.csvConfig = csvConfig;
  }

  /**
   * Returns the columns name.
   * @return name
   */
  public String getName() {

    return name;
  }

  /**
   * Sets the columns name.
   * @param name
   */
  @Mandatory
  public void setName(String name) {

    this.name = name;
  }

  /**
   * Returns the expected column header.
   * @return header
   */
  public String getHeader() {

    return header;
  }

  /**
   * Sets the expected column header.
   * @param header
   */
  public void setHeader(String header) {

    this.header = header;
  }

  /**
   * Returns the columns position.
   * @return position
   */
  public int getPosition() {

    return position;
  }
  
  void setInternalPosition(int position) {
    
    this.position = position;
  }
  
  /**
   * Sets the columns position.
   * The position of the leftmost column is 1, not 0.
   * @param position
   * @throws ConfigException
   */
  public void setPosition(int position) throws ConfigException {

    if (position < 1) {
      throw new ConfigException(text.get("invalidPosition"));
    }
    this.position = position;
  }

  /**
   * Returns the trim property.
   * If not specified,
   * we get the global trim property from <code>CsvConfig</code>
   * @return
   */
  public Boolean isTrim() {

    if (trim == null) {
      trim = csvConfig.getTrim();
    }
    return trim;
  }

  /**
   * Sets the trim property.
   * @param trim
   */
  public void setTrim(Boolean trim) {

    this.trim = trim;
  }

  /**
   * Returns the decimal point character.
   * If not specified,
   * we get the global character from <code>CsvConfig</code>.
   * @return <code>null</code>, point or comma.
   */
  public Character getDecimalChar() {

    if (decimalChar == null) {
      decimalChar = csvConfig.getDecimalChar();
    }
    return decimalChar;
  }

  /**
   * Sets the decimal point character.
   * The character may be a point or a comma.
   * <p>
   * If no character is explicitly defined,
   * both point and comma are assumed to
   * separate the integer from the fraction part of a decimal number.
   * If a character is specified, the alternate decimal point
   * character is assumed to be a grouping character and is 
   * removed before number parsing. 
   * @param decimalChar
   */
  public void setDecimalChar(Character decimalChar) {

    this.decimalChar = decimalChar;
  }

  /**
   * Returns the date format.
   * If not specified,
   * we get the global date format from <code>CsvConfig</code>.
   * @return date format
   */
  public SimpleDateFormat getDateFormat() {

    if (dateFormat == null) {
      dateFormat = csvConfig.getDateFormat();
    }
    return dateFormat;
  }

  /**
   * Sets the date pattern.
   * @param datePattern
   */
  public void setDatePattern(String datePattern) {

    this.dateFormat = new SimpleDateFormat(datePattern);
  }
  
  /**
   * Returns the constants representing the boolean value <code>true</code>.
   * If not specified,
   * we get the global constants from <code>CsvConfig</code>.
   * @return true values
   */
  public String[] getTrueValues() {

    if (trueValues == null) {
      trueValues = csvConfig.getTrueValues();
    }
    return trueValues;
  }

  /**
   * Sets the constants representing the boolean value <code>true</code>.
   * @param trueValues
   */
  public void setTrueValues(String[] trueValues) {
  
    this.trueValues = trueValues;
  }

  /**
   * Returns the column editor.
   * By default, there is no column editor.
   * @return column editor
   */
  public ColumnEditor getEditor() {
  
    return editor;
  }

  /**
   * Sets the column editor.
   * @param editor
   */
  public void setEditor(ColumnEditor editor) {
  
    this.editor = editor;
  }

  /**
   * Adds a replacement definition.
   * @param replace
   */
  public void addReplace(ReplaceConfig replace) {
    
    if (replaceList == null) {
      replaceList = new ArrayList<ReplaceConfig>();
    }
    replaceList.add(replace);
  }
  
  /**
   * Returns the list with replacement definitions.
   * If there are no replacement definitions, we get <code>null</code>.
   * @return list
   */
  public List<ReplaceConfig> getReplaceList() {
    
    return replaceList;
  }
}
