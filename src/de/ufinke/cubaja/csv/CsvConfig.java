// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.io.FileConfig;
import de.ufinke.cubaja.util.Text;

/**
 * Global <code>CsvReader</code> properties.
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
 *     <td align="left" valign="top"><code>file</code></td>
 *     <td align="left" valign="top">file name</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>charset</code></td>
 *     <td align="left" valign="top">character set name</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>separator</code></td>
 *     <td align="left" valign="top">character which separates columns (default: tab [<code>x'09'</code>])</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>escapeChar</code></td>
 *     <td align="left" valign="top">character which delimits text containing separator characters (default: there is no escape character)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>parser</code></td>
 *     <td align="left" valign="top">class name of a <code>RowParser</code> implementation (default: <code>DefaultRowParser</code>; can handle separators and escape characters as defined in <code>tools.ietf.org/html/rfc4180</code>)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>trim</code></td>
 *     <td align="left" valign="top">global trim attribute for column content (default: <code>false</code>)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>decimalChar</code></td>
 *     <td align="left" valign="top">character for decimal point; may be a point or a comma (default: both point and comma are decimalChars)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>datePattern</code></td>
 *     <td align="left" valign="top">date format pattern as described in <code>java.text.SimpleDateFormat</code> (default: depends on locale properties; <code>dd.MM.yyyy</code> for german, <code>yyyy-MM-dd</code> for other locales)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>trueValues</code></td>
 *     <td align="left" valign="top">comma-separated list of values which are interpreted as <code>true</code> (default: <code>true,TRUE,1,y,Y,x,X</code> and variants depending on locale properties)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>header</code></td>
 *     <td align="left" valign="top">flag (<code>true</code> or <code>false</code>) which marks the first row as header row (default: <code>true</code> if at least one column defines a <code>header</code> attribute, <code>false</code> otherwise)</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>rowFilter</code></td>
 *     <td align="left" valign="top">class name of a <code>RowFilter</code> implementation</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>col</code></td>
 *     <td align="left" valign="top">column definition (see <code>ColConfig</code>)</td>
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
public class CsvConfig {

  static private final Text text = new Text(CsvConfig.class);

  private String fileName;
  private String charset;
  private FileConfig fileConfig;

  private Character separator;
  private Character escapeChar;
  private RowParser parser;
  private RowFilter rowFilter;

  private Boolean trim;
  private Character decimalChar;
  private SimpleDateFormat dateFormat;
  private String[] trueValues;

  private Boolean header;
  
  private List<ColConfig> columnList;
  private boolean headerDefined;

  /**
   * Constructor.
   */
  public CsvConfig() {

    columnList = new ArrayList<ColConfig>();
    addCol(new ColConfig(true)); // dummy column; positions start with 1
  }

  /**
   * Sets the file name.
   * @param fileName
   */
  public void setFile(String fileName) {

    this.fileName = fileName;
  }

  /**
   * Sets the charset.
   * @param charset
   */
  public void setCharset(String charset) {

    this.charset = charset;
  }

  /**
   * Creates a <code>Reader</code>.
   * This method is called by the <code>CsvReader</code> constructor
   * without a <code>Reader</code> parameter.
   * If we use this constructor, the file name must have been set.
   * The charset property is also used if specified.
   * @return reader
   * @throws IOException
   * @throws ConfigException
   */
  public Reader createReader() throws IOException, ConfigException {

    if (fileConfig == null) {
      if (fileName == null) {
        throw new ConfigException(text.get("noFileName"));
      }
      fileConfig = new FileConfig();
      fileConfig.setName(fileName);
      if (charset != null) {
        fileConfig.setCharset(charset);
      }
    }

    return fileConfig.createReader();
  }

  /**
   * Sets the column separator character.
   * @param separator
   */
  public void setSeparator(Character separator) {

    this.separator = separator;
  }

  /**
   * Returns the column separator character.
   * The default separator is the tab character (<code>\t</code>).
   * @return separator
   */
  public Character getSeparator() {

    if (separator == null) {
      separator = Character.valueOf('\t');
    }
    return separator;
  }

  /**
   * Sets the character which delimits the column content.
   * Typically, this is the quote character.
   * @param escapeChar
   */
  public void setEscapeChar(Character escapeChar) {

    this.escapeChar = escapeChar;
  }

  /**
   * Returns the column content delimiter character.
   * By default, no such character is defined.
   * @return delimiter char
   */
  public Character getEscapeChar() {

    return escapeChar;
  }

  /**
   * Sets the parser which separates columns.
   * @param parser
   */
  public void setParser(RowParser parser) {

    this.parser = parser;
  }

  /**
   * Returns the parser.
   * By default, this is a <code>DefaultRowParser</code>.
   * @return parser
   */
  public RowParser getParser() {

    if (parser == null) {
      parser = new DefaultRowParser();
    }
    return parser;
  }
  
  /**
   * Returns the global decimal point character.
   * By default, both point and comma are decimal point characters.
   * @return decimal point charcter
   */
  public Character getDecimalChar() {
  
    return decimalChar;
  }

  /**
   * Sets the decimal point character.
   * @param decimalChar
   */
  public void setDecimalChar(Character decimalChar) {
  
    this.decimalChar = decimalChar;
  }

  /**
   * Sets the global date pattern.
   * @param datePattern
   */
  public void setDatePattern(String datePattern) {

    dateFormat = new SimpleDateFormat(datePattern);
  }

  /**
   * Returns the global date format.
   * By default, the date format depends on the localized package properties. 
   * @return date format
   */
  public SimpleDateFormat getDateFormat() {

    if (dateFormat == null) {
      setDatePattern(text.get("datePattern"));
    }
    return dateFormat;
  }

  /**
   * Returns the trim property.
   * When set, column content is trimmed before further processing.
   * Note that on read operations that require parsing the
   * content is always trimmed.
   * By default, the trim property is <code>false</code>.
   * @return trim propery
   */
  public Boolean getTrim() {

    if (trim == null) {
      trim = Boolean.FALSE;
    }
    return trim;
  }

  /**
   * Sets the global trim property.
   * @param trim
   */
  public void setTrim(Boolean trim) {

    this.trim = trim;
  }
  
  /**
   * Returns the global constants representing the boolean value <code>true</code>.
   * They depend on the localized package properties.
   * @return trueValues
   */
  public String[] getTrueValues() {

    if (trueValues == null) {
      trueValues = text.get("trueValues").split(",");
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
   * Returns whether the CSV input has a header row.
   * This is <code>true</code> when
   * we set the header flag explicitly,
   * or when we define a header property on at least one column.  
   * @return flag
   */
  public boolean hasHeaderRow() {

    if (header == null) {
      setHeader(headerDefined);
    }
    return header.booleanValue();
  }

  /**
   * Signals whether the CSV input has a header row.
   * @param header
   */
  public void setHeader(Boolean header) {

    this.header = header;
  }

  /**
   * Returns the row filter.
   * By default, there is no filter.
   * @return row filter
   */
  public RowFilter getRowFilter() {
  
    return rowFilter;
  }
  
  /**
   * Sets a row filter.
   * @param rowFilter
   */
  public void setRowFilter(RowFilter rowFilter) {
  
    this.rowFilter = rowFilter;
  }

  /**
   * Adds a column definition.
   * @param column
   */
  public void addCol(ColConfig column) {
    
    column.setCsvConfig(this);        
    headerDefined |= (column.getHeader() != null);
    columnList.add(column);
  }
  
  /**
   * Returns the list of defined columns.
   * @return list
   */
  public List<ColConfig> getColumnList() {
    
    return columnList;
  }
  
}
