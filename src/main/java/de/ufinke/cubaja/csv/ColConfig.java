// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.config.Mandatory;
import de.ufinke.cubaja.util.Text;

/**
 * <p>
 * CSV column properties.
 * </p><p>
 * A defined column must have a name.
 * All other properties are optional.
 * </p><p>
 * When there is no explicit position,
 * the position is determined by the first matching header.
 * When there is no header,
 * the position is the position
 * of the previously defined column plus one.
 * The position of the first column is <code>1</code>, not <code>0</code> (for compatibility with JDBC).
 * </p>
 * <table class="striped">
 * <caption style="text-align:left">XML attributes and subelements</caption>
 * <thead>
 * <tr>
 * <th scope="col" style="text-align:left">Name</th>
 * <th scope="col" style="text-align:left">Description</th>
 * <th scope="col" style="text-align:center">A/E</th>
 * <th scope="col" style="text-align:center">M</th>
 * <th scope="col" style="text-align:center">U</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>name</code></td>
 * <td style="text-align:left;vertical-align:top">the name of this column</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>header</code></td>
 * <td style="text-align:left;vertical-align:top">the exact content of this column's header line</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>position</code></td>
 * <td style="text-align:left;vertical-align:top">the position of this column, starting with <code>1</code> for the first column (default: position of the previous column plus one; or - in case the <code>headerMatch</code> feature of {@link CsvConfig#setHeaderMatch(boolean) CsvConfig} is enabled - derived from the column position of the first row which matches the header constant)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>trim</code></td>
 * <td style="text-align:left;vertical-align:top">trim attribute for column content (default: global <code>trim</code> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>decimalChar</code></td>
 * <td style="text-align:left;vertical-align:top">character for decimal point; may be a point or a comma (default: global <code>decimalChar</code> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>scale</code></td>
 * <td style="text-align:left;vertical-align:top">number of fractional digits for decimal numbers (default: global <code>scale</code> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>datePattern</code></td>
 * <td style="text-align:left;vertical-align:top">date format pattern as described in {@link java.text.SimpleDateFormat} (default: global <code>datePattern</code> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>trueValue</code></td>
 * <td style="text-align:left;vertical-align:top">value representing boolean value <code>true</code> (default: global <code>trueValue</code> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>falseValue</code></td>
 * <td style="text-align:left;vertical-align:top">value representing boolean value <code>false</code> (default: global <code>falseValue</code> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>nullValue</code></td>
 * <td style="text-align:left;vertical-align:top">replacement for <code>null</code> (default: global <code>nullValue</code> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>editor</code></td>
 * <td style="text-align:left;vertical-align:top">class name of a {@link de.ufinke.cubaja.csv.ColumnEditor ColumnEditor} implementation</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>replace</code></td>
 * <td style="text-align:left;vertical-align:top">replacement constants (see {@link de.ufinke.cubaja.csv.ReplaceConfig ReplaceConfig})</td>
 * <td style="text-align:center;vertical-align:top">E</td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * <td style="text-align:center;vertical-align:top"> </td>
 * </tr>
 * </tbody>
 * </table>
 * <code>A/E</code>: attribute or subelement
 * <br>
 * <code>M</code>: mandatory
 * <br>
 * <code>U</code>: unique
 * @author Uwe Finke
 */
public class ColConfig {

  static private final Text text = Text.getPackageInstance(ColConfig.class);
  
  private String name;
  private String header;
  private int position;
  private int sequence;
  private Boolean trim;
  private Character decimalChar;
  private Integer scale;
  private SimpleDateFormat dateFormat;
  private String trueValue;
  private String falseValue;
  private String nullValue;
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
   * Returns the parent <code>CsvConfig</code>.
   * @return config parent node
   */
  protected CsvConfig getParent() {
    
    return csvConfig;
  }

  /**
   * Returns the column's name.
   * @return name
   */
  public String getName() {

    return name;
  }

  /**
   * Sets the column's name.
   * @param name name of column
   */
  @Mandatory
  public void setName(String name) {

    if (csvConfig != null) {
      csvConfig.replaceName(name, this);
    }
    this.name = name;
  }

  /**
   * Returns the column header.
   * @return header
   */
  public String getHeader() {

    return header;
  }

  /**
   * Sets the column header.
   * @param header header text of column
   */
  public void setHeader(String header) {

    this.header = header;
  }

  /**
   * Returns the column's position.
   * @return position
   */
  public int getPosition() {

    return position;
  }
  
  /**
   * Sets the column's position.
   * The position of the leftmost column is 1, not 0.
   * @param position position of column
   * @throws ConfigException when position is less than 1
   */
  public void setPosition(int position) throws ConfigException {

    if (position < 1) {
      throw new ConfigException(text.get("invalidPosition"));
    }
    setInternalPosition(position);
  }

  void setInternalPosition(int position) {
    
    this.position = position;
  }

  void setSequence(int sequence) {
    
    this.sequence = sequence;
  }
  
  int getSequence() {
    
    return sequence;
  }
  
