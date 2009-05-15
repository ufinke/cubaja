// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.io.*;
import de.ufinke.cubaja.util.Text;

/**
 * CSV reader.
 * <p>
 * We retrieve rows in a loop calling <code>nextRow</code>
 * until we receive <code>false</code>.
 * <p>
 * For every row, we can read column contents as the type we need in our application,
 * or we can create a data object with column data by using method <code>readObject</code>.
 * When the column is empty, the read methods for numeric primitive types
 * return <code>0</code>; read methods for objects types 
 * (except <code>readObject</code>) return <code>null</code>.
 * <p>
 * The position of the first column is 1, not 0.
 * <p>
 * The first row is read automatically if the configurations
 * <code>hasHeaderRow</code> method returns <code>true</code>.
 * In this case, column positions are determined automatically
 * when the column configuration contains a header definition.
 * Despite the automatism, we can retrieve the content of the header
 * row before we call <code>nextRow</code> the first time. 
 * <p>
 * Most methods may throw a <code>CsvException</code>.
 * An exception is thrown if there is an attempt to
 * read any data after a call to <code>nextRow</code>
 * returned <code>false</code>, or after the reader was closed.
 * @author Uwe Finke
 */
public class CsvReader implements ColumnReader {

  static private final Text text = new Text(CsvReader.class);
  
  private CsvConfig config;
  private Reader in;
  private int rowCount;
  
  private List<ColConfig> columnList;
  private Map<String, Integer> nameMap;
  private RowParser parser;
  private RowFilter rowFilter;
  private ErrorHandler errorHandler;
  
  private boolean eof;
  
  private String row;
  private int currentIndex;    
  private ColConfig colConfig;
  
  /**
   * Constructor with configuration.
   * When using this constructor,
   * we have to set the configurations file property.
   * @param config
   * @throws IOException
   * @throws ConfigException
   * @throws CsvException
   */
  public CsvReader(CsvConfig config) throws IOException, ConfigException, CsvException {
    
    this(config.createReader(), config);
  }

  /**
   * Constructor with implicit default configuration.
   * With this constructor, we have no defined columns.
   * @param reader
   * @throws CsvException
   */
  public CsvReader(Reader reader) throws IOException, CsvException {
  
    this(reader, new CsvConfig());
  }
  
  /**
   * Constructor with reader and configuration.
   * @param reader
   * @param config
   * @throws CsvException
   */
  public CsvReader(Reader reader, CsvConfig config) throws IOException, CsvException {
  
    this.config = config;
    in = reader;

    columnList = config.getColumnList();
    
    parser = config.getParser();
    parser.init(in, config);
    
    rowFilter = config.getRowFilter();
    errorHandler = new DefaultErrorHandler();
    
    readHeaderRow();
    initPositions();
  }
  
  private void readHeaderRow() throws IOException, CsvException {
    
    if (! config.hasHeaderRow()) {
      return;
    }
    
    if (! nextRow()) {
      return;
    }
    
    rowCount--;
    
    String[] headers = readColumns();
    
    if (config.getHeader() != null && config.getHeader().booleanValue()) {
      for (String header : headers) {
        config.addCol(createAutoHeaderName(header), header);
      }
    }
    
    Map<String, Integer> headerMap = new HashMap<String, Integer>();
    for (int i = headers.length - 1; i >= 0; i--) { // backward because on duplicate headers we prefer the leftmost column
      headerMap.put(headers[i], i + 1);
    }
    
    for (ColConfig col : columnList) {
      if (col.getPosition() == 0 && col.getHeader() != null) {
        Integer position = headerMap.get(col.getHeader());
        if (position == null) {
          throw new CsvException(text.get("headerNotFound", col.getHeader()), parser.getLineCount(), 0, row);
        }
        col.setInternalPosition(position);
      }
    }
  }
  
  private String createAutoHeaderName(String header) {
    
    char[] buffer = new char[header.length()];
    
    for (int i = 0; i < buffer.length; i++) {
      char c = header.charAt(i);
      if (Character.isJavaIdentifierPart(c)) {
        buffer[i] = Character.toLowerCase(c);
      } else {
        buffer[i] = '_';
      }
    }
    
    return String.valueOf(buffer);
  }
  
