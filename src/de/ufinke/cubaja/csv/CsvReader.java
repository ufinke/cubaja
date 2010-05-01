// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
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
import de.ufinke.cubaja.util.ColumnReader;
import de.ufinke.cubaja.util.NoSuchEnumException;
import de.ufinke.cubaja.util.RowIterator;
import de.ufinke.cubaja.util.Text;
import de.ufinke.cubaja.util.Util;

/**
 * CSV reader.
 * <p>
 * Rows are read in a loop by calling {@link #nextRow nextRow}
 * until the result is <tt>false</tt>.
 * <p>
 * For every row, the column's content may be read as the type needed by the application.
 * Alternatively, a complete row may be read as data object with method {@link #readRow readRow}.
 * An even more convenient way to read a complete CSV file is
 * the {@link #cursor cursor} method, which combines
 * the call to <tt>nextRow</tt> and <tt>readRow</tt> within an automatic loop.
 * <p>
 * When a column is empty, the read methods for numeric primitive types
 * return <tt>0</tt>; read methods for objects types return <tt>null</tt>.
 * <p>
 * For compatibility with JDBC result sets and the <tt>sql</tt> package, 
 * the position of the first column is <tt>1</tt>, not <tt>0</tt>.
 * <p>
 * The first row is read automatically if the configuration's
 * {@link CsvConfig#hasHeaderRow hasHeaderRow} method returns <tt>true</tt>.
 * Special processing is performed when
 * {@link CsvConfig#setAutoCol(boolean) autoCol}
 * or {@link CsvConfig#setHeaderMatch(boolean) headerMatch} is set to <tt>true</tt>.
 * An application may access the content of an automatically read header row
 * immediately after instantiation of the <tt>CsvReader</tt> without an explicit call
 * to <tt>nextRow</tt>.
 * <p>
 * Most methods may throw a {@link CsvException}, e.g. as a result of parsing errors.
 * An exception will also be thrown if there is an attempt to
 * read any data after a call to <tt>nextRow</tt>
 * returned <tt>false</tt>, or after the reader was closed.
 * @author Uwe Finke
 */
public class CsvReader implements ColumnReader {

  static private final Text text = new Text(CsvReader.class);
  
  private CsvConfig config;
  private Reader in;
  private int rowCount;
  
  private RowParser parser;
  private RowFilter rowFilter;
  private ErrorHandler errorHandler;
  
  private boolean eof;
  
  private String row;
  private int currentIndex;    
  private ColConfig colConfig;
  
  private ObjectFactoryGenerator generator;
  private Class<?> dataClass;
  private ObjectFactory objectFactory;
  
  /**
   * Constructor with configuration.
   * If you use this constructor,
   * you have to set the configurations <tt>file</tt> property.
   * @param config
   * @throws IOException
   * @throws ConfigException
   * @throws CsvException
   */
  public CsvReader(CsvConfig config) throws IOException, ConfigException, CsvException {
    
    this(config.getFile().createReader(), config);
  }

