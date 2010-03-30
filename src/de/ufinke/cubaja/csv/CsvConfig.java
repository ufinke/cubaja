// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.DecimalFormat;
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
 * <td align="left" valign="top">values representing boolean <tt>true</tt>
 * (default: <tt>true</tt>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>falseValue</tt></td>
 * <td align="left" valign="top">values representing boolean <tt>false</tt>
 * (default: <tt>false</tt>)</td>
 * <td align="center" valign="top">A</td>
 * <td align="center" valign="top"></td>
 * <td align="center" valign="top">x</td>
 * </tr>
 * <tr bgcolor="#eeeeff">
 * <td align="left" valign="top"><tt>header</tt></td>
 * <td align="left" valign="top">see description of method
 * {@link de.ufinke.cubaja.csv.CsvConfig#setHeader(java.lang.Boolean) setHeader}
 * </td>
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
   * Creates a <tt>Reader</tt>. This method is called by the
   * <tt>CsvReader</tt> constructor without a <tt>Reader</tt> parameter.
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
   * Creates a <tt>Writer</tt>. This method is called by the
   * <tt>CsvWriter</tt> constructor without a <tt>Writer</tt> parameter.
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
   * Returns the parser. By default, this is a <tt>DefaultRowParser</tt>.
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
        writerDecimalChar = new DecimalFormat().getDecimalFormatSymbols().getDecimalSeparator();
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
   * Returns whether the CSV input has a header row. This is <tt>true</tt>
   * when we set the header attribute explicitly to <tt>true</tt>, or when
   * we define a header property on at least one column and the header attribute
   * is not set explicitly to <tt>false</tt>.
   * 
   * @return flag
   */
  public boolean hasHeaderRow() {

    return (header == null) ? headerDefined : header.booleanValue();
  }

  /**
   * Returns the explicitly set header attribute. May be <tt>null</tt> if
   * the attribute has not been set.
   * 
   * @return header attribute
   */
  public Boolean getHeader() {

    return header;
  }

  /**
   * Signals whether the CSV source or target has a header row.
   * <p>
   * When this attribute is set to <tt>true</tt>, the first input row 
   * read by <tt>CsvReader</tt> is
   * used to define all (or additional) columns. The name attribute of the
   * automatically defined columns is derived from the column content. For the
   * name attribute, all non-identifier characters (that is,
   * <tt>Character.isJavaIdentifierPart</tt> returns <tt>false</tt> for
   * this character) are replaced by an underscore. The header attribute of
   * those columns is the original column content. If the generated name matches
   * an already existing column name, this column is not added automatically.
   * <p>
   * When this attribute is set to <tt>false</tt>, the first row is not
   * processed as header row even if some <tt>col</tt> definitions contain a
   * <tt>header</tt> attribute.
   * <p>
   * When this attribute isn't set at all, the first row is considered to be
   * a header row if any <tt>col</tt> definition has a <tt>header</tt>
   * attribute.
   * <p>
   * A <tt>CsvWriter</tt> automatically writes a header row
   * if this attribute is set to <tt>true</tt>.
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
   * <tt>DefaultRowFormatter</tt>.
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
   * Default is <tt>2</tt>.
   * @param scale
   */
  public void setScale(Integer scale) {

    this.scale = scale;
  }

}
