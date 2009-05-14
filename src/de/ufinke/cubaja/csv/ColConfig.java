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
 *     <td align="left" valign="top"><code>name</code></td>
 *     <td align="left" valign="top">the name of this column</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top">x</td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>header</code></td>
 *     <td align="left" valign="top">the exact content of this columns header line</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>position</code></td>
 *     <td align="left" valign="top">the position of this column; the position of the first column is 1 (default: position of the previous column plus one; or - in case the <code>header</code> attribute is defined - derived from the column position of the first row which matches the header constant)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>trim</code></td>
 *     <td align="left" valign="top">trim attribute for column content (default: global <code>trim</code> attribute of {@link de.ufinke.cubaja.CsvConfig CsvConfig})</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>decimalChar</code></td>
 *     <td align="left" valign="top">character for decimal point; may be a point or a comma (default: global <code>decimalChar</code> attribute of {@link de.ufinke.cubaja.CsvConfig CsvConfig})</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>datePattern</code></td>
 *     <td align="left" valign="top">date format pattern as described in <code>java.text.SimpleDateFormat</code> (default: global <code>datePattern</code> attribute of {@link de.ufinke.cubaja.CsvConfig CsvConfig})</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>trueValues</code></td>
 *     <td align="left" valign="top">comma-separated list of values which are interpreted as <code>true</code> (default: global <code>trueValues</code> attribute of {@link de.ufinke.cubaja.CsvConfig CsvConfig})</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>editor</code></td>
 *     <td align="left" valign="top">class name of a {@link de.ufinke.cubaja.csv.ColumnEditor ColumnEditor} implementation</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>replace</code></td>
 *     <td align="left" valign="top">replacement constants (see {@link de.ufinke.cubaja.csv.ReplaceConfig ReplaceConfig})</td>
 *     <td align="center" valign="top">E</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top"> </td>
 *     </tr>
 * </table>
 * <code>A/E</code>: attribute or subelement
 * <br/>
 * <code>M</code>: mandatory
 * <br/>
 * <code>U</code>: unique
 * </blockquote>
 * </p>
 * @author Uwe Finke
 */
public class ColConfig {

  static private final Text text = new Text(ColConfig.class);
  
  private boolean dummyColumn;
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
  
  ColConfig(boolean dummyColumn) {
    
    this.dummyColumn = dummyColumn;
  }
  
  boolean isDummyColumn() {
    
    return dummyColumn;
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
   * @return trim
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
