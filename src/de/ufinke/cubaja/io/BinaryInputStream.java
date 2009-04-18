// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UTFDataFormatException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads primitive types and objects from an <code>InputStream</code>.
 * The input data should have been written
 * by <code>BinaryOutputStream</code>.
 * This class maintains an internal buffer;
 * it is not necessary to wrap the underlaying stream into
 * a <code>BufferedInputStream</code>.
 * @author Uwe Finke
 */
public class BinaryInputStream extends FilterInputStream {

  static private final int DEFAULT_BUFFER_SIZE = 8192;
  static private final int MIN_BUFFER_SIZE = 64;
  
  static private final Map<String, Class<?>> primitivesMap = createPrimitivesMap();
  
  static private Map<String, Class<?>> createPrimitivesMap() {
  
    Map<String, Class<?>> map = new HashMap<String, Class<?>>();
    
    for (BinaryStreamParameter parameter : BinaryStreamParameter.values()) {
      if (parameter.isPrimitive()) {
        map.put(parameter.getClazz().getName(), parameter.getClazz());
      }
    }
    
    return map;
  }
  
  private final Map<Class<?>, InputObjectHandler> handlerMap = new HashMap<Class<?>, InputObjectHandler>();
  
  private int bufferSize;
  private byte[] buffer;
  private int bufferLimit;
  private int bufferPosition;
  private long streamPosition;
  
  /**
   * Constructor.
   * @param in the underlaying <code>InputStream</code>
   */
  public BinaryInputStream(InputStream in) {

    this(in, DEFAULT_BUFFER_SIZE);
  }
  
  public BinaryInputStream(InputStream in, int bufferSize) {
    
    super(in);
    this.bufferSize = Math.max(bufferSize, MIN_BUFFER_SIZE);
    buffer = new byte[this.bufferSize];
  }
  
  private byte[] ensureBuffer(int requestedLength) throws IOException {

    if (in == null) {
      throw new IOException("Stream closed");
    }
    
    int availableLength = bufferLimit - bufferPosition;
    
    if (requestedLength <= availableLength) {
      return buffer;
    }
    
    if (requestedLength > bufferSize - bufferPosition) {
      streamPosition += bufferPosition;
      if (availableLength > 0) {
        System.arraycopy(buffer, bufferPosition, buffer, 0, availableLength);
      }
      bufferPosition = 0;
      bufferLimit = availableLength;
    }
    
    while (availableLength < requestedLength) {      
      int bytesRead = in.read(buffer, bufferLimit, bufferSize - bufferLimit);
      if (bytesRead < 0) {
        throw new EOFException();
      }
      availableLength += bytesRead;
      bufferLimit += bytesRead;
    }    
    
    return buffer;
  }
  
  public int read() throws IOException {
    
    try {
      ensureBuffer(1);
      return buffer[bufferPosition++];
    } catch (EOFException e) {
      return -1;
    }
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    
    try {
      ensureBuffer(1);
      len = Math.min(len, bufferLimit - bufferPosition);
      System.arraycopy(buffer, bufferPosition, b, off, len);
      bufferPosition += len;
      return len;
    } catch (EOFException e) {
      return -1;
    }
  }
  
  public long skip(long n) throws IOException {
    
    try {
      ensureBuffer(1);
      long skipped = Math.min(n, bufferLimit - bufferPosition);
      bufferPosition += skipped;
      return skipped;
    } catch (EOFException e) {
      return -1;
    }
  }
  
  public boolean markSupported() {
    
    return false;
  }
  
  public void mark(int readLimit) {
    
  }
  
  public void reset() throws IOException {
    
    throw new IOException("mark / reset not supported");
  }
  
  public int available() throws IOException {
    
    return in.available() + bufferLimit - bufferPosition;
  }
  
  public void close() throws IOException {
    
    if (in != null) {
      in.close();
      in = null;
      buffer = null;
    }
  }
  
  public long position() {
    
    return streamPosition + bufferPosition;
  }
  
