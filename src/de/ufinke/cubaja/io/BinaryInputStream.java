// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.util.Text;

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
  
  static private final Text text = new Text(BinaryInputStream.class);
  
  static private final Map<Integer, String> typeMap = createTypeMap();
  
  static private Map<Integer, String> createTypeMap() {
    
    Map<Integer, String> map = new HashMap<Integer, String>(32);
    
    map.put(0x0, "[reserved]");
    map.put(0x1, "Boolean");
    map.put(0x2, "Float");
    map.put(0x3, "Double");
    map.put(0x4, "Byte");
    map.put(0x5, "Short");
    map.put(0x6, "Character");
    map.put(0x7, "Integer");
    map.put(0x8, "Long");
    map.put(0x9, "String");
    map.put(0xA, "BigInteger");
    map.put(0xB, "BigDecimal");
    map.put(0xC, "Date");
    map.put(0xD, "Enum");
    map.put(0xE, "byteArray");
    map.put(0xF, "Object");
    
    return map;
  }
  
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
  private char[] charBuffer;
  private int[] additionalIndex;
  
  public BinaryInputStream(InputStream in) {
    
    this(in, DEFAULT_BUFFER_SIZE);
  }
  
  public BinaryInputStream(InputStream in, int bufferSize) {
    
    super(in);
    this.bufferSize = Math.max(bufferSize, MIN_BUFFER_SIZE);
    buffer = new byte[this.bufferSize];
    charBuffer = new char[32];
    additionalIndex = new int[8];
  }
  
  private int expectType(int mask) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    int typeByte = buf[bufferPosition++];
    
    if ((typeByte & 0xF0) != mask) {
      int expected = mask >>> 4;
      int received = (typeByte >>> 4) & 0x0F;
      throw new IOException(text.get("wrongType", Long.valueOf(position()), typeMap.get(expected), typeMap.get(received)));
    }
    
    return typeByte & 0x0F;
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
  
  private byte getByte(int subType) throws IOException {
    
    switch (subType) {
      
      case 0:
      case 1:
        return (byte) 0;
        
      case 2:
        return ensureBuffer(1)[bufferPosition++];
        
      default:
        throw new IOException(text.get("wrongSubType", Integer.valueOf(subType), Long.valueOf(position())));
    }
  }
  
  private int getSmallInt(int subType) throws IOException {
    
    switch (subType) {
      
      case 0:
      case 1:
        return 0;
        
      case 2: {        
        byte[] buf = ensureBuffer(2);
        int pos = bufferPosition;
        int result = ((buf[pos++] & 0xFF) << 8)
                   |  (buf[pos++] & 0xFF);
        bufferPosition = pos;
        return result;
      }
        
      case 3:
        return ensureBuffer(1)[bufferPosition++] & 0xFF;
        
      case 4:
        return 0xFF00 | (ensureBuffer(1)[bufferPosition++] & 0xFF);
        
      default:
        throw new IOException(text.get("wrongSubType", Integer.valueOf(subType), Long.valueOf(position())));
    }
  }
  
  private int getInt(int subType) throws IOException {

    switch (subType) {
      
      case 0:
      case 1:
        return 0;
        
      case 2: {        
        byte[] buf = ensureBuffer(4);
        int pos = bufferPosition;
        int result = ( buf[pos++]         << 24)
                   | ((buf[pos++] & 0xFF) << 16)
                   | ((buf[pos++] & 0xFF) << 8)
                   |  (buf[pos++] & 0xFF);
        bufferPosition = pos;
        return result;
      }
        
      case 3: {
        byte[] buf = ensureBuffer(3);
        int pos = bufferPosition;
        int result = ((buf[pos++] & 0xFF) << 16)
                   | ((buf[pos++] & 0xFF) << 8)
                   |  (buf[pos++] & 0xFF);
        bufferPosition = pos;
        return result;
      }
        
      case 4: {
        byte[] buf = ensureBuffer(2);
        int pos = bufferPosition;
        int result = ((buf[pos++] & 0xFF) << 8)
                   |  (buf[pos++] & 0xFF);
        bufferPosition = pos;
        return result;
      }
        
      case 5:
        return ensureBuffer(1)[bufferPosition++] & 0xFF;
        
      case 6: {
        byte[] buf = ensureBuffer(3);
        int pos = bufferPosition;
        int result = 0xFF000000
                   | ((buf[pos++] & 0xFF) << 16)
                   | ((buf[pos++] & 0xFF) << 8)
                   |  (buf[pos++] & 0xFF);
        bufferPosition = pos;
        return result;
      }
        
      case 7: {
        byte[] buf = ensureBuffer(2);
        int pos = bufferPosition;
        int result = 0xFFFF0000
                   | ((buf[pos++] & 0xFF) << 8)
                   |  (buf[pos++] & 0xFF);
        bufferPosition = pos;
        return result;
      }
        
      case 8:
        return 0xFFFFFF00 | (ensureBuffer(1)[bufferPosition++] & 0xFF);
        
      default:
        throw new IOException(text.get("wrongSubType", Integer.valueOf(subType), Long.valueOf(position())));
    }
  }
  
  private long getLong(int subType) throws IOException {
    
    switch (subType) {
      
      case 0:
      case 1:
        return 0;
        
      case 2: {
        byte[] buf = ensureBuffer(8);
        int pos = bufferPosition;
        long result = ((long) (
                          ( buf[pos++]         << 24)
                        | ((buf[pos++] & 0xFF) << 16)
                        | ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      ) << 32)
                    | (
                          ( buf[pos++]         << 24)
                        | ((buf[pos++] & 0xFF) << 16)
                        | ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      );
        bufferPosition = pos;
        return result;
      }
                
      case 3: {
        byte[] buf = ensureBuffer(7);
        int pos = bufferPosition;
        long result = ((long) (
                          ((buf[pos++] & 0xFF) << 16)
                        | ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      ) << 32)
                    | (
                          ( buf[pos++]         << 24)
                        | ((buf[pos++] & 0xFF) << 16)
                        | ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      );
        bufferPosition = pos;
        return result;
      }
                
      case 4: {
        byte[] buf = ensureBuffer(6);
        int pos = bufferPosition;
        long result = ((long) (
                          ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      ) << 32)
                    | (
                          ( buf[pos++]         << 24)
                        | ((buf[pos++] & 0xFF) << 16)
                        | ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      );
        bufferPosition = pos;
        return result;
      }
                
      case 5: {
        byte[] buf = ensureBuffer(5);
        int pos = bufferPosition;
        long result = ((long) (
                           (buf[pos++] & 0xFF)
                      ) << 32)
                    | (
                          ( buf[pos++]         << 24)
                        | ((buf[pos++] & 0xFF) << 16)
                        | ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      );
        bufferPosition = pos;
        return result;
      }
                
      case 6: {
        byte[] buf = ensureBuffer(4);
        int pos = bufferPosition;
        long result = ( buf[pos++]         << 24)
                    | ((buf[pos++] & 0xFF) << 16)
                    | ((buf[pos++] & 0xFF) << 8)
                    |  (buf[pos++] & 0xFF);
        bufferPosition = pos;
        return result;
      }
                
      case 7: {
        byte[] buf = ensureBuffer(3);
        int pos = bufferPosition;
        long result = ((buf[pos++] & 0xFF) << 16)
                    | ((buf[pos++] & 0xFF) << 8)
                    |  (buf[pos++] & 0xFF);
        bufferPosition = pos;
        return result;
      }
                
      case 8: {
        byte[] buf = ensureBuffer(2);
        int pos = bufferPosition;
        long result = ((buf[pos++] & 0xFF) << 8)
                    |  (buf[pos++] & 0xFF);
        bufferPosition = pos;
        return result;
      }
                
      case 9: 
        return ensureBuffer(1)[bufferPosition++] & 0xFF;
                
      case 0xA: {
        byte[] buf = ensureBuffer(6);
        int pos = bufferPosition;
        long result = ((long) (0xFFFF0000
                        | ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      ) << 32)
                    | (
                          ( buf[pos++]         << 24)
                        | ((buf[pos++] & 0xFF) << 16)
                        | ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      );
        bufferPosition = pos;
        return result;
      }
                
      case 0xB: {
        byte[] buf = ensureBuffer(5);
        int pos = bufferPosition;
        long result = ((long) (0xFFFFFF00
                        |  (buf[pos++] & 0xFF)
                      ) << 32)
                    | (
                          ( buf[pos++]         << 24)
                        | ((buf[pos++] & 0xFF) << 16)
                        | ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      );
        bufferPosition = pos;
        return result;
      }
                
      case 0xC: {
        byte[] buf = ensureBuffer(4);
        int pos = bufferPosition;
        long result = 0xFFFFFFFF00000000L
                    | (
                          ( buf[pos++]         << 24)
                        | ((buf[pos++] & 0xFF) << 16)
                        | ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      );
        bufferPosition = pos;
        return result;
      }
                
      case 0xD: {
        byte[] buf = ensureBuffer(3);
        int pos = bufferPosition;
        long result = 0xFFFFFFFFFF000000L
                    | (
                          ((buf[pos++] & 0xFF) << 16)
                        | ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      );
        bufferPosition = pos;
        return result;
      }
                
      case 0xE: {
        byte[] buf = ensureBuffer(2);
        int pos = bufferPosition;
        long result = 0xFFFFFFFFFFFF0000L
                    | (
                          ((buf[pos++] & 0xFF) << 8)
                        |  (buf[pos++] & 0xFF)
                      );
        bufferPosition = pos;
        return result;
      }
                
      case 0xF: {
        return 0xFFFFFFFFFFFFFF00L | (ensureBuffer(1)[bufferPosition++] & 0xFF);
      }
                
      default:
        throw new IOException(text.get("wrongSubType", Integer.valueOf(subType), Long.valueOf(position())));
    }
  }
  
  private float getFloat(int subType) throws IOException {
    
    switch (subType) {
      
      case 0:
      case 1:
        return 0;
        
      case 2: {
        byte[] buf = ensureBuffer(4);
        int pos = bufferPosition;
        int v = ( buf[pos++]         << 24)
              | ((buf[pos++] & 0xFF) << 16)
              | ((buf[pos++] & 0xFF) << 8)
              |  (buf[pos++] & 0xFF);
        bufferPosition = pos;
        return Float.intBitsToFloat(v);
      }
        
      case 3: {
        byte[] buf = ensureBuffer(3);
        int pos = bufferPosition;
        int v = ( buf[pos++]         << 24)
              | ((buf[pos++] & 0xFF) << 16)
              | ((buf[pos++] & 0xFF) << 8);
        bufferPosition = pos;
        return Float.intBitsToFloat(v);
      }
        
      case 4: {
        byte[] buf = ensureBuffer(2);
        int pos = bufferPosition;
        int v = ( buf[pos++]         << 24)
              | ((buf[pos++] & 0xFF) << 16);
        bufferPosition = pos;
        return Float.intBitsToFloat(v);
      }
        
      default:
        throw new IOException(text.get("wrongSubType", Integer.valueOf(subType), Long.valueOf(position())));
    }
  }
  
  private double getDouble(int subType) throws IOException {
    
    switch (subType) {

      case 0:
      case 1:
        return 0;
      
      case 2: {
        byte[] buf = ensureBuffer(8);
        int pos = bufferPosition;
        long v = ((long) (
                     ( buf[pos++]         << 24)
                   | ((buf[pos++] & 0xFF) << 16)
                   | ((buf[pos++] & 0xFF) << 8)
                   |  (buf[pos++] & 0xFF)
                 ) << 32)
               | (
                     ( buf[pos++]         << 24)
                   | ((buf[pos++] & 0xFF) << 16)
                   | ((buf[pos++] & 0xFF) << 8)
                   |  (buf[pos++] & 0xFF)
                 );
        bufferPosition = pos;
        return Double.longBitsToDouble(v);
      }
                
      case 3: {
        byte[] buf = ensureBuffer(7);
        int pos = bufferPosition;
        long v = ((long) (
                     ( buf[pos++]         << 24)
                   | ((buf[pos++] & 0xFF) << 16)
                   | ((buf[pos++] & 0xFF) << 8)
                   |  (buf[pos++] & 0xFF)
                 ) << 32)
               | (
                     ( buf[pos++]         << 24)
                   | ((buf[pos++] & 0xFF) << 16)
                   | ((buf[pos++] & 0xFF) << 8)
                 );
        bufferPosition = pos;
        return Double.longBitsToDouble(v);
      }
                
      case 4: {
        byte[] buf = ensureBuffer(6);
        int pos = bufferPosition;
        long v = ((long) (
                     ( buf[pos++]         << 24)
                   | ((buf[pos++] & 0xFF) << 16)
                   | ((buf[pos++] & 0xFF) << 8)
                   |  (buf[pos++] & 0xFF)
                 ) << 32)
               | (
                     ( buf[pos++]         << 24)
                   | ((buf[pos++] & 0xFF) << 16)
                 );
        bufferPosition = pos;
        return Double.longBitsToDouble(v);
      }
                
      case 5: {
        byte[] buf = ensureBuffer(5);
        int pos = bufferPosition;
        long v = ((long) (
                     ( buf[pos++]         << 24)
                   | ((buf[pos++] & 0xFF) << 16)
                   | ((buf[pos++] & 0xFF) << 8)
                   |  (buf[pos++] & 0xFF)
                 ) << 32)
               |     ( buf[pos++]         << 24);
        bufferPosition = pos;
        return Double.longBitsToDouble(v);
      }
                
      case 6: {
        byte[] buf = ensureBuffer(4);
        int pos = bufferPosition;
        long v = ((long) (
                     ( buf[pos++]         << 24)
                   | ((buf[pos++] & 0xFF) << 16)
                   | ((buf[pos++] & 0xFF) << 8)
                   |  (buf[pos++] & 0xFF)
                 ) << 32);
        bufferPosition = pos;
        return Double.longBitsToDouble(v);
      }
                
      case 7: {
        byte[] buf = ensureBuffer(3);
        int pos = bufferPosition;
        long v = ((long) (
                     ( buf[pos++]         << 24)
                   | ((buf[pos++] & 0xFF) << 16)
                   | ((buf[pos++] & 0xFF) << 8)
                 ) << 32);
        bufferPosition = pos;
        return Double.longBitsToDouble(v);
      }
                
      case 8: {
        byte[] buf = ensureBuffer(2);
        int pos = bufferPosition;
        long v = ((long) (
                     ( buf[pos++]         << 24)
                   | ((buf[pos++] & 0xFF) << 16)
                 ) << 32);
        bufferPosition = pos;
        return Double.longBitsToDouble(v);
      }
                
      default:
        throw new IOException(text.get("wrongSubType", Integer.valueOf(subType), Long.valueOf(position())));
    }
  }
  
  private byte[] getArray(int subType) throws IOException {
    
    int len = getInt(subType);
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
    
    int subType = expectType(0x10);
    switch (subType) {
      case 0:
        return null;
      case 1:
        return Boolean.FALSE;
      case 2:
        return Boolean.TRUE;
      default:
        throw new IOException(text.get("wrongSubType", Integer.valueOf(subType), "Boolean", Long.valueOf(position())));
    }
  }
  
  public boolean readBoolean() throws IOException {

    int subType = expectType(0x10);
    switch (subType) {
      case 0:
      case 1:
        return false;
      case 2:
        return true;
      default:
        throw new IOException(text.get("wrongSubType", Integer.valueOf(subType), "Boolean", Long.valueOf(position())));
    }
  }
  
  /**
   * Reads a <code>Byte</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Byte readByteObject() throws IOException {

    int subType = expectType(0x40);
    if (subType == 0) {
      return null;
    } else {
      return Byte.valueOf(getByte(subType));
    }
  }
  
  public byte readByte() throws IOException {

    return getByte(expectType(0x40));
  }
  
  /**
   * Reads a <code>Short</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Short readShortObject() throws IOException {

    int subType = expectType(0x50);
    if (subType == 0) {
      return null;
    } else {
      return Short.valueOf((short) getSmallInt(subType));
    }
  }
  
  public short readShort() throws IOException {
    
    return (short) getSmallInt(expectType(0x50));
  }
  
  /**
   * Reads a <code>Character</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Character readCharObject() throws IOException {

    int subType = expectType(0x60);
    if (subType == 0) {
      return null;
    } else {
      return Character.valueOf((char) getSmallInt(subType));
    }
  }
  
  public char readChar() throws IOException {
    
    return (char) getSmallInt(expectType(0x60));
  }
  
  /**
   * Reads an <code>Integer</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Integer readIntObject() throws IOException {

    int subType = expectType(0x70);
    if (subType == 0) {
      return null;
    } else {
      return Integer.valueOf(getInt(subType));
    }
  }
  
  public int readInt() throws IOException {
    
    return getInt(expectType(0x70));
  }
  
  /**
   * Reads a <code>Long<code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Long readLongObject() throws IOException {

    int subType = expectType(0x80);
    if (subType == 0) {
      return null;
    } else {
      return Long.valueOf(getLong(subType));
    }
  }
  
  public long readLong() throws IOException {

    return getLong(expectType(0x80));
  }
  
  /**
   * Reads a <code>Float</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Float readFloatObject() throws IOException {

    int subType = expectType(0x20);
    if (subType == 0) {
      return null;
    } else {
      return Float.valueOf(getFloat(subType));
    }
  }
  
  public float readFloat() throws IOException {

    return getFloat(expectType(0x20));
  }
  
  /**
   * Reads a <code>Double</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Double readDoubleObject() throws IOException {

    int subType = expectType(0x30);
    if (subType == 0) {
      return null;
    } else {
      return Double.valueOf(getDouble(subType));
    }
  }
  
  public double readDouble() throws IOException {
    
    return getDouble(expectType(0x30));
  }
  
  /**
   * Reads a <code>String</code>.
   * @return a string or <code>null</code>
   * @throws IOException
   */
  public String readString() throws IOException {

    int subType = expectType(0x90);
    switch (subType) {
      case 0:
        return null;
      case 1:
        return "";
    }

    int length = getInt(subType);
    
    char[] cb = getCharBuffer(length);
    int i = 0;
    
    int[] addIndex = additionalIndex;
    int addPos = 0;
    
    while (i < length) {
      
      byte[] buf = ensureBuffer(1);
      int pos = bufferPosition;
      int chunkLength = Math.min(length - i, bufferLimit - bufferPosition);
      int limit = i + chunkLength;
      
      while (i < limit) {
        byte b = buf[pos++];
        if ((b & 0x80) == 0x80) {
          if (addPos == addIndex.length) {
            addIndex = enlargeAdditionalIndex();
          }
          if ((b & 0xC0) == 0xC0) {
            addIndex[addPos++] = i | 0x80000000;
          } else {
            addIndex[addPos++] = i;
            charBuffer[i] = (char) ((b & 0x3F) << 8);
          }
        } else {
          charBuffer[i] = (char) (b & 0xFF);
        }
        i++;
      }
      
      bufferPosition = pos;
    }
    
    if (addPos > 0) {
      for (i = 0; i < addPos; i++) {
        int index = addIndex[i];
        if (index < 0) {
          byte[] buf = ensureBuffer(2);
          int pos = bufferPosition;
          charBuffer[index & 0x7FFFFFFF] = (char) (((buf[pos++] << 8) & 0xFF) | (buf[pos++] & 0xFF));
          bufferPosition = pos;
        } else {
          byte[] buf = ensureBuffer(1);
          charBuffer[index] |= buf[bufferPosition++] & 0xFF;
        }
      }
    }
    
    return new String(cb, 0, length);
  }
  
  private char[] getCharBuffer(int length) {
    
    if (charBuffer.length < length) {
      charBuffer = new char[length];
    }
    return charBuffer;
  }
  
  private int[] enlargeAdditionalIndex() {
    
    int[] newIndex = new int[additionalIndex.length << 1];
    System.arraycopy(additionalIndex, 0, newIndex, 0, additionalIndex.length);
    additionalIndex = newIndex;
    return newIndex;
  }
  
  /**
   * Reads a <code>Date</code>.
   * @return a date or <code>null</code>
   * @throws IOException
   */
  public Date readDate() throws IOException {

    int subType = expectType(0xC0);
    if (subType == 0) {
      return null;
    } else {
      return new Date(getLong(subType));
    }
  }
  
  /**
   * Reads a <code>BigInteger</code>.
   * @return a big integer or <code>null</code>
   * @throws IOException
   */
  public BigInteger readBigInteger() throws IOException {

    int subType = expectType(0xA0);
    switch (subType) {
      case 0:
        return null;
      case 1:
        return BigInteger.ZERO;
      default:
        return new BigInteger(getArray(subType));
    }
  }
  
  /**
   * Reads a <code>BigDecimal</code>.
   * @return a big decimal or <code>null</code>
   * @throws IOException
   */
  public BigDecimal readBigDecimal() throws IOException {

    int subType = expectType(0xB0);
    switch (subType) {
      case 0:
        return null;
      case 1:
        return BigDecimal.ZERO;
      default:
        return new BigDecimal(new BigInteger(getArray(subType)), readInt());
    }
  }
  
  /**
   * Reads a byte array which may be <code>null</code>.
   * @return a byte array or <code>null</code>
   * @throws IOException
   */
  public byte[] readByteArray() throws IOException {

    int subType = expectType(0xE0);
    switch (subType) {
      case 0:
        return null;
      case 1:
        return new byte[0];
      default:
        return getArray(subType);
    }
  }
  
  /**
   * Reads an <code>Enum</code> constant.
   * @param <E> enum type
   * @param clazz the <code>Enum</code> class
   * @return enum constant or <code>null</code>
   * @throws IOException
   */
  public <E extends Enum<E>> E readEnum(Class<E> clazz) throws IOException {

    int subType = expectType(0xD0);
    if (subType == 0) {
      return null;
    } else {
      return clazz.getEnumConstants()[getSmallInt(subType)];
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
    
    int subType = expectType(0xF0);
    switch (subType) {
      case 0:        
        return null;
      case 1:
      case 2:
        InputObjectHandler handler = handlerMap.get(clazz); 
        if (handler == null) {
          handler = getGeneratedHandler(clazz);
          handlerMap.put(clazz, handler);
        }
        return (D) handler.read(this, clazz);
      default:
        throw new IOException(text.get("wrongSubType", Integer.valueOf(subType), Long.valueOf(position())));
    }
  }
  
  private InputObjectHandler getGeneratedHandler(Class<?> clazz) throws Exception {
    
    int size = readShort();
    List<PropertyDescription> receivedProperties = new ArrayList<PropertyDescription>(size);
    for (int i = 0; i < size; i++) {
      String propertyName = readString();
      String propertyClazzName = readString();
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
