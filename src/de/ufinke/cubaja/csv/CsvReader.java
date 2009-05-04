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
import de.ufinke.cubaja.util.Text;

/**
 * CSV reader.
 * <p>
 * We retrieve record in a loop calling <code>nextRecord</code>
 * until we receive <code>false</code>.
 * <p>
 * For every record, we can read column contents as the type we need in our application,
 * or we can create a data object with column data by using method <code>readObject</code>.
 * When the column is empty, the read methods for numeric primitive types
 * return <code>0</code>; read methods for objects types 
 * (except <code>readObject</code>) return <code>null</code>.
 * <p>
 * The position of the first column is 1, not 0.
 * <p>
 * The first record is read automatically if the configurations
 * <code>hasHeaderRecord</code> method returns <code>true</code>.
 * In this case, column positions are determined automatically
 * when the column configuration contains a header definition.
 * Despite the automatism, we can retrieve the content of the header
 * record before we call <code>nextRecord</code> the first time. 
 * <p>
 * Most methods may throw a <code>CsvException</code>.
 * An exception is thrown if there is an attempt to
 * read any data after a call to <code>nextRecord</code>
 * returned <code>false</code>, or after the reader was closed.
 * @author Uwe Finke
 */
public class CsvReader {

  static private final Text text = new Text(CsvReader.class);
  
  private CsvConfig config;
  private Reader in;
  private int recordCount;
  
  private List<ColConfig> columnList;
  private Map<String, Integer> nameMap;
  private RecordParser parser;
  private RecordFilter recordFilter;
  private ErrorHandler errorHandler;
  
  private boolean eof;
  
  private String record;
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
    
    recordFilter = config.getRecordFilter();
    errorHandler = new DefaultErrorHandler();
    
