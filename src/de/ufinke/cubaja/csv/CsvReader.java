// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.util.Text;

public class CsvReader {

  static private final Text text = new Text(CsvReader.class);
  
  private CsvConfig config;
  private LineNumberReader in;
  
  private List<ColConfig> columnList;
  private Map<String, Integer> nameMap;
  private LineParser parser;
  
  private boolean eof;
  
  private String line;
  private int currentIndex;    
  private ColConfig colConfig;
  
  private ObjectFactoryGenerator generator;
  
  public CsvReader(CsvConfig config) throws IOException, ConfigException, CsvException {
    
    this(config.createReader(), config);
  }

  public CsvReader(Reader reader) throws CsvException {
  
    this(reader, new CsvConfig());
  }
  
  public CsvReader(Reader reader, CsvConfig config) throws CsvException {
  
    this.config = config;
    in = new LineNumberReader(reader);
    in.setLineNumber(1);

    columnList = config.getColumnList();
    
    readHeaderLine();
    initPositions();
    initParser();
  }
  
  private void readHeaderLine() {
    
    if (! config.hasHeaderLine()) {
      return;
    }
    
    if (! nextLine()) {
      return;
    }
    
    String[] headers = readColumns();
    Map<String, Integer> headerMap = new HashMap<String, Integer>();
    for (int i = 0; i < headers.length; i++) {
      headerMap.put(headers[i], i + 1);
    }
    
    for (ColConfig col : columnList) {
      if (col.getPosition() == 0 && col.getHeader() != null) {
        Integer position = headerMap.get(col.getHeader());
        if (position == null) {
          throw new CsvException(text.get("headerNotFound", col.getHeader()), 1, line);
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
  
  private void initParser() {
    
    parser = config.getParser();
    parser.init(config);
  }
  
  public int getLineNumber() {
    
    return in.getLineNumber();
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
  
  public boolean nextLine() throws CsvException {
    
    try {      
      line = in.readLine();
      parser.setLine(line, in.getLineNumber());
    } catch (IOException e) {
      throw new CsvException(text.get("ioException", Integer.valueOf(getLineNumber())), e, getLineNumber(), null);
    }
    
    eof = (line == null);
    return ! eof;
  }
  
  public boolean isEmptyLine() throws CsvException {

    checkEOF();
    return line.length() < parser.getColumnCount();
  }
  
  public String getLine() throws CsvException {
    
    checkEOF();
    return line;
  }
  
  private String getColumn(int index) {

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
    
    return s;
  }
  
  public int getColumnPosition(String columnName) throws CsvException {
    
    Integer index = nameMap.get(columnName);
    if (index == null) {
      throw new CsvException(text.get("undefinedName", columnName));
    }
    return index; 
  }
  
  private CsvException createParseError(Throwable cause, String value, String type) {
    
    return new CsvException(text.get("parseError", value, Integer.valueOf(getLineNumber()), Integer.valueOf(currentIndex)), cause, getLineNumber(), currentIndex, line, value);
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
  
  private byte getByte(String s) throws CsvException {
    
    try {
      return Byte.parseByte(s);
    } catch (Exception e) {
      throw createParseError(e, s, "byte");
    }
  }
  
  public byte readByte(String columnName) throws CsvException {
    
    return readByte(getColumnPosition(columnName));
  }
  
  public byte readByte(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return 0;
    }
    return getByte(s);
  }
  
  public Byte readByteObject(String columnName) throws CsvException {
    
    return readByteObject(getColumnPosition(columnName));
  }
  
  public Byte readByteObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    return Byte.valueOf(getByte(s));
  }
  
  private short getShort(String s) throws CsvException {
    
    try {
      return Short.parseShort(s);
    } catch (Exception e) {
      throw createParseError(e, s, "short");
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
    return getShort(s);
  }
  
  public Short readShortObject(String columnName) throws CsvException {
    
    return readShortObject(getColumnPosition(columnName));
  }
  
  public Short readShortObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    return Short.valueOf(getShort(s));
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
  
  private int getInt(String s) throws CsvException {
    
    try {
      return Integer.parseInt(s);
    } catch (Exception e) {
      throw createParseError(e, s, "int");
    }
  }
  
  public int readInt(String columnName) throws CsvException {
    
    return readInt(getColumnPosition(columnName));
  }
  
  public int readInt(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return 0;
    }
    return getInt(s);
  }
  
  public Integer readIntObject(String columnName) throws CsvException {
    
    return readIntObject(getColumnPosition(columnName));
  }
  
  public Integer readIntObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    return Integer.valueOf(getInt(s));
  }
  
  private long getLong(String s) throws CsvException {
    
    try {
      return Long.parseLong(s);
    } catch (Exception e) {
      throw createParseError(e, s, "long");
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
    return getLong(s);
  }
  
  public Long readLongObject(String columnName) throws CsvException {
    
    return readLongObject(getColumnPosition(columnName));
  }
  
  public Long readLongObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    return Long.valueOf(getLong(s));
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
  
  private float getFloat(String s) throws CsvException {
    
    try {
      return Float.parseFloat(prepareDecimalString(s));
    } catch (Exception e) {
      throw createParseError(e, s, "float");
    }
  }
  
  public float readFloat(String columnName) throws CsvException {
    
    return readFloat(getColumnPosition(columnName));
  }
  
  public float readFloat(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return 0;
    }
    return getFloat(s);
  }
  
  public Float readFloatObject(String columnName) throws CsvException {
    
    return readFloatObject(getColumnPosition(columnName));
  }
  
  public Float readFloatObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    return Float.valueOf(getFloat(s));
  }
  
  private double getDouble(String s) throws CsvException {
    
    try {
      return Double.parseDouble(prepareDecimalString(s));
    } catch (Exception e) {
      throw createParseError(e, s, "double");
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
    return getDouble(s);
  }
  
  public Double readDoubleObject(String columnName) throws CsvException {
    
    return readDoubleObject(getColumnPosition(columnName));
  }
  
  public Double readDoubleObject(int columnPosition) throws CsvException {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }
    return Double.valueOf(getDouble(s));
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
      throw createParseError(e, s, "BigDecimal");
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
      throw createParseError(e, s, "BigInteger");
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
      throw createParseError(e, s, "Date");
    }
  }
  
  public <E extends Enum<E>> E readEnum(Class<E> clazz, String columnName) throws CsvException {
    
    return readEnum(clazz, getColumnPosition(columnName));
  }
  
  public <E extends Enum<E>> E readEnum(Class<E> clazz, int columnPosition) {
    
    String s = getColumn(columnPosition).trim();
    if (s.length() == 0) {
      return null;
    }

    try {
      char c = s.charAt(0);
      if (c >= 0 && c <= 9) {
        return clazz.getEnumConstants()[getInt(s)];
      } else {
        try {
          return Enum.valueOf(clazz, s);
        } catch (Exception e) {
          return Enum.valueOf(clazz, s.toUpperCase());
        }
      }
    } catch (Exception e) {
      throw createParseError(e, s, clazz.getName());
    }
  }
  
  @SuppressWarnings("unchecked")
  public <D> D readObject(Class<? extends D> clazz) throws CsvException {
    
    try {
      if (generator == null) {
        generator = new ObjectFactoryGenerator();
      }
      return (D) generator.getFactory(clazz).createObject(this);
    } catch (Exception e) {
      throw new CsvException(text.get("readObject", clazz.getName()), e);
    }
  }
  
  public <D> Iterable<D> iterator(Class<? extends D> clazz) {
    
    return new ObjectIterator<D>(this, clazz);
  }

}
