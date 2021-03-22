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
 * <p>
 * Global configuration properties.
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
 * <td style="text-align:left;vertical-align:top"><code>file</code></td>
 * <td style="text-align:left;vertical-align:top">file name; mandatory if {@link CsvReader} constructor without <code>Reader</code> or {@link CsvWriter} constructor without <code>Writer</code> parameter is used</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>charset</code></td>
 * <td style="text-align:left;vertical-align:top">character set name</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>separator</code></td>
 * <td style="text-align:left;vertical-align:top">character which separates columns (default: tab [<code>x'09'</code>])</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>escapeChar</code></td>
 * <td style="text-align:left;vertical-align:top">character which delimits text containing
 * separator characters (default: there is no escape character)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>parser</code></td>
 * <td style="text-align:left;vertical-align:top">class name of a {@link RowParser} implementation (default: {@link DefaultRowParser})</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>trim</code></td>
 * <td style="text-align:left;vertical-align:top">global trim attribute for column content (default: <code>false</code>)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>decimalChar</code></td>
 * <td style="text-align:left;vertical-align:top">global character for decimal point; may be a point or a comma (default for parsing: both point and comma are decimalChars;  default for formatting: depends on <code>Locale</code>)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>scale</code></td>
 * <td style="text-align:left;vertical-align:top">maximum number of fractional digits in formatted decimal numbers (default: 2)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>datePattern</code></td>
 * <td style="text-align:left;vertical-align:top">global date format pattern as described in {@link java.text.SimpleDateFormat} (default: <code>yyyy-MM-dd</code>)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>trueValue</code></td>
 * <td style="text-align:left;vertical-align:top">value representing boolean <code>true</code> (default: <code>true</code>)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>falseValue</code></td>
 * <td style="text-align:left;vertical-align:top">value representing boolean <code>false</code>
 * (default: <code>false</code>)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>nullValue</code></td>
 * <td style="text-align:left;vertical-align:top">replacement for <code>null</code> (default: empty column is <code>null</code>)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>header</code></td>
 * <td style="text-align:left;vertical-align:top">flag whether there is a header row (default: <code>false</code>)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>autoCol</code></td>
 * <td style="text-align:left;vertical-align:top">flag whether there is a header row and columns shall be automatically created according to the header row's content (default: <code>false</code>)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>headerMatch</code></td>
 * <td style="text-align:left;vertical-align:top">flag whether there is a header row and column positions depend on the position of their defined header text within the header row (default: <code>false</code>)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>rowFilter</code></td>
 * <td style="text-align:left;vertical-align:top">class name of a {@link RowFilter} implementation</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>formatter</code></td>
 * <td style="text-align:left;vertical-align:top">class name of a {@link RowFormatter} implementation (default: {@link DefaultRowFormatter})</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>rowSeparator</code></td>
 * <td style="text-align:left;vertical-align:top">separator between rows (lines) used by <code>CsvWriter</code> (default: platform dependent JVM default)</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>col</code></td>
 * <td style="text-align:left;vertical-align:top">column definition (see {@link ColConfig})</td>
 * <td style="text-align:center;vertical-align:top">E</td>
 * <td style="text-align:center;vertical-align:top"></td>
 * <td style="text-align:center;vertical-align:top"></td>
 * </tr>
 * </tbody>
 * </table>
 * <p>
 * <code>A/E</code>: attribute or subelement <br>
 * <code>M</code>: mandatory <br>
 * <code>U</code>: unique
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
   * @param defaultColConfig config with default values
   */
  public void setDefaultColConfig(ColConfig defaultColConfig) {
    
    defaultColConfig.setCsvConfig(this);
    defaultColConfig.setInternalPosition(0);
    this.defaultColConfig = defaultColConfig;
  }

  /**
   * Returns a column configuration for a column identified by name.
   * The result is <code>null</code> if there is no column with the given name.
   * 
   * @param columnName name of column
   * @return column config
   */
  public ColConfig getColConfig(String columnName) {

    return nameMap.get(columnName);
  }

  /**
   * Returns a column configuration for a column identified by position.
   * Returns the default column configuration if there is no column with the given index.
   * Position count starts with <code>1</code>; the position property of the default column is <code>0</code>.
   * 
   * @param position position of column
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
   * @param columnName name of column
   * @return position position of column. Position count starts with <code>1</code>
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
   * @param fileName file path and name
   */
  public void setFile(String fileName) {

    this.fileName = fileName;
    fileConfig = null;
  }

  /**
   * Sets the charset.
   * 
   * @param charset charset name
   */
  public void setCharset(String charset) {

    this.charset = charset;
  }

  /**
   * Returns a <code>FileConfig</code>.
   * @return file config
   * @throws ConfigException
   *         if the <code>file</code> attribute is not set
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
   * @param separator column separator
   */
  public void setSeparator(Character separator) {

    this.separator = separator;
  }

  /**
   * Returns the column separator character. The default separator is the tab
   * character (<code>\t</code>).
   * 
   * @return separator column separator
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
   * @param escapeChar column content delimiter
   */
  public void setEscapeChar(Character escapeChar) {

    this.escapeChar = escapeChar;
  }

  /**
   * Returns the column content delimiter character. By default, no such
   * character is defined.
   * 
   * @return column content delimiter char
   */
  public Character getEscapeChar() {

    return escapeChar;
  }

  /**
   * Sets the parser which separates columns.
   * 
   * @param parser parser instance
   */
  public void setParser(RowParser parser) {

    this.parser = parser;
  }

  /**
   * Returns the parser. By default, this is a {@link DefaultRowParser}.
   * 
   * @return parser instance
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
   * For <code>CsvWriter</code>, the default decimal point character 
   * depends on the default <code>Locale</code>.
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
   * @param decimalChar point or comma as decimal separator
   */
  public void setDecimalChar(Character decimalChar) {

    this.decimalChar = decimalChar;
  }

  /**
   * Sets the global date pattern.
   * 
   * @param datePattern date pattern. Example: <code>dd.MM.yyyy</code>
   */
  public void setDatePattern(String datePattern) {

    dateFormat = new SimpleDateFormat(datePattern);
    dateFormat.setLenient(false);
  }

  /**
   * Returns the global date format. 
   * The default pattern is <code>yyyy-MM-dd</code>.
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
   * <code>false</code>.
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
   * @param trim flag wether to trim column content
   */
  public void setTrim(Boolean trim) {

    this.trim = trim;
  }

  /**
   * Tells whether the CSV input has a header row. This is <code>true</code>
   * if any of the {@link #setHeader(boolean) header}, the
   * {@link #setAutoCol(boolean) autoCol} 
   * or the {@link #setHeaderMatch(boolean) headerMatch} properties is <code>true</code>.
   * 
   * @return flag
   */
  public boolean hasHeaderRow() {

    return header || autoCol || headerMatch;
  }

  /**
   * Signals whether the CSV source or target has a header row.
   * <p>
   * When this attribute is set to <code>true</code>, 
   * a <code>CsvReader</code> does not treat the first row's content as data,
   * and a <code>CsvWriter</code> will automatically write a header row.
   * </p>
   * 
   * @param header flag wether there is a header row
   */
  public void setHeader(boolean header) {

    this.header = header;
  }
  
  /**
   * Sets the <code>autoCol</code> property.
   * <p>
   * If set to <code>true</code>, a <code>CsvReader</code> will
   * configure columns automatically. The column names and positions are 
   * derived from the header row's content.
   * Within a derived column name, non-Java characters are replaced by underlines.
   * The derived column name is in lower case letters.
   * </p><p>
   * If a <code>ColConfig</code> with the same name has been already defined,
   * it will not be replaced.
   * </p><p>
   * An automatically added <code>ColConfig</code> instance is of the same class
   * as the {@link #setDefaultColConfig(ColConfig) default column}.
   * </p>
   * @param autoCol flag wether there is a header row with column names
   */
  public void setAutoCol(boolean autoCol) {
    
    this.autoCol = autoCol;
  }
  
  /**
   * Returns the <code>autoCol</code> property.
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
   * Sets the <code>headerMatch</code> property.
   * If set to <code>true</code>, a <code>CsvReader</code> will
   * identify the columns' position by header text.
   * If there is any column with a {@link ColConfig#setHeader(String) header} property
   * and that header text is not found within the header row,
   * an exception will be thrown.
   * 
   * @param headerMatch flag wether columns are identified by text in header row
   */
  public void setHeaderMatch(boolean headerMatch) {
    
    this.headerMatch = headerMatch;
  }
  
  /**
   * Returns the <code>headerMatch</code> property.
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
   * @param rowFilter row filter implementation
   */
  public void setRowFilter(RowFilter rowFilter) {

    this.rowFilter = rowFilter;
  }

  /**
   * Adds a column definition.
   * 
   * @param column config of new column
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
   * Returns the formatter for <code>CsvWriter</code> output.
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
   * Sets the formatter for <code>CsvWriter</code> output. Default is
   * {@link DefaultRowFormatter}.
   * 
   * @param formatter formatter implementation
   */
  public void setFormatter(RowFormatter formatter) {

    this.formatter = formatter;
  }

  /**
   * Returns the row separator needed for <code>CsvWriter</code>.
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
   * Sets the row separator needed for <code>CsvWriter</code>. Default is the
   * platform dependent separator returned by
   * <code>System.getProperty("line.separator")</code>.
   * 
   * @param rowSeparator line separator
   */
  public void setRowSeparator(String rowSeparator) {

    this.rowSeparator = rowSeparator;
  }

  /**
   * Retrieves the representation of boolean value <code>true</code>.
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
   * Sets the representation of boolean value <code>true</code>. Default is
   * <code>true</code>.
   * 
   * @param trueValue text representation of <code>true</code>
   */
  public void setTrueValue(String trueValue) {

    this.trueValue = trueValue;
  }

  /**
   * Retrieves the representation of boolean value <code>false</code>.
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
   * Sets the representation of boolean value <code>false</code>.
   * 
   * @param falseValue text representation of <code>false</code>
   */
  public void setFalseValue(String falseValue) {

    this.falseValue = falseValue;
  }
  
  /**
   * Retrieves the replacement for <code>null</code> values.
   * A {@link CsvReader} replaces the content of an empty column with this value.
   * A {@link CsvWriter} replaces <code>null</code> by this value. 
   * @return null value
   */
  public String getNullValue() {
    
    return nullValue;
  }
  
  /**
   * Sets the replacement value for <code>null</code>.
   * By default, <code>null</code> values remains <code>null</code> (nothing).
   * @param nullValue alternative <code>null</code> representation 
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
      scale = Integer.valueOf(2);
    }
    return scale;
  }

  /**
   * Sets the global number of fractional digits for decimal numbers.
   * Default is <code>2</code>.
   * 
   * @param scale scale of decimal numbers
   */
  public void setScale(Integer scale) {

    this.scale = scale;
  }

}
