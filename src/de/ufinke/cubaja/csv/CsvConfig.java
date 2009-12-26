// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.io.FileConfig;
import de.ufinke.cubaja.util.Text;

/**
 * Global <code>CsvReader</code> properties.
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
 * <td align="left" valign="top"><code>file</code></td>
 * <td align="left" valign="top">file name; mandatory if <code>CsvReader</code>
 * constructor without <code>Reader</code> parameter is used</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>charset</code></td>
 * <td align="left" valign="top">character set name</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>separator</code></td>
 * <td align="left" valign="top">character which separates columns (default: tab
 * [<code>x'09'</code>])</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>escapeChar</code></td>
 * <td align="left" valign="top">character which delimits text containing
 * separator characters (default: there is no escape character)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>parser</code></td>
 * <td align="left" valign="top">class name of a
 * {@link de.ufinke.cubaja.csv.RowParser RowParser} implementation (default:
 * {@link de.ufinke.cubaja.csv.DefaultRowParser DefaultRowParser})</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>trim</code></td>
 * <td align="left" valign="top">global trim attribute for column content
 * (default: <code>false</code>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>decimalChar</code></td>
 * <td align="left" valign="top">global character for decimal point; may be a
 * point or a comma (default: both point and comma are decimalChars)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>scale</code></td>
 * <td align="left" valign="top">number of fractional digits for decimal numbers
 * (default: 2)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>datePattern</code></td>
 * <td align="left" valign="top">global date format pattern as described in
 * <code>java.text.SimpleDateFormat</code> (default: <code>yyyy-MM-dd</code>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>trueValue</code></td>
 * <td align="left" valign="top">values representing boolean <code>true</code>
 * (default: <code>true</code>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>falseValue</code></td>
 * <td align="left" valign="top">values representing boolean <code>false</code>
 * (default: <code>false</code>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>header</code></td>
 * <td align="left" valign="top">see description of method
 * {@link de.ufinke.cubaja.csv.CsvConfig#setHeader(java.lang.Boolean) setHeader}
 * </td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>rowFilter</code></td>
 * <td align="left" valign="top">class name of a
 * {@link de.ufinke.cubaja.csv.RowFilter RowFilter} implementation</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>formatter</code></td>
 * <td align="left" valign="top">class name of a
 * {@link de.ufinke.cubaja.csv.RowFormatter RowFormatter} implementation
 * (default: {@link de.ufinke.cubaja.csv.DefaultRowFormatter
 * DefaultRowFormatter})</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>rowSeparator</code></td>
 * <td align="left" valign="top">separator between rows (lines) used by
 * <code>CsvWriter</code> (default: platform dependent JVM default)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><code>col</code></td>
 * <td align="left" valign="top">column definition (see
 * {@link de.ufinke.cubaja.csv.ColConfig ColConfig})</td>
 * <td align="center" valign="top">E</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top"></td>
 * </tr>
 * </table>
 * <code>A/E</code>: attribute or subelement <br/>
 * <code>M</code>: mandatory <br/>
 * <code>U</code>: unique </blockquote>
 * </p>
 * 
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
  private Character writerDecimalChar;
  private SimpleDateFormat dateFormat;
  private String trueValue;
  private String falseValue;
  private Integer scale;

  private Boolean header;

  private RowFormatter formatter;
  private String rowSeparator;

  private List<ColConfig> columnList;
  private Map<String, Integer> nameMap;
  private Set<String> columnSet;
  private boolean headerDefined;

  /**
   * Constructor.
   */
  public CsvConfig() {

    columnList = new ArrayList<ColConfig>();
    addCol(new ColConfig(true)); // dummy column; positions start with 1
  }

  void initPositions() {

    nameMap = new HashMap<String, Integer>();

    boolean mustRearrange = false;

    int nextPosition = 0; // first list entry is dummy / default

    for (ColConfig col : columnList) {

      int colPosition = col.getPosition();
      if (colPosition == 0) {
        col.setInternalPosition(nextPosition);
      } else if (colPosition != nextPosition) {
        mustRearrange = true;
      }
      nextPosition = col.getPosition() + 1;

      if (!col.isDummyColumn()) {
        nameMap.put(col.getName(), col.getPosition());
      }
    }

    if (mustRearrange) {
      rearrange();
    }

    columnSet = null;
  }

  private void rearrange() {

    Map<Integer, ColConfig> map = new HashMap<Integer, ColConfig>();

    for (ColConfig col : columnList) {
      map.put(col.getPosition(), col);
    }

    List<Integer> posList = new ArrayList<Integer>(map.keySet());
    Collections.sort(posList);

    List<ColConfig> newList = new ArrayList<ColConfig>();
    int expected = 0;
    for (int key : posList) {
      while (expected < key) {
        newList.add(columnList.get(0));
        expected++;
      }
      newList.add(columnList.get(key));
    }

    columnList.clear();
    columnList.addAll(newList);
  }

  /**
   * Returns a column configuration for a column identified by name.
   * 
   * @param columnName
   * @return column config
   * @throws CsvException
   */
  public ColConfig getColConfig(String columnName) throws CsvException {

    return getColConfig(getColumnPosition(columnName));
  }

  /**
   * Returns a column configuration for a column identified by position.
   * 
   * @param index
   * @return column config
   */
  public ColConfig getColConfig(int index) {

    int configIndex = (index < 1 || index >= columnList.size()) ? 0 : index;
    return columnList.get(configIndex);
  }

  Map<String, Integer> getNameMap() {

    return nameMap;
  }

  /**
   * Returns the position of a column identified by name.
   * 
   * @param columnName
   * @return position
   * @throws CsvException
   *         if name doesn't exist
   */
  public int getColumnPosition(String columnName) throws CsvException {

    Integer index = nameMap.get(columnName);
    if (index == null) {
      throw new CsvException(text.get("undefinedName", columnName));
    }
    return index;
  }

  /**
   * Sets the file name.
   * 
   * @param fileName
   */
  public void setFile(String fileName) {

    this.fileName = fileName;
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
   * Creates a <code>Reader</code>. This method is called by the
   * <code>CsvReader</code> constructor without a <code>Reader</code> parameter.
   * If we use this constructor, the file name must have been set. The charset
   * property is also used if specified.
   * 
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
   * Creates a <code>Writer</code>. This method is called by the
   * <code>CsvWriter</code> constructor without a <code>Writer</code> parameter.
   * If we use this constructor, the file name must have been set. The charset
   * property is also used if specified.
   * 
   * @return writer
   * @throws IOException
   * @throws ConfigException
   */
  public Writer createWriter() throws IOException, ConfigException {

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

    return fileConfig.createWriter();
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
   * character (<code>\t</code>).
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
   * Returns the parser. By default, this is a <code>DefaultRowParser</code>.
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
   * 
   * @return decimal point charcter
   */
  public Character getDecimalChar() {

    return decimalChar;
  }
  
  Character getWriterDecimalChar() {
    
    if (writerDecimalChar == null) {
      if (decimalChar == null) {
        writerDecimalChar = DecimalFormatSymbols.getInstance().getDecimalSeparator();
      } else {
        writerDecimalChar = decimalChar;
      }
    }
    return writerDecimalChar;
  }

  /**
   * Sets the decimal point character.
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
  }

  /**
   * Returns the global date format. By default, the date format depends on the
   * localized package properties.
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
   * Returns the trim property. When set, column content is trimmed before
   * further processing. Note that on read operations that require parsing the
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
   * @param trim
   */
  public void setTrim(Boolean trim) {

    this.trim = trim;
  }

  /**
   * Returns whether the CSV input has a header row. This is <code>true</code>
   * when we set the header attribute explicitly to <code>true</code>, or when
   * we define a header property on at least one column and the header attribute
   * is not set explicitly to <code>false</code>.
   * 
   * @return flag
   */
  public boolean hasHeaderRow() {

    return (header == null) ? headerDefined : header.booleanValue();
  }

  /**
   * Returns the explicitly set header attribute. May be <code>null</code> if
   * the attribute has not been set.
   * 
   * @return header attribute
   */
  public Boolean getHeader() {

    return header;
  }

  /**
   * Signals whether the CSV input has a header row.
   * <p>
   * When we set this attribute to <code>true</code>, the first input row is
   * used to define all (or additional) columns. The name attribute of the
   * automatically defined columns is derived from the column content. For the
   * name attribute, all non-identifier characters (that is,
   * <code>Character.isJavaIdentifierPart</code> returns <code>false</code> for
   * this character) are replaced by an underscore. The header attribute of
   * those columns is the original column content. If the generated name matches
   * an already existing column name, this column is not added automatically.
   * <p>
   * When we set this attribute to <code>false</code> the first row is not
   * processed as header row even if some <code>col</code> definitions contain a
   * <code>header</code> attribute.
   * <p>
   * When we don't set this attribute at all, the first row is considered to be
   * a header row if any <code>col</code> definition has a <code>header</code>
   * attribute.
   * 
   * @param header
   */
  public void setHeader(Boolean header) {

    this.header = header;
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
    headerDefined |= (column.getHeader() != null);
    columnList.add(column);
  }

  void addCol(String name, String header) {

    if (columnSet == null) {
      columnSet = new HashSet<String>();
      for (ColConfig col : columnList) {
        columnSet.add(col.getName());
      }
    }

    if (columnSet.contains(name)) {
      return;
    }

    ColConfig col = new ColConfig();
    col.setName(name);
    col.setHeader(header);
    addCol(col);

    columnSet.add(name);
  }

  /**
   * Returns the list of defined columns.
   * 
   * @return list
   */
  public List<ColConfig> getColumnList() {

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
   * <code>DefaultRowFormatter</code>.
   * 
   * @param formatter
   */
  public void setFormatter(RowFormatter formatter) {

    this.formatter = formatter;
  }

  /**
   * Returns the row separator needed for <code>CsvWriter</code>.
   * 
   * @return
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
   * @param rowSeparator
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
   * @param trueValue
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
   * @param falseValue
   */
  public void setFalseValue(String falseValue) {

    this.falseValue = falseValue;
  }

  /**
   * Returns the number of fractional digits for decimal numbers.
   * @return scale
   */
  public Integer getScale() {

    if (scale == null) {
      scale = new Integer(2);
    }
    return scale;
  }

  /**
   * Sets the number of fractional digits for decimal numbers.
   * Default is <code>2</code>.
   * @param scale
   */
  public void setScale(Integer scale) {

    this.scale = scale;
  }

}