  /**
   * Reads a <code>Boolean</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Boolean readBooleanObject() throws IOException {
    
    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return Boolean.valueOf(readBoolean());
    }
  }
  
  public boolean readBoolean() throws IOException {

    ensureBuffer(1);
    return buffer[bufferPosition++] != 0;
  }
  
  /**
   * Reads a <code>Byte</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Byte readByteObject() throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return Byte.valueOf(readByte());
    }
  }
  
  public byte readByte() throws IOException {

    ensureBuffer(1);
    return buffer[bufferPosition++];
  }
  
  /**
   * Reads a <code>Short</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Short readShortObject() throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return Short.valueOf(readShort());
    }
  }
  
  public short readShort() throws IOException {
    
    byte[] buf = ensureBuffer(2);
    int pos = bufferPosition;
    short result = (short) (((buf[pos++] & 0xFF) << 8) 
                           | (buf[pos++] & 0xFF));
    bufferPosition = pos;
    return result;
  }
  
  /**
   * Reads a <code>Character</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Character readCharObject() throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return Character.valueOf(readChar());
    }
  }
  
  public char readChar() throws IOException {
    
    byte[] buf = ensureBuffer(2);
    int pos = bufferPosition;
    char result = (char) (((buf[pos++] & 0xFF) << 8) 
                         | (buf[pos++] & 0xFF));
    bufferPosition = pos;
    return result;
  }
  
  /**
   * Reads an <code>Integer</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Integer readIntObject() throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return Integer.valueOf(readInt());
    }
  }
  
  public int readInt() throws IOException {
    
    byte[] buf = ensureBuffer(4);
    int pos = bufferPosition;
    int result = ((buf[pos++] & 0xFF) << 24)
               | ((buf[pos++] & 0xFF) << 16)
               | ((buf[pos++] & 0xFF) << 8)
               |  (buf[pos++] & 0xFF);
    bufferPosition = pos;
    return result;
  }
  
  /**
   * Reads a <code>Long<code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Long readLongObject() throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return Long.valueOf(readLong());
    }
  }
  
  public long readLong() throws IOException {
    
    byte[] buf = ensureBuffer(8);
    int pos = bufferPosition;
    int highBytes = ((buf[pos++] & 0xFF) << 24)
                  | ((buf[pos++] & 0xFF) << 16)
                  | ((buf[pos++] & 0xFF) << 8)
                  |  (buf[pos++] & 0xFF);
    int lowBytes  = ((buf[pos++] & 0xFF) << 24)
                  | ((buf[pos++] & 0xFF) << 16)
                  | ((buf[pos++] & 0xFF) << 8)
                  |  (buf[pos++] & 0xFF);
    bufferPosition = pos;
    return (((long) highBytes) << 32) | (lowBytes & 0xFFFFFFFFL);
  }
  
  /**
   * Reads a <code>Float</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Float readFloatObject() throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return Float.valueOf(readFloat());
    }
  }
  
  public float readFloat() throws IOException {
    
    return Float.intBitsToFloat(readInt());
  }
  
  /**
   * Reads a <code>Double</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Double readDoubleObject() throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return Double.valueOf(readDouble());
    }
  }
  
  public double readDouble() throws IOException {
    
    return Double.longBitsToDouble(readLong());
  }
  
  /**
   * Reads a <code>String</code>.
   * @return a string or <code>null</code>
   * @throws IOException
   */
  public String readString() throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return readUTF();
    }
  }
  
  private String readUTF() throws IOException {
    
    int utfLength = readChar();

    if (utfLength == 0) {
      return "";
    }
    
    boolean ownBuffer = utfLength > bufferSize;
    
    byte[] buf = ownBuffer ? createUTFBuffer(utfLength) : ensureBuffer(utfLength);
    int pos = ownBuffer ? 0 : bufferPosition;
    
    char[] charBuffer = new char[utfLength];
    int charSize = 0;
    
    byte c = 0;
    byte c2 = 0;
    byte c3 = 0;
    
    try {
      int posLimit = pos + utfLength;
      while (pos < posLimit) {
        c = buf[pos++];
        switch ((c & 0xF0) >>> 4) {
          case 0: 
          case 1:
          case 2:
          case 3:
          case 4:
          case 5:
          case 6:
          case 7:
            charBuffer[charSize++] = (char) c;
            break;
          case 12:
          case 13:
            c2 = buf[pos++];
            if ((c2 & 0xC0) != 0x80) {
              throw new UTFDataFormatException();
            }
            charBuffer[charSize++] = (char) (((c & 0x1F) << 6) | (c2 & 0x3F));
            break;
          case 14:
            c2 = buf[pos++];
            c3 = buf[pos++];
            if ((c2 & 0xC0) != 0x80 || (c3 & 0xC0) != 0x80) {
              throw new UTFDataFormatException();
            }
            charBuffer[charSize++] = (char) (((c & 0x0F) << 12) | ((c2 & 0x3F) << 6) | (c3 & 0x3F));
            break;
          default:
            throw new UTFDataFormatException();
        }
      }
    } catch (IndexOutOfBoundsException e) {
      throw new UTFDataFormatException();
    }
    
    if (ownBuffer) {
      streamPosition += utfLength;
      bufferPosition = 0;
      bufferLimit = 0;
    } else {      
      bufferPosition = pos;
    }
    
    return new String(charBuffer, 0, charSize);
  }
  
  private byte[] createUTFBuffer(int length) throws IOException {
    
    byte[] buf = new byte[length];
    
    int bytesAvailable = bufferLimit - bufferPosition;
    if (bytesAvailable > 0) {
      System.arraycopy(buffer, bufferPosition, buf, 0, bytesAvailable);
    }
    
    while (bytesAvailable < buf.length) {      
      int bytesRead = in.read(buf, bytesAvailable, buf.length - bytesAvailable);
      if (bytesRead < 0) {
        throw new EOFException();
      }
      bytesAvailable += bytesRead;
    }
    
    return buf;
  }
  
  /**
   * Reads a <code>Date</code>.
   * @return a date or <code>null</code>
   * @throws IOException
   */
  public Date readDate() throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return new Date(readLong());
    }
  }
  
  /**
   * Reads a <code>BigInteger</code>.
   * @return a big integer or <code>null</code>
   * @throws IOException
   */
  public BigInteger readBigInteger() throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return new BigInteger(readNonNullByteArray());
    }
  }
  
  /**
   * Reads a <code>BigDecimal</code>.
   * @return a big decimal or <code>null</code>
   * @throws IOException
   */
  public BigDecimal readBigDecimal() throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return new BigDecimal(new BigInteger(readNonNullByteArray()), readInt());
    }
  }
  
  /**
   * Reads a byte array which may be <code>null</code>.
   * @return a byte array or <code>null</code>
   * @throws IOException
   */
  public byte[] readByteArray() throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return readNonNullByteArray();
    }
  }
  
  private byte[] readNonNullByteArray() throws IOException {
    
    int len = readInt();
    byte[] b = new byte[len];
    
    if (len == 0) {
      return b;
    } 
    
    if (len <= bufferSize) {      
      ensureBuffer(len);
      System.arraycopy(buffer, bufferPosition, b, 0, len);
      bufferPosition += len;
      return b;
    }
    
    int offset = 0;
    while (len > 0) {
      int chunkLen = Math.min(len, bufferSize);
      ensureBuffer(chunkLen);
      System.arraycopy(buffer, bufferPosition, b, offset, chunkLen);
      bufferPosition += chunkLen;
      len -= chunkLen;
    }
    return b;
  }
  
  /**
   * Reads an <code>Enum</code> constant.
   * @param <E> enum type
   * @param clazz the <code>Enum</code> class
   * @return enum constant or <code>null</code>
   * @throws IOException
   */
  public <E extends Enum<E>> E readEnum(Class<E> clazz) throws IOException {

    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    } else {
      return clazz.getEnumConstants()[readChar()];
    }
  }
  
  /**
   * Reads any object.
   * <p/>
   * The object is created and filled by an <code>InputObjectHandler</code>.
   * We can provide our own individual handlers with the <code>addObjectHandler</code> method.
   * <p/>
   * If we do not provide an appropriate handler for a data class,
   * a factory will generate a handler automatically.
   * An automatically generated handler calls all setter methods of a data object
   * when there is a matching getter method with the same return type
   * as the setter parameter type.
   * When there is an unknown parameter type, the <code>readObject</code>
   * method is called recursively.
   * @param <D> data class type
   * @param clazz data class type
   * @return object of type <code>clazz</code>
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public <D> D readObject(Class<? extends D> clazz) throws Exception {
    
    ensureBuffer(1);
    if (buffer[bufferPosition++] == 0) {
      return null;
    }
    
    InputObjectHandler handler = handlerMap.get(clazz); 
    
    if (handler == null) {
      handler = getGeneratedHandler(clazz);
      handlerMap.put(clazz, handler);
    }
    
    return (D) handler.read(this, clazz);
  }
  
  private InputObjectHandler getGeneratedHandler(Class<?> clazz) throws Exception {
    
    int size = readShort();
    List<PropertyDescription> receivedProperties = new ArrayList<PropertyDescription>(size);
    for (int i = 0; i < size; i++) {
      String propertyName = readUTF();
      String propertyClazzName = readUTF();
      Class<?> propertyClazz = primitivesMap.get(propertyClazzName);
      if (propertyClazz == null) {
        propertyClazz = Class.forName(propertyClazzName);
      }
      receivedProperties.add(new PropertyDescription(propertyName, propertyClazz));
    }
    
    PropertyClassAnalyzer analyzer = new PropertyClassAnalyzer(clazz);
    analyzer.checkIntersection(receivedProperties);
    
    return InputObjectHandlerFactory.getHandler(clazz, receivedProperties);
  }
  
  /**
   * Adds an object handler for a data class type.
   * @param clazz data class type
   * @param handler handler
   */
  public void addObjectHandler(Class<?> clazz, InputObjectHandler handler) {
    
    handlerMap.put(clazz, handler);
  }  
  
}
