// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.io.FileConfig;
import de.ufinke.cubaja.util.Text;
import de.ufinke.cubaja.util.Util;

/**
 * Global configuration properties.
 * <p>
 * XML attributes and subelements: <blockquote>
 * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
 * <tr bgcolor="#ccccff">
 * <th align="left">Name</th>
 * <th align="left">Description</th>
 * <th align="center">A/E</th>
 * <th align="center">M</th>
 * <th align="center">U</th>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>file</tt></td>
 * <td align="left" valign="top">file name; mandatory if {@link CsvReader}
 * constructor without <tt>Reader</tt> 
 * or {@link CsvWriter} constructor without <tt>Writer</tt> parameter is used</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>charset</tt></td>
 * <td align="left" valign="top">character set name</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>separator</tt></td>
 * <td align="left" valign="top">character which separates columns (default: tab
 * [<tt>x'09'</tt>])</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>escapeChar</tt></td>
 * <td align="left" valign="top">character which delimits text containing
 * separator characters (default: there is no escape character)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>parser</tt></td>
 * <td align="left" valign="top">class name of a
 * {@link de.ufinke.cubaja.csv.RowParser RowParser} implementation (default:
 * {@link de.ufinke.cubaja.csv.DefaultRowParser DefaultRowParser})</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>trim</tt></td>
 * <td align="left" valign="top">global trim attribute for column content
 * (default: <tt>false</tt>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>decimalChar</tt></td>
 * <td align="left" valign="top">global character for decimal point; may be a
 * point or a comma (default for parsing: both point and comma are decimalChars; 
 * default for formatting: depends on <tt>Locale</tt>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>scale</tt></td>
 * <td align="left" valign="top">maximum number of fractional digits in formatted decimal numbers
 * (default: 2)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>datePattern</tt></td>
 * <td align="left" valign="top">global date format pattern as described in
 * {@link java.text.SimpleDateFormat} (default: <tt>yyyy-MM-dd</tt>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>trueValue</tt></td>
 * <td align="left" valign="top">value representing boolean <tt>true</tt>
 * (default: <tt>true</tt>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>falseValue</tt></td>
 * <td align="left" valign="top">value representing boolean <tt>false</tt>
 * (default: <tt>false</tt>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>nullValue</tt></td>
 * <td align="left" valign="top">replacement for <tt>null</tt> (default: empty column is <tt>null</tt>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>header</tt></td>
 * <td align="left" valign="top">flag whether there is a header row (default: <tt>false</tt>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>autoCol</tt></td>
 * <td align="left" valign="top">flag whether there is a header row
 * and columns shall be automatically created according to the header row's content
 * (default: <tt>false</tt>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>headerMatch</tt></td>
 * <td align="left" valign="top">flag whether there is a header row
 * and column positions depend on the position of their defined header text within the header row
 * (default: <tt>false</tt>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>rowFilter</tt></td>
 * <td align="left" valign="top">class name of a
 * {@link de.ufinke.cubaja.csv.RowFilter RowFilter} implementation</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>formatter</tt></td>
 * <td align="left" valign="top">class name of a
 * {@link de.ufinke.cubaja.csv.RowFormatter RowFormatter} implementation
 * (default: {@link de.ufinke.cubaja.csv.DefaultRowFormatter
 * DefaultRowFormatter})</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>rowSeparator</tt></td>
 * <td align="left" valign="top">separator between rows (lines) used by
 * <tt>CsvWriter</tt> (default: platform dependent JVM default)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>col</tt></td>
 * <td align="left" valign="top">column definition (see
 * {@link de.ufinke.cubaja.csv.ColConfig ColConfig})</td>
 * <td align="center" valign="top">E</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top"></td>
 * </tr>
 * </table>
 * <tt>A/E</tt>: attribute or subelement <br/>
 * <tt>M</tt>: mandatory <br/>
 * <tt>U</tt>: unique </blockquote>
 * </p>
 * 
 * @author Uwe Finke
 */
public class CsvConfig {

  static private final Text text = Text.getPackageInstance(CsvConfig.class);

  private String fileName;
  private String charset;
  private FileConfig fileConfig;

  private Character separator;
  private Character escapeChar;
  private RowParser parser;
  private RowFilter rowFilter;

  private Boolean trim;
  private Character decimalChar;
  private Character writerDecimalChar;
  private SimpleDateFormat dateFormat;
  private String trueValue;
  private String falseValue;
  private String nullValue;
  private Integer scale;

  private boolean header;
  private boolean autoCol;
  private boolean headerMatch;