  private void initPositions() {
    
    nameMap = new HashMap<String, Integer>();
    
    int nextPosition = 0; // first list entry is dummy / default
    
    for (ColConfig col : columnList) {
      
      int colPosition = col.getPosition();
      if (colPosition == 0) {
        col.setInternalPosition(nextPosition);
      }
      nextPosition = col.getPosition() + 1;

      if (! col.isDummyColumn()) {
        nameMap.put(col.getName(), col.getPosition());
      }      
    }
  }
  
  /**
   * Sets the error handler.
   * @param errorHandler
   */
  public void setErrorHandler(ErrorHandler errorHandler) {
    
    if (errorHandler == null) {
      errorHandler = new DefaultErrorHandler();
    }
    this.errorHandler = errorHandler;
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#getRowCount()
   */
  public int getRowCount() {
    
    return rowCount;
  }
  
  /**
   * Returns the number of raw lines read so far.
   * @return line number
   */
  public int getLineCount() {
    
    return parser.getLineCount();
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#close()
   */
  public void close() throws IOException {
    
    in.close();
    in = null;
    eof = true;
  }
  
  private void checkEOF() throws CsvException {
    
    if (eof) {
      throw new CsvException("eof");
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#nextRow()
   */
  public boolean nextRow() throws IOException, CsvException {
    
    boolean accepted = false;
    
    while (! accepted) {
      rowCount++;
      row = parser.readRow();
      eof = (row == null);
      if (eof) {
        accepted = true;
      } else {
        accepted = (rowFilter == null) ? true : rowFilter.acceptRow(this);
      }
    }
    
    return ! eof;
  }
  
  /**
   * Returns whether the retrieved row is empty.
   * A row is assumed to be empty when all valid column data have zero length.
   * @return flag
   * @throws CsvException
   */
  public boolean isEmptyRow() throws CsvException {

    checkEOF();
    return parser.isEmptyRow();
  }
  
  /**
   * Returns the complete last retrieved row.
   * @return row
   * @throws CsvException
   */
  public String getRow() throws CsvException {
    
    checkEOF();
    return row;
  }
  
  /**
   * Sets a column editor.
   * @param columnName
   * @param editor
   */
  public void setColumnEditor(String columnName, ColumnEditor editor) throws CsvException {
    
    setColumnEditor(getColumnPosition(columnName), editor);
  }
  
  /**
   * Sets a column editor.
   * @param columnPosition
   * @param editor
   */
  public void setColumnEditor(int columnPosition, ColumnEditor editor) {
    
    columnList.get(columnPosition).setEditor(editor);
  }
  
  /**
   * Sets a row filter.
   * @param rowFilter
   */
  public void setRowFilter(RowFilter rowFilter) {
    
    config.setRowFilter(rowFilter);
  }
  
  private String getColumn(int index) throws CsvException {

    checkEOF();
    
    currentIndex = index;
    
    String s = (index < 1 || index > parser.getColumnCount()) ? "" : parser.getColumn(index);
    
    int configIndex = (index < 1 || index >= columnList.size()) ? 0 : index;
    colConfig = columnList.get(configIndex);
    
    if (colConfig.isTrim()) {
      s = s.trim();
    }
    
    List<ReplaceConfig> replaceList = colConfig.getReplaceList();
    if (replaceList != null) {
      for (ReplaceConfig replace : replaceList) {
        s = s.replaceAll(replace.getRegex(), replace.getReplacement());
      }
    }
    
    if (colConfig.getEditor() != null) {
      s = colConfig.getEditor().editColumn(s, colConfig);
    }
    
    return s;
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#getColumnPosition(java.lang.String)
   */
  public int getColumnPosition(String columnName) throws CsvException {
    
    Integer index = nameMap.get(columnName);
    if (index == null) {
      throw new CsvException(text.get("undefinedName", columnName));
    }
    return index; 
  }
  
  private void handleParseError(Throwable cause, String value, String type) throws CsvException {

    CsvException error = new CsvException(text.get("parseError", value, type), cause, getLineCount(), getRowCount(), row, currentIndex, colConfig.getName(), value);
    errorHandler.handleError(error);
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readColumns()
   */
  public String[] readColumns() throws CsvException {
    
    int count = parser.getColumnCount();
    String[] col = new String[count];
    
    int i = 0;
    while (i < count) {
      col[i] = getColumn(++i);
    }
    
    return col;
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#getColumnCount()
   */
  public int getColumnCount() throws CsvException {
    
    checkEOF();
    return parser.getColumnCount();
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readString(java.lang.String)
   */
  public String readString(String columnName) throws CsvException {
    
    return readString(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readString(int)
   */
  public String readString(int columnPosition) throws CsvException {

    return getColumn(columnPosition);
  }
  
  private boolean getBoolean(String s) {
    
    String[] values = colConfig.getTrueValues();
    if (values == null) {
      return false;
    }
    
    int limit = values.length;
    for (int i = 0; i < limit; i++) {
      if (values[i].equals(s)) {
        return true;
      }
    }
    return false;
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readBoolean(java.lang.String)
   */
  public boolean readBoolean(String columnName) throws CsvException {
    
    return readBoolean(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readBoolean(int)
   */
  public boolean readBoolean(int columnPosition) throws CsvException {
    
    return getBoolean(getColumn(columnPosition).trim());
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readBooleanObject(java.lang.String)
   */
  public Boolean readBooleanObject(String columnName) throws CsvException {
    
    return readBooleanObject(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readBooleanObject(int)
   */
  public Boolean readBooleanObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();    
    return (s.length() == 0) ? null : Boolean.valueOf(getBoolean(s));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readByte(java.lang.String)
   */
  public byte readByte(String columnName) throws CsvException {
    
    return readByte(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readByte(int)
   */
  public byte readByte(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return 0;
    }
    try {
      return Byte.parseByte(s);
    } catch (Exception e) {
      handleParseError(e, s, "byte");
      return 0;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readByteObject(java.lang.String)
   */
  public Byte readByteObject(String columnName) throws CsvException {
    
    return readByteObject(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readByteObject(int)
   */
  public Byte readByteObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    try {
      return Byte.valueOf(s);
    } catch (Exception e) {
      handleParseError(e, s, "byte");
      return null;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readShort(java.lang.String)
   */
  public short readShort(String columnName) throws CsvException {
    
    return readShort(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readShort(int)
   */
  public short readShort(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return 0;
    }
    try {
      return Short.parseShort(s);
    } catch (Exception e) {
      handleParseError(e, s, "short");
      return 0;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readShortObject(java.lang.String)
   */
  public Short readShortObject(String columnName) throws CsvException {
    
    return readShortObject(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readShortObject(int)
   */
  public Short readShortObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    try {
      return Short.valueOf(s);
    } catch (Exception e) {
      handleParseError(e, s, "short");
      return null;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readChar(java.lang.String)
   */
  public char readChar(String columnName) throws CsvException {
    
    return readChar(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readChar(int)
   */
  public char readChar(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return 0;
    }
    return s.charAt(0);
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readCharObject(java.lang.String)
   */
  public Character readCharObject(String columnName) throws CsvException {
    
    return readCharObject(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readCharObject(int)
   */
  public Character readCharObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    return Character.valueOf(s.charAt(0));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readInt(java.lang.String)
   */
  public int readInt(String columnName) throws CsvException {
    
    return readInt(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readInt(int)
   */
  public int readInt(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return 0;
    }
    try {
      return Integer.parseInt(s);
    } catch (Exception e) {
      handleParseError(e, s, "int");
      return 0;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readIntObject(java.lang.String)
   */
  public Integer readIntObject(String columnName) throws CsvException {
    
    return readIntObject(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readIntObject(int)
   */
  public Integer readIntObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    try {
      return Integer.valueOf(s);
    } catch (Exception e) {
      handleParseError(e, s, "int");
      return null;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readLong(java.lang.String)
   */
  public long readLong(String columnName) throws CsvException {
    
    return readLong(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readLong(int)
   */
  public long readLong(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return 0;
    }
    try {
      return Long.parseLong(s);
    } catch (Exception e) {
      handleParseError(e, s, "long");
      return 0;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readLongObject(java.lang.String)
   */
  public Long readLongObject(String columnName) throws CsvException {
    
    return readLongObject(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readLongObject(int)
   */
  public Long readLongObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    try {
      return Long.valueOf(s);
    } catch (Exception e) {
      handleParseError(e, s, "long");
      return null;
    }
  }
  
  private String prepareDecimalString(String s) {
  
    Character decimalChar = colConfig.getDecimalChar();
    char dc = (decimalChar == null) ? 0 : decimalChar.charValue();
    
    StringBuilder sb = new StringBuilder(s.length());
    int limit = s.length();
    for (int i = 0; i < limit; i++) {
      char c = s.charAt(i);
      switch (c) {
        case '.':
          if (dc != ',') {
            sb.append('.');
          }
          break;
        case ',':
          if (dc != '.') {
            sb.append('.');
          }
          break;
        default:
          sb.append(c);
      }
    }
    
    return sb.toString();
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readFloat(java.lang.String)
   */
  public float readFloat(String columnName) throws CsvException {
    
    return readFloat(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readFloat(int)
   */
  public float readFloat(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return 0;
    }
    try {
      return Float.parseFloat(prepareDecimalString(s));
    } catch (Exception e) {
      handleParseError(e, s, "float");
      return 0;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readFloatObject(java.lang.String)
   */
  public Float readFloatObject(String columnName) throws CsvException {
    
    return readFloatObject(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readFloatObject(int)
   */
  public Float readFloatObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    try {
      return Float.valueOf(prepareDecimalString(s));
    } catch (Exception e) {
      handleParseError(e, s, "float");
      return null;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readDouble(java.lang.String)
   */
  public double readDouble(String columnName) throws CsvException {
    
    return readDouble(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readDouble(int)
   */
  public double readDouble(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return 0;
    }
    try {
      return Double.parseDouble(prepareDecimalString(s));
    } catch (Exception e) {
      handleParseError(e, s, "double");
      return 0;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readDoubleObject(java.lang.String)
   */
  public Double readDoubleObject(String columnName) throws CsvException {
    
    return readDoubleObject(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readDoubleObject(int)
   */
  public Double readDoubleObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    try {
      return Double.valueOf(prepareDecimalString(s));
    } catch (Exception e) {
      handleParseError(e, s, "double");
      return null;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readBigDecimal(java.lang.String)
   */
  public BigDecimal readBigDecimal(String columnName) throws CsvException {
    
    return readBigDecimal(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readBigDecimal(int)
   */
  public BigDecimal readBigDecimal(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    try {      
      return new BigDecimal(prepareDecimalString(s));
    } catch (Exception e) {
      handleParseError(e, s, "BigDecimal");
      return null;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readBigInteger(java.lang.String)
   */
  public BigInteger readBigInteger(String columnName) throws CsvException {
    
    return readBigInteger(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readBigInteger(int)
   */
  public BigInteger readBigInteger(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    try {      
      return new BigInteger(s);
    } catch (Exception e) {
      handleParseError(e, s, "BigInteger");
      return null;
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readDate(java.lang.String)
   */
  public Date readDate(String columnName) throws CsvException {
    
    return readDate(getColumnPosition(columnName));
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readDate(int)
   */
  public Date readDate(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    try {      
      return colConfig.getDateFormat().parse(s);
    } catch (Exception e) {
      handleParseError(e, s, "Date");
      return null;
    }
  }

  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readEnum(java.lang.String, java.lang.Class)
   */
  public <E extends Enum<E>> E readEnum(String columnName, Class<E> clazz) throws CsvException {
    
    return readEnum(getColumnPosition(columnName), clazz);
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readEnum(int, java.lang.Class)
   */
  public <E extends Enum<E>> E readEnum(int columnPosition, Class<E> clazz) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    try {
      char c = s.charAt(0);
      if (c >= 0 && c <= 9) {
        return clazz.getEnumConstants()[readInt(columnPosition)];
      } else {
        try {
          return Enum.valueOf(clazz, s);
        } catch (Exception e) {
          return Enum.valueOf(clazz, s.toUpperCase());
        }
      }
    } catch (Exception e) {
      handleParseError(e, s, clazz.getName());
      return null;
    }
  }

  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readObject(java.lang.Class)
   */
  @SuppressWarnings("unchecked")
  public <D> D readObject(Class<? extends D> clazz) throws CsvException {
    
    try {
      return (D) ObjectFactoryManager.getFactory(clazz, nameMap).createObject(this);
    } catch (Exception e) {
      throw new CsvException(text.get("readObject", clazz.getName()), e);
    }
  }
  
  /* (non-Javadoc)
   * @see de.ufinke.cubaja.csv.ColumnReader#readAllRows(java.lang.Class)
   */
  public <D> Iterable<D> readAllRows(Class<? extends D> clazz) {
    
    return new RowIterator<D>(this, clazz);
  }

}