    readHeaderRecord();
    initPositions();
  }
  
  private void readHeaderRecord() throws IOException, CsvException {
    
    if (! config.hasHeaderRecord()) {
      return;
    }
    
    if (! nextRecord()) {
      return;
    }
    
    recordCount--;
    
    String[] headers = readColumns();
    Map<String, Integer> headerMap = new HashMap<String, Integer>();
    for (int i = headers.length - 1; i >= 0; i--) { // backward because on duplicate headers we prefere the leftmost column
      headerMap.put(headers[i], i + 1);
    }
    
    for (ColConfig col : columnList) {
      if (col.getPosition() == 0 && col.getHeader() != null) {
        Integer position = headerMap.get(col.getHeader());
        if (position == null) {
          throw new CsvException(text.get("headerNotFound", col.getHeader()), parser.getLineCount(), 0, record);
        }
        col.setInternalPosition(position);
      }
    }
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
      
      nameMap.put(col.getName(), col.getPosition());
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
  
  /**
   * Returns the number of the current record.
   * This value may differ from the line number
   * because header records don't count and
   * regular records may contain line breaks within escaped column data.
   * @return record number
   */
  public int getRecordCount() {
    
    return recordCount;
  }
  
  /**
   * Returns the number of raw lines read so far.
   * @return line number
   */
  public int getLineCount() {
    
    return parser.getLineCount();
  }
  
  /**
   * Closes the reader.
   * @throws IOException
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
  
  /**
   * Retrieves the next record.
   * @return <code>true</code> when a record was successfully read, <code>false</code> when end of file.
   * @throws CsvException
   */
  public boolean nextRecord() throws IOException, CsvException {
    
    boolean accepted = false;
    
    while (! accepted) {
      recordCount++;
      record = parser.readRecord();
      eof = (record == null);
      if (eof) {
        accepted = true;
      } else {
        accepted = (recordFilter == null) ? true : recordFilter.acceptRecord(this);
      }
    }
    
    return ! eof;
  }
  
  /**
   * Returns whether the retrieved record is empty.
   * A record is assumed to be empty when all valid column data have zero length.
   * @return flag
   * @throws CsvException
   */
  public boolean isEmptyRecord() throws CsvException {

    checkEOF();
    return parser.isEmptyRecord();
  }
  
  /**
   * Returns the complete last retrieved record.
   * @return record
   * @throws CsvException
   */
  public String getRecord() throws CsvException {
    
    checkEOF();
    return record;
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
   * Sets a record filter.
   * @param recordFilter
   */
  public void setRecordFilter(RecordFilter recordFilter) {
    
    config.setRecordFilter(recordFilter);
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
      s = colConfig.getEditor().editColumn(s, colConfig, this);
    }
    
    return s;
  }
  
  /**
   * Returns the position of a named column.
   * @param columnName
   * @return column position
   * @throws CsvException
   */
  public int getColumnPosition(String columnName) throws CsvException {
    
    Integer index = nameMap.get(columnName);
    if (index == null) {
      throw new CsvException(text.get("undefinedName", columnName));
    }
    return index; 
  }
  
  private void handleParseError(Throwable cause, String value, String type) throws CsvException {

    CsvException error = new CsvException(text.get("parseError", value, type), cause, getLineCount(), getRecordCount(), record, currentIndex, colConfig.getName(), value);
    errorHandler.handleError(error);
  }
  
  /**
   * Returns all columns of the last retrieved record.
   * @return array with columns
   * @throws CsvException
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
  
  /**
   * Returns the number of columns in the last retrieved record.
   * @return column count
   * @throws CsvException
   */
  public int getColumnCount() throws CsvException {
    
    checkEOF();
    return parser.getColumnCount();
  }
  
  /**
   * Returns column content as string.
   * The original content may have been modified according
   * to the settings in <code>ColConfig</code>.
   * @param columnName 
   * @return string
   * @throws CsvException
   */
  public String readString(String columnName) throws CsvException {
    
    return readString(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as string.
   * The original content may have been modified according
   * to the settings in <code>ColConfig</code>. 
   * @param columnPosition
   * @return string
   * @throws CsvException
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
  
  /**
   * Returns column content as boolean.
   * See <code>ColConfig</code> to define the <code>true</code> values.
   * @param columnName
   * @return boolean
   * @throws CsvException
   */
  public boolean readBoolean(String columnName) throws CsvException {
    
    return readBoolean(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as boolean.
   * See <code>ColConfig</code> to define the <code>true</code> values.
   * @param columnPosition
   * @return boolean
   * @throws CsvException
   */
  public boolean readBoolean(int columnPosition) throws CsvException {
    
    return getBoolean(getColumn(columnPosition).trim());
  }
  
  /**
   * Returns column content as Boolean object.
   * See <code>ColConfig</code> to define the <code>true</code> values.
   * @param columnName
   * @return boolean
   * @throws CsvException
   */
  public Boolean readBooleanObject(String columnName) throws CsvException {
    
    return readBooleanObject(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as Boolean object.
   * See <code>ColConfig</code> to define the <code>true</code> values.
   * @param columnPosition
   * @return boolean
   * @throws CsvException
   */
  public Boolean readBooleanObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();    
    return (s.length() == 0) ? null : Boolean.valueOf(getBoolean(s));
  }
  
  /**
   * Returns column content as byte.
   * @param columnName
   * @return byte
   * @throws CsvException
   */
  public byte readByte(String columnName) throws CsvException {
    
    return readByte(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as byte.
   * @param columnPosition
   * @return byte
   * @throws CsvException
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
  
  /**
   * Returns column content as Byte object.
   * @param columnName
   * @return byte
   * @throws CsvException
   */
  public Byte readByteObject(String columnName) throws CsvException {
    
    return readByteObject(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as Byte object.
   * @param columnPosition
   * @return byte
   * @throws CsvException
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
  
  /**
   * Returns column content as short.
   * @param columnName
   * @return short
   * @throws CsvException
   */
  public short readShort(String columnName) throws CsvException {
    
    return readShort(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as short.
   * @param columnPosition
   * @return short
   * @throws CsvException
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
  
  /**
   * Returns column content as Short object.
   * @param columnName
   * @return short
   * @throws CsvException
   */
  public Short readShortObject(String columnName) throws CsvException {
    
    return readShortObject(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as Short object.
   * @param columnPosition
   * @return short
   * @throws CsvException
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
  
  /**
   * Returns column content as char.
   * @param columnName
   * @return char
   * @throws CsvException
   */
  public char readChar(String columnName) throws CsvException {
    
    return readChar(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as char.
   * @param columnPosition
   * @return char
   * @throws CsvException
   */
  public char readChar(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return 0;
    }
    return s.charAt(0);
  }
  
  /**
   * Returns column content as Character object.
   * @param columnName
   * @return char
   * @throws CsvException
   */
  public Character readCharObject(String columnName) throws CsvException {
    
    return readCharObject(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as Character object.
   * @param columnPosition
   * @return char
   * @throws CsvException
   */
  public Character readCharObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    return Character.valueOf(s.charAt(0));
  }
  
  /**
   * Returns column content as int.
   * @param columnName
   * @return int
   * @throws CsvException
   */
  public int readInt(String columnName) throws CsvException {
    
    return readInt(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as int.
   * @param columnPosition
   * @return int
   * @throws CsvException
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
  
  /**
   * Returns column content as Integer object.
   * @param columnName
   * @return int
   * @throws CsvException
   */
  public Integer readIntObject(String columnName) throws CsvException {
    
    return readIntObject(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as Integer object.
   * @param columnPosition
   * @return int
   * @throws CsvException
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
  
  /**
   * Returns column content as long.
   * @param columnName
   * @return long
   * @throws CsvException
   */
  public long readLong(String columnName) throws CsvException {
    
    return readLong(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as long.
   * @param columnPosition
   * @return long
   * @throws CsvException
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
  
  /**
   * Returns column content as Long object.
   * @param columnName
   * @return long
   * @throws CsvException
   */
  public Long readLongObject(String columnName) throws CsvException {
    
    return readLongObject(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as Long object.
   * @param columnPosition
   * @return long
   * @throws CsvException
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
  
  /**
   * Returns column content as float.
   * @param columnName
   * @return float
   * @throws CsvException
   */
  public float readFloat(String columnName) throws CsvException {
    
    return readFloat(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as float.
   * @param columnPosition
   * @return float
   * @throws CsvException
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
  
  /**
   * Returns column content as Float object.
   * @param columnName
   * @return float
   * @throws CsvException
   */
  public Float readFloatObject(String columnName) throws CsvException {
    
    return readFloatObject(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as Float object.
   * @param columnPosition
   * @return float
   * @throws CsvException
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
  
  /**
   * Returns column content as double.
   * @param columnName
   * @return double
   * @throws CsvException
   */
  public double readDouble(String columnName) throws CsvException {
    
    return readDouble(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as double.
   * @param columnPosition
   * @return double
   * @throws CsvException
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
  
  /**
   * Returns column content as Double object.
   * @param columnName
   * @return double
   * @throws CsvException
   */
  public Double readDoubleObject(String columnName) throws CsvException {
    
    return readDoubleObject(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as Double object.
   * @param columnPosition
   * @return double
   * @throws CsvException
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
  
  /**
   * Returns column content as BigDecimal.
   * @param columnName
   * @return BigDecimal
   * @throws CsvException
   */
  public BigDecimal readBigDecimal(String columnName) throws CsvException {
    
    return readBigDecimal(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as BigDecimal.
   * @param columnPosition
   * @return BigDecimal
   * @throws CsvException
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
  
  /**
   * Returns column content as BigInteger.
   * @param columnName
   * @return BigInteger
   * @throws CsvException
   */
  public BigInteger readBigInteger(String columnName) throws CsvException {
    
    return readBigInteger(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as BigInteger.
   * @param columnPosition
   * @return BigInteger
   * @throws CsvException
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
  
  /**
   * Returns column content as Date.
   * @param columnName
   * @return Date
   * @throws CsvException
   */
  public Date readDate(String columnName) throws CsvException {
    
    return readDate(getColumnPosition(columnName));
  }
  
  /**
   * Returns column content as Date.
   * @param columnPosition
   * @return Date
   * @throws CsvException
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

  /**
   * Returns a column content as Enum constant.
   * If the column content start with a digit, 
   * the constant is derived using the position of the Enum constant value array.
   * Otherwise, the reader tries to get the constant by name. A last try 
   * is done with the constant name in uppercase.
   * @param <E> Enum type
   * @param columnName
   * @param clazz Enum class
   * @return Enum constant 
   * @throws CsvException
   */
  public <E extends Enum<E>> E readEnum(String columnName, Class<E> clazz) throws CsvException {
    
    return readEnum(getColumnPosition(columnName), clazz);
  }
  
  /**
   * Returns a column content as Enum constant.
   * If the column content start with a digit, 
   * the constant is derived using the position of the Enum constant value array.
   * Otherwise, the reader tries to get the constant by name. A last try 
   * is done with the constant name in uppercase.
   * @param <E> Enum type
   * @param columnPosition
   * @param clazz Enum class
   * @return Enum constant 
   * @throws CsvException
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

  /**
   * Returns a data object.
   * <p>
   * The data object class must have setter methods corresponding to 
   * column names. See description of method <code>createMethodName</code> 
   * of class <code>de.ufinke.cubaja.Util</code> for building method names from
   * column names.
   * The setter methods must have a void return type and exactly one parameter
   * of a type supported by one of the <code>CsvReader</code>s <code>read</code> 
   * methods.
   * <p>
   * Note that for performance reasons
   * the setter methods are not called by the reflection API but
   * by an on the fly generated instance of <code>ObjectFactory</code>. 
   * @param <D> data type
   * @param clazz
   * @return data object
   * @throws CsvException
   */
  @SuppressWarnings("unchecked")
  public <D> D readObject(Class<? extends D> clazz) throws CsvException {
    
    try {
      return (D) ObjectFactoryManager.getFactory(clazz, nameMap).createObject(this);
    } catch (Exception e) {
      throw new CsvException(text.get("readObject", clazz.getName()), e);
    }
  }
  
  /**
   * Returns an <code>Iterable</code> over all data objects.
   * The underlying <code>Iterator</code> calls <code>nextRecord</code>
   * and <code>readObject</code> until EOF.
   * May be used to process CSV sources with homogeneous structures and high data quality
   * in a <code>for</code> loop. 
   * @param <D> data type
   * @param clazz
   * @return Iterable
   */
  public <D> Iterable<D> iterator(Class<? extends D> clazz) {
    
    return new ObjectIterator<D>(this, clazz);
  }

}