  private RowFormatter formatter;
  private String rowSeparator;

  private ColConfig defaultColConfig;
  private ColConfig[] positionArray;
  private Map<String, ColConfig> nameMap;
  private List<ColConfig> columnList;
  private int lastPosition;
  private int sequence;

  /**
   * Constructor.
   */
  public CsvConfig() {

    nameMap = new HashMap<String, ColConfig>();
    columnList = new ArrayList<ColConfig>();
    setDefaultColConfig(new ColConfig());
  }
  
  /**
   * Sets a customized default column configuration.
   * A standard default column configuration is created by the constructor.
   * @param defaultColConfig
   */
  public void setDefaultColConfig(ColConfig defaultColConfig) {
    
    defaultColConfig.setCsvConfig(this);
    defaultColConfig.setInternalPosition(0);
    this.defaultColConfig = defaultColConfig;
  }

  /**
   * Returns a column configuration for a column identified by name.
   * The result is <tt>null</tt> if there is no column with the given name.
   * 
   * @param columnName
   * @return column config
   */
  public ColConfig getColConfig(String columnName) {

    return nameMap.get(columnName);
  }

  /**
   * Returns a column configuration for a column identified by position.
   * Returns the default column configuration if there is no column with the given index.
   * The position property of the default column is <tt>0</tt>.
   * 
   * @param position
   * @return column config
   */
  public ColConfig getColConfig(int position) {

    if (positionArray == null) {
      buildPositionArray();
    }
    
    if (position < 0 || position >= positionArray.length) {
      return defaultColConfig;
    }
    
    ColConfig result = positionArray[position];
    return (result == null) ? defaultColConfig : result;
  }
  
  private void buildPositionArray() {

    int maxPosition = 0;
    for (ColConfig col : columnList) {
      maxPosition = Math.max(maxPosition, col.getPosition());
    }
    
    positionArray = new ColConfig[maxPosition + 1];
    for (ColConfig col : getColumnList()) {
      positionArray[col.getPosition()] = col;
    }
  }
  
  Map<String, ColConfig> getNameMap() {
    
    return nameMap;
  }

  /**
   * Returns the position of a column identified by name.
   * 
   * @param columnName
   * @return position
   * @throws CsvException
   *         if name does not exist
   */
  public int getColumnPosition(String columnName) throws CsvException {

    ColConfig col = getColConfig(columnName);
    if (col == null) {
      throw new CsvException(text.get("undefinedName", columnName));
    }
    return col.getPosition();
  }

  /**
   * Sets the file name.
   * 
   * @param fileName
   */
  public void setFile(String fileName) {

    this.fileName = fileName;
    fileConfig = null;
  }

  /**
   * Sets the charset.
   * 
   * @param charset
   */
  public void setCharset(String charset) {

    this.charset = charset;
  }

  /**
   * Returns a <tt>FileConfig</tt>.
   * @return file config
   * @throws ConfigException
   *         if the <tt>file</tt> attribute is not set
   */
  public FileConfig getFile() throws ConfigException {
    
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
    
    return fileConfig;
  }

  /**
   * Sets the column separator character.
   * 
   * @param separator
   */
  public void setSeparator(Character separator) {

    this.separator = separator;
  }

  /**
   * Returns the column separator character. The default separator is the tab
   * character (<tt>\t</tt>).
   * 
   * @return separator
   */
  public Character getSeparator() {

    if (separator == null) {
      separator = Character.valueOf('\t');
    }
    return separator;
  }

  /**
   * Sets the character which delimits the column content. Typically, this is
   * the quote character.
   * 
   * @param escapeChar
   */
  public void setEscapeChar(Character escapeChar) {

    this.escapeChar = escapeChar;
  }

  /**
   * Returns the column content delimiter character. By default, no such
   * character is defined.
   * 
   * @return delimiter char
   */
  public Character getEscapeChar() {

    return escapeChar;
  }

  /**
   * Sets the parser which separates columns.
   * 
   * @param parser
   */
  public void setParser(RowParser parser) {

    this.parser = parser;
  }

  /**
   * Returns the parser. By default, this is a {@link DefaultRowParser}.
   * 
   * @return parser
   */
  public RowParser getParser() {

    if (parser == null) {
      parser = new DefaultRowParser();
    }
    return parser;
  }

  /**
   * Returns the global decimal point character. By default, both point and
   * comma are decimal point characters.
   * <p>
   * For <tt>CsvWriter</tt>, the default decimal point character 
   * depends on the default <tt>Locale</tt>.
   * 
   * @return decimal point charcter
   */
  public Character getDecimalChar() {

    return decimalChar;
  }
  
