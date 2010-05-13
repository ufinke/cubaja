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
 * CSV column properties.
 * <p>
 * A defined column must have a name.
 * All other properties are optional.
 * <p>
 * When there is no explicit position,
 * the position is determined by the first matching header.
 * When there is no header,
 * the position is the position
 * of the previously defined column plus one.
 * The position of the first column is <tt>1</tt>, not <tt>0</tt> (for compatibility with JDBC).
 * <p>
 * XML attributes and subelements:
 * <blockquote>
 * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
 *   <tr bgcolor="#ccccff">
 *     <th align="left">Name</th>
 *     <th align="left">Description</th>
 *     <th align="center">A/E</th>
 *     <th align="center">M</th>
 *     <th align="center">U</th>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>name</tt></td>
 *     <td align="left" valign="top">the name of this column</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top">x</td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>header</tt></td>
 *     <td align="left" valign="top">the exact content of this column's header line</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>position</tt></td>
 *     <td align="left" valign="top">the position of this column, starting with <tt>1</tt> for the first column (default: position of the previous column plus one; or - in case the <tt>headerMatch</tt> feature of {@link CsvConfig#setHeaderMatch(boolean) CsvConfig} is enabled - derived from the column position of the first row which matches the header constant)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>trim</tt></td>
 *     <td align="left" valign="top">trim attribute for column content (default: global <tt>trim</tt> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>decimalChar</tt></td>
 *     <td align="left" valign="top">character for decimal point; may be a point or a comma (default: global <tt>decimalChar</tt> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>scale</tt></td>
 *     <td align="left" valign="top">number of fractional digits for decimal numbers (default: global <tt>scale</tt> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>datePattern</tt></td>
 *     <td align="left" valign="top">date format pattern as described in {@link java.text.SimpleDateFormat} (default: global <tt>datePattern</tt> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>trueValue</tt></td>
 *     <td align="left" valign="top">value representing boolean value <tt>true</tt> (default: global <tt>trueValue</tt> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>falseValue</tt></td>
 *     <td align="left" valign="top">value representing boolean value <tt>false</tt> (default: global <tt>falseValue</tt> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>nullValue</tt></td>
 *     <td align="left" valign="top">replacement for <tt>null</tt> (default: global <tt>nullValue</tt> attribute of {@link de.ufinke.cubaja.csv.CsvConfig CsvConfig})</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>editor</tt></td>
 *     <td align="left" valign="top">class name of a {@link de.ufinke.cubaja.csv.ColumnEditor ColumnEditor} implementation</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>replace</tt></td>
 *     <td align="left" valign="top">replacement constants (see {@link de.ufinke.cubaja.csv.ReplaceConfig ReplaceConfig})</td>
 *     <td align="center" valign="top">E</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top"> </td>
 *     </tr>
 * </table>
 * <tt>A/E</tt>: attribute or subelement
 * <br/>
 * <tt>M</tt>: mandatory
 * <br/>
 * <tt>U</tt>: unique
 * </blockquote>
 * </p>
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
   * Returns the parent <tt>CsvConfig</tt>.
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
   * @param name
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
   * @param header
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
   * @param position
   * @throws ConfigException
   */
  public void setPosition(int position) throws ConfigException {

    if (position < 1) {
      throw new ConfigException(text.get("invalidPosition"));
    }
    setInternalPosition(position);
  }

  void setInternalPosition(int position) {
    
    if (csvConfig != null) {
      sequence = csvConfig.getSequence(position); // last setting wins when there are position duplicates
    }    
    this.position = position;
  }
  
  int getSequence() {
    
    return sequence;
  }
  
  /**
   * Returns the trim property.
   * If not specified,
   * the global trim property from <tt>CsvConfig</tt> is returned.
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
   * @param trim
   */
  public void setTrim(Boolean trim) {

    this.trim = trim;
  }

  /**
   * Returns the decimal point character.
   * If not specified,
   * the global character from <tt>CsvConfig</tt> is returned.
   * @return <tt>null</tt>, point or comma.
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
   * the global date format from <tt>CsvConfig</tt> is returns.
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
   * @param datePattern
   */
  public void setDatePattern(String datePattern) {

    this.dateFormat = new SimpleDateFormat(datePattern);
  }
  
  /**
   * Returns the constant representing the boolean value <tt>true</tt>.
   * If not specified,
   * the global constant from <tt>CsvConfig</tt> is returned.
   * @return true value
   */
  public String getTrueValue() {

    if (trueValue == null && csvConfig != null) {
      return csvConfig.getTrueValue();
    }
    return trueValue;
  }

  /**
   * Sets the constant representing the boolean value <tt>true</tt>.
   * @param trueValue
   */
  public void setTrueValue(String trueValue) {
  
    this.trueValue = trueValue;
  }

  /**
   * Returns the constant representing the boolean value <tt>false</tt>.
   * If not specified,
   * the global constant from <tt>CsvConfig</tt> is returned.
   * @return false value
   */
  public String getFalseValue() {

    if (falseValue == null && csvConfig != null) {
      return csvConfig.getFalseValue();
    }
    return falseValue;
  }

  /**
   * Sets the constant representing the boolean value <tt>false</tt>.
   * @param falseValue
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
   * If there are no replacement definitions, the method returns <tt>null</tt>.
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
   * Default is <tt>2</tt>.
   * @param scale
   */
  public void setScale(Integer scale) {

    this.scale = scale;
  }
  
  /**
   * Retrieves the replacement for <tt>null</tt> values.
   * A {@link CsvReader} replaces the content of an empty column with this value.
   * A {@link CsvWriter} replaces <tt>null</tt> by this value. 
   * @return null value
   */
  public String getNullValue() {
    
    if (nullValue == null && csvConfig != null) {
      return csvConfig.getNullValue();
    }
    return nullValue;
  }
  
  /**
   * Sets the replacement value for <tt>null</tt>.
   * By default, <tt>null</tt> values remain <tt>null<tt>.
   * @param nullValue
   */
  public void setNullValue(String nullValue) {
    
    this.nullValue = nullValue;
  }

}