  /**
   * Returns the trim property.
   * If not specified,
   * the global trim property from <code>CsvConfig</code> is returned.
   * @return trim
   */
  public Boolean isTrim() {

    if (trim == null && csvConfig != null) {
      return csvConfig.getTrim();
    }
    return trim;
  }

  /**
   * Sets the trim property.
   * @param trim boolean wether to trim column content
   */
  public void setTrim(Boolean trim) {

    this.trim = trim;
  }

  /**
   * Returns the decimal point character.
   * If not specified,
   * the global character from <code>CsvConfig</code> is returned.
   * @return <code>null</code>, point or comma.
   */
  public Character getDecimalChar() {

    if (decimalChar == null && csvConfig != null) {
      return csvConfig.getDecimalChar();
    }
    return decimalChar;
  }
  
  Character getWriterDecimalChar() {
    
    if (decimalChar == null && csvConfig != null) {
      return csvConfig.getWriterDecimalChar();
    }
    return decimalChar;
  }

  /**
   * <p>
   * Sets the decimal point character.
   * The character may be a point or a comma.
   * </p><p>
   * If no character is explicitly defined,
   * both point and comma are assumed to
   * separate the integer from the fraction part of a decimal number.
   * If a character is specified, the alternate decimal point
   * character is assumed to be a grouping character and is 
   * removed before number parsing.
   * </p> 
   * @param decimalChar point or comma
   */
  public void setDecimalChar(Character decimalChar) {

    this.decimalChar = decimalChar;
  }

  /**
   * Returns the date format.
   * If not specified,
   * the global date format from <code>CsvConfig</code> is returns.
   * @return date format
   */
  public SimpleDateFormat getDateFormat() {

    if (dateFormat == null && csvConfig != null) {
      return csvConfig.getDateFormat();
    }
    return dateFormat;
  }

  /**
   * Sets the date pattern.
   * @param datePattern date pattern, for example <code>dd.MM.yyyy</code>
   */
  public void setDatePattern(String datePattern) {

    dateFormat = new SimpleDateFormat(datePattern);
    dateFormat.setLenient(false);
  }
  
  /**
   * Returns the constant representing the boolean value <code>true</code>.
   * If not specified,
   * the global constant from <code>CsvConfig</code> is returned.
   * @return true value
   */
  public String getTrueValue() {

    if (trueValue == null && csvConfig != null) {
      return csvConfig.getTrueValue();
    }
    return trueValue;
  }

  /**
   * Sets the constant representing the boolean value <code>true</code>.
   * @param trueValue alternate representation of <code>true</code>
   */
  public void setTrueValue(String trueValue) {
  
    this.trueValue = trueValue;
  }

  /**
   * Returns the constant representing the boolean value <code>false</code>.
   * If not specified,
   * the global constant from <code>CsvConfig</code> is returned.
   * @return false value
   */
  public String getFalseValue() {

    if (falseValue == null && csvConfig != null) {
      return csvConfig.getFalseValue();
    }
    return falseValue;
  }

  /**
   * Sets the constant representing the boolean value <code>false</code>.
   * @param falseValue alternate representation of <code>false</code>
   */
  public void setFalseValue(String falseValue) {
  
    this.falseValue = falseValue;
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
   * @param editor column editor
   */
  public void setEditor(ColumnEditor editor) {
  
    this.editor = editor;
  }

  /**
   * Adds a replacement definition.
   * @param replace replacement configuration
   */
  public void addReplace(ReplaceConfig replace) {
    
    if (replaceList == null) {
      replaceList = new ArrayList<ReplaceConfig>();
    }
    replaceList.add(replace);
  }
  
  /**
   * Returns the list with replacement definitions.
   * If there are no replacement definitions, the method returns <code>null</code>.
   * @return list
   */
  public List<ReplaceConfig> getReplaceList() {
    
    return replaceList;
  }
  
  /**
   * Returns the number of fractional digits for decimal numbers.
   * @return scale
   */
  public Integer getScale() {

    if (scale == null && csvConfig != null) {
      return csvConfig.getScale();
    }
    return scale;
  }

  /**
   * Sets the number of fractional digits for decimal numbers.
   * Default is <code>2</code>.
   * @param scale number of fractional digits
   */
  public void setScale(Integer scale) {

    this.scale = scale;
  }
  
  /**
   * Retrieves the replacement for <code>null</code> values.
   * A {@link CsvReader} replaces the content of an empty column with this value.
   * A {@link CsvWriter} replaces <code>null</code> by this value. 
   * @return null value
   */
  public String getNullValue() {
    
    if (nullValue == null && csvConfig != null) {
      return csvConfig.getNullValue();
    }
    return nullValue;
  }
  
  /**
   * Sets the replacement value for <code>null</code>.
   * By default, <code>null</code> values remain <code>null</code>.
   * @param nullValue alternate representation of <code>null</code>
   */
  public void setNullValue(String nullValue) {
    
    this.nullValue = nullValue;
  }

}