  Character getWriterDecimalChar() {
    
    if (writerDecimalChar == null) {
      if (decimalChar == null) {
        writerDecimalChar = new DecimalFormat().getDecimalFormatSymbols().getDecimalSeparator();
      } else {
        writerDecimalChar = decimalChar;
      }
    }
    return writerDecimalChar;
  }

  /**
   * Sets the global decimal point character.
   * Should be point or comma; other values may lead to unpredictable results.
   * 
   * @param decimalChar
   */
  public void setDecimalChar(Character decimalChar) {

    this.decimalChar = decimalChar;
  }

  /**
   * Sets the global date pattern.
   * 
   * @param datePattern
   */
  public void setDatePattern(String datePattern) {

    dateFormat = new SimpleDateFormat(datePattern);
    dateFormat.setLenient(false);
  }

  /**
   * Returns the global date format. 
   * The default pattern is <tt>yyyy-MM-dd</tt>.
   * 
   * @return date format
   */
  public SimpleDateFormat getDateFormat() {

    if (dateFormat == null) {
      setDatePattern("yyyy-MM-dd");
    }
    return dateFormat;
  }

  /**
   * Returns the global trim property. When set, column content is trimmed before
   * further processing. Note that on read operations which parse numbers, the
   * content is always trimmed. By default, the trim property is
   * <tt>false</tt>.
   * 
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
   * 
   * @param trim
   */
  public void setTrim(Boolean trim) {

    this.trim = trim;
  }

  /**
   * Tells whether the CSV input has a header row. This is <tt>true</tt>
   * if any of the {@link #setHeader(boolean) header}, the
   * {@link #setAutoCol(boolean) autoCol} 
   * or the {@link #setHeaderMatch(boolean) headerMatch} properties is <tt>true</tt>.
   * 
   * @return flag
   */
  public boolean hasHeaderRow() {

    return header || autoCol || headerMatch;
  }

  /**
   * Signals whether the CSV source or target has a header row.
   * <p>
   * When this attribute is set to <tt>true</tt>, 
   * a <tt>CsvReader</tt> does not treat the first row's content as data,
   * and a <tt>CsvWriter</tt> will automatically write a header row.
   * 
   * @param header
   */
  public void setHeader(boolean header) {

    this.header = header;
  }
  
  /**
   * Sets the <tt>autoCol</tt> property.
   * <p>
   * If set to <tt>true</tt>, a <tt>CsvReader</tt> will
   * configure columns automatically. The column names and positions are 
   * derived from the header row's content.
   * Within a derived column name, non-Java characters are replaced by underlines.
   * The derived column name is in lower case letters.
   * <p>
   * If a <tt>ColConfig</tt> with the same name has been already defined,
   * it will not be replaced.
   * <p>
   * An automatically added <tt>ColConfig</tt> instance is of the same class
   * as the {@link #setDefaultColConfig(ColConfig) default column}.
   * 
   * @param autoCol
   */
  public void setAutoCol(boolean autoCol) {
    
    this.autoCol = autoCol;
  }
  
  /**
   * Returns the <tt>autoCol</tt> property.
   * 
   * @return flag
   */
  public boolean isAutoCol() {
    
    return autoCol;
  }
  
  void addAutoCol(String header, int position) throws CsvException {
    
    header = header.toLowerCase();
    
    char[] buffer = new char[header.length()];
    for (int i = 0; i < buffer.length; i++) {
      char c = header.charAt(i);
      buffer[i] = Character.isJavaIdentifierPart(c) ? c : '_';
    }
    String name = String.valueOf(buffer);
    
    ColConfig col = nameMap.get(name);
    if (col != null) {
      return;
    }
    
    try {
      col = defaultColConfig.getClass().newInstance();
    } catch (Exception e) {
      throw new CsvException(text.get("createAutoCol"), e);
    }
    col.setName(name);
    col.setHeader(header);
    col.setInternalPosition(position);
    
    addCol(col);
  }

  /**
   * Sets the <tt>headerMatch</tt> property.
   * If set to <tt>true</tt>, a <tt>CsvReader</tt> will
   * identify the columns' position by header text.
   * If there is any column with a {@link ColConfig#setHeader(String) header} property
   * and that header text is not found within the header row,
   * an exception will be thrown.
   * 
   * @param headerMatch
   */
  public void setHeaderMatch(boolean headerMatch) {
    
    this.headerMatch = headerMatch;
  }
  