  /**
   * Constructor with implicit default configuration.
   * With this constructor, there are no columns defined.
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
    
    parser = config.getParser();
    parser.init(in, config);
    
    rowFilter = config.getRowFilter();
    errorHandler = new DefaultErrorHandler();
    
    if (config.hasHeaderRow()) {
      processHeaderRow();
    }
  }
  
  private void processHeaderRow() throws IOException, CsvException {
    
    if (! nextRow()) {
      return;
    }
    
    rowCount--;
    
    if (config.isAutoCol()) {
      processAutoCol();
    }
    
    if (config.isHeaderMatch()) {
      processHeaderMatch();
    }
  }
  
  private void processAutoCol() throws CsvException {
    
    for (int i = 1; i <= getColumnCount(); i++) {
      config.addAutoCol(readString(i), i);
    }
  }
  
  private void processHeaderMatch() throws CsvException {
    
    Map<String, Integer> headerMap = new HashMap<String, Integer>();
    for (int i = getColumnCount(); i >= 0; i--) { // backward because on duplicate headers we prefer the leftmost column
      headerMap.put(readString(i), i);
    }
    
    for (ColConfig col : config.getColumnList()) {
      String header = col.getHeader();
      if (header != null) {
        Integer position = headerMap.get(header);
        if (position == null) {
          throw new CsvException(text.get("headerNotFound", header), parser.getLineCount(), 0, row);
        }
        col.setInternalPosition(position);
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
   * Tells whether the retrieved row is empty.
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
  public String getPlainRow() throws CsvException {
    
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
    
    config.getColConfig(columnPosition).setEditor(editor);
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
    
    colConfig = config.getColConfig(index);
    
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
  
  public int getColumnPosition(String columnName) throws CsvException {

    return config.getColumnPosition(columnName);
  }
  
  private void handleParseError(Throwable cause, String value, String type) throws CsvException {

    CsvException error = new CsvException(text.get("parseError", value, type), cause, getLineCount(), getRowCount(), row, currentIndex, colConfig.getName(), value);
    errorHandler.handleError(error);
  }
  
  public String[] readColumns() throws CsvException {
    
    int count = parser.getColumnCount();
    String[] col = new String[count];
    
    int i = 0;
    while (i < count) {
      col[i] = getColumn(++i);
    }
    
    return col;
  }
  
  public int getColumnCount() throws CsvException {
    
    checkEOF();
    return parser.getColumnCount();
  }
  
  public String readString(String columnName) throws CsvException {
    
    return readString(getColumnPosition(columnName));
  }
  
  public String readString(int columnPosition) throws CsvException {

    return getColumn(columnPosition);
  }
  
  private boolean getBoolean(String s) {
    
    return colConfig.getTrueValue().equals(s);
  }
  
  public boolean readBoolean(String columnName) throws CsvException {
    
    return readBoolean(getColumnPosition(columnName));
  }
  
  public boolean readBoolean(int columnPosition) throws CsvException {
    
    return getBoolean(getColumn(columnPosition).trim());
  }
  
  public Boolean readBooleanObject(String columnName) throws CsvException {
    
    return readBooleanObject(getColumnPosition(columnName));
  }
  
  public Boolean readBooleanObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();    
    return (s.length() == 0) ? null : Boolean.valueOf(getBoolean(s));
  }
  
  public byte readByte(String columnName) throws CsvException {
    
    return readByte(getColumnPosition(columnName));
  }
  
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
  
  public Byte readByteObject(String columnName) throws CsvException {
    
    return readByteObject(getColumnPosition(columnName));
  }
  
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
  
  public short readShort(String columnName) throws CsvException {
    
    return readShort(getColumnPosition(columnName));
  }
  
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
  
  public Short readShortObject(String columnName) throws CsvException {
    
    return readShortObject(getColumnPosition(columnName));
  }
  
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
  
  public char readChar(String columnName) throws CsvException {
    
    return readChar(getColumnPosition(columnName));
  }
  
  public char readChar(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return 0;
    }
    return s.charAt(0);
  }
  
  public Character readCharObject(String columnName) throws CsvException {
    
    return readCharObject(getColumnPosition(columnName));
  }
  
  public Character readCharObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    return Character.valueOf(s.charAt(0));
  }
  
  public int readInt(String columnName) throws CsvException {
    
    return readInt(getColumnPosition(columnName));
  }
  
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
  
  public Integer readIntObject(String columnName) throws CsvException {
    
    return readIntObject(getColumnPosition(columnName));
  }
  
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
  
  public long readLong(String columnName) throws CsvException {
    
    return readLong(getColumnPosition(columnName));
  }
  
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
  
  public Long readLongObject(String columnName) throws CsvException {
    
    return readLongObject(getColumnPosition(columnName));
  }
  
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
  
  public float readFloat(String columnName) throws CsvException {
    
    return readFloat(getColumnPosition(columnName));
  }
  
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
  
  public Float readFloatObject(String columnName) throws CsvException {
    
    return readFloatObject(getColumnPosition(columnName));
  }
  
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
  
  public double readDouble(String columnName) throws CsvException {
    
    return readDouble(getColumnPosition(columnName));
  }
  
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
  
  public Double readDoubleObject(String columnName) throws CsvException {
    
    return readDoubleObject(getColumnPosition(columnName));
  }
  
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
  
  public BigDecimal readBigDecimal(String columnName) throws CsvException {
    
    return readBigDecimal(getColumnPosition(columnName));
  }
  
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
  
  public BigInteger readBigInteger(String columnName) throws CsvException {
    
    return readBigInteger(getColumnPosition(columnName));
  }
  
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
  
  public Date readDate(String columnName) throws CsvException {
    
    return readDate(getColumnPosition(columnName));
  }
  
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
  
  /**
   * Reads an <tt>enum</tt> constant.
   * @param <E>
   * @param columnName
   * @param enumType
   * @return enum
   * @throws CsvException
   */
  public <E extends Enum<E>> E readEnum(String columnName, Class<E> enumType) throws CsvException {
    
    return readEnum(getColumnPosition(columnName), enumType);
  }
  
  /**
   * Reads an <tt>enum</tt> constant.
   * @param <E>
   * @param columnPosition
   * @param enumType
   * @return enum
   * @throws CsvException
   */
  public <E extends Enum<E>> E readEnum(int columnPosition, Class<E> enumType) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    try {
      return Util.getEnum(enumType, s);
    } catch (NoSuchEnumException e) {
      handleParseError(e, s, enumType.getName());
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public <D> D readRow(Class<? extends D> clazz) throws CsvException {
    
    try {
      if (dataClass != clazz) {
        if (generator == null) {
          generator = new ObjectFactoryGenerator(config.getNameMap());
        }
        objectFactory = generator.getFactory(clazz);
        dataClass = clazz;
      }
      return (D) objectFactory.createObject(this);
    } catch (Exception e) {
      throw new CsvException(text.get("createObject", clazz.getName()), e);
    }
  }
  
  public <D> Iterable<D> cursor(Class<? extends D> clazz) {
    
    return new RowIterator<D>(this, clazz);
  }

}