  /**
   * Returns the <tt>headerMatch</tt> property.
   * 
   * @return flag
   */
  public boolean isHeaderMatch() {
    
    return headerMatch;
  }

  /**
   * Returns the row filter. By default, there is no filter.
   * 
   * @return row filter
   */
  public RowFilter getRowFilter() {

    return rowFilter;
  }

  /**
   * Sets a row filter.
   * 
   * @param rowFilter
   */
  public void setRowFilter(RowFilter rowFilter) {

    this.rowFilter = rowFilter;
  }

  /**
   * Adds a column definition.
   * 
   * @param column
   */
  public void addCol(ColConfig column) {

    column.setCsvConfig(this);

    if (column.getPosition() == 0) {
      column.setInternalPosition(lastPosition + 1);
    }
    lastPosition = column.getPosition();
    column.setSequence(++sequence);
    positionArray = null;
    
    columnList.add(column);
    nameMap.put(column.getName(), column);
  }
  
  void replaceName(String newName, ColConfig column) {
    
    nameMap.remove(column.getName());
    nameMap.put(newName, column);
  }
  
  /**
   * Returns a list of columns.
   * The list is sorted by the column's position.
   * 
   * @return list
   */
  public List<ColConfig> getColumnList() {

    Comparator<ColConfig> comparator = new Comparator<ColConfig>() {
      
      public int compare(ColConfig a, ColConfig b) {
        
        int result = Util.compare(a.getPosition(), b.getPosition());
        if (result == 0) {
          result = Util.compare(a.getSequence(), b.getSequence());
        }
        return result;
      }
    };
    
    Collections.sort(columnList, comparator);
    
    return columnList;
  }
  
  /**
   * Returns the formatter for <tt>CsvWriter</tt> output.
   * 
   * @return formatter
   */
  public RowFormatter getFormatter() {

    if (formatter == null) {
      formatter = new DefaultRowFormatter();
    }
    return formatter;
  }

  /**
   * Sets the formatter for <tt>CsvWriter</tt> output. Default is
   * {@link DefaultRowFormatter}.
   * 
   * @param formatter
   */
  public void setFormatter(RowFormatter formatter) {

    this.formatter = formatter;
  }

  /**
   * Returns the row separator needed for <tt>CsvWriter</tt>.
   * 
   * @return row separator character(s)
   */
  public String getRowSeparator() {

    if (rowSeparator == null) {
      rowSeparator = System.getProperty("line.separator");
    }
    return rowSeparator;
  }

  /**
   * Sets the row separator needed for <tt>CsvWriter</tt>. Default is the
   * platform dependent separator returned by
   * <tt>System.getProperty("line.separator")</tt>.
   * 
   * @param rowSeparator
   */
  public void setRowSeparator(String rowSeparator) {

    this.rowSeparator = rowSeparator;
  }

  /**
   * Retrieves the representation of boolean value <tt>true</tt>.
   * 
   * @return true value
   */
  public String getTrueValue() {

    if (trueValue == null) {
      trueValue = "true";
    }
    return trueValue;
  }

  /**
   * Sets the representation of boolean value <tt>true</tt>. Default is
   * <tt>true</tt>.
   * 
   * @param trueValue
   */
  public void setTrueValue(String trueValue) {

    this.trueValue = trueValue;
  }

  /**
   * Retrieves the representation of boolean value <tt>false</tt>.
   * 
   * @return false value
   */
  public String getFalseValue() {

    if (falseValue == null) {
      falseValue = "false";
    }
    return falseValue;
  }

  /**
   * Sets the representation of boolean value <tt>false</tt>.
   * 
   * @param falseValue
   */
  public void setFalseValue(String falseValue) {

    this.falseValue = falseValue;
  }
  
  /**
   * Retrieves the replacement for <tt>null</tt> values.
   * A {@link CsvReader} replaces the content of an empty column with this value.
   * A {@link CsvWriter} replaces <tt>null</tt> by this value. 
   * @return null value
   */
  public String getNullValue() {
    
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

  /**
   * Returns the global number of fractional digits for decimal numbers.
   * 
   * @return scale
   */
  public Integer getScale() {

    if (scale == null) {
      scale = new Integer(2);
    }
    return scale;
  }

  /**
   * Sets the global number of fractional digits for decimal numbers.
   * Default is <tt>2</tt>.
   * 
   * @param scale
   */
  public void setScale(Integer scale) {

    this.scale = scale;
  }

}
