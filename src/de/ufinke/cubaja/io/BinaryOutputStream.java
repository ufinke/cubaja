// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.util.*;

/**
 * Writes primitive types and objects to an <code>OutputStream</code>.
 * <p>
 * If an object is <code>null</code>, only an indicator byte with value <code>0</code>
 * will be written. Otherwise, the indicator byte is written with value <code>1</code>,
 * followed by the object value as with <code>DataOutputStream</code>.
 * A <code>String</code> is written in UTF8 format,
 * a <code>Date</code> as <code>long</code>,
 * a <code>BigInteger</code> as byte array,
 * a <code>BigDecimal</code> as byte array followed by an <code>int</code> representing the scale
 * and an <code>Enum</code> as an <code>int</code> representing its ordinal number. 
 * </p>
 * <p>
 * The written data can be read
 * by <code>BinaryInputStream</code>.
 * </p> 
 * @author Uwe Finke
 */
public class BinaryOutputStream extends FilterOutputStream {

  static private final int DEFAULT_BUFFER_SIZE = 8192;
  static private final int MIN_BUFFER_SIZE = 64;
  
  static private final Text text = new Text(BinaryOutputStream.class);
  
  private final Map<Class<?>, OutputObjectHandler> handlerMap = new HashMap<Class<?>, OutputObjectHandler>();
  
  private int bufferSize;
  private long streamPosition;
  private byte[] buffer;
  private int bufferPosition;
  
  /**
   * Constructor.
   * @param out the underlying <code>OutputStream</code>
   */
  public BinaryOutputStream(OutputStream out) {
    
    this(out, DEFAULT_BUFFER_SIZE);
  }
  
  public BinaryOutputStream(OutputStream out, int bufferSize) {
    
    super(out);
    this.bufferSize = Math.max(bufferSize, MIN_BUFFER_SIZE);
    buffer = new byte[this.bufferSize];
  }
  
  private byte[] ensureBuffer(int requestedLength) throws IOException {
  
    if (requestedLength > bufferSize - bufferPosition) {
      writeBuffer();
    }
    return buffer;
  }
  
  private void writeBuffer() throws IOException {
    
    if (bufferPosition == 0) {
      return;
    }
    
    streamPosition += bufferPosition;
    out.write(buffer, 0, bufferPosition);
    bufferPosition = 0;
  }
  
  public void close() throws IOException {
    
    writeBuffer();
    out.close();
  }
  
  public void flush() throws IOException {
    
    writeBuffer();
    out.flush();
  }
  
  public void write(int value) throws IOException {

    byte[] buf = ensureBuffer(1);
    buf[bufferPosition++] = (byte) value;
  }
  
  public void write(byte[] value, int offset, int length) throws IOException {

    if (length <= bufferSize) {
      byte[] buf = ensureBuffer(length);
      System.arraycopy(value, offset, buf, bufferPosition, length);
      bufferPosition += length;
      return;
    }
    
    writeBuffer();
    out.write(value, offset, length);
    streamPosition += length;
  }
  
  public long position() {
    
    return streamPosition + bufferPosition;
  }
  
  /**
   * Writes a <code>Boolean</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeBooleanObject(Boolean value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeBoolean(value.booleanValue());
    }
  }
  
  public void writeBoolean(boolean value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    buf[bufferPosition++] = (byte) (value ? 1 : 0);
  }

  /**
   * Writes a <code>Byte</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeByteObject(Byte value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeByte(value.byteValue());
    }
  }
  
  public void writeByte(int value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    buf[bufferPosition++] = (byte) value;
  }
  
  /**
   * Writes a <code>Short</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeShortObject(Short value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeShort(value.shortValue());
    }
  }
  
  public void writeShort(int value) throws IOException {
    
    byte[] buf = ensureBuffer(2);
    int pos = bufferPosition;
    buf[pos++] = (byte) (value >>> 8);
    buf[pos++] = (byte) value;
    bufferPosition = pos;
  }
  
  /**
   * Writes a <code>Character</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeCharObject(Character value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeChar(value.charValue());
    }
  }
  
  public void writeChar(int value) throws IOException {
    
    byte[] buf = ensureBuffer(2);
    int pos = bufferPosition;
    buf[pos++] = (byte) (value >>> 8);
    buf[pos++] = (byte) value;
    bufferPosition = pos;
  }
  
  /**
   * Writes an <code>Integer</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeIntObject(Integer value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeInt(value.intValue());
    }
  }
  
  public void writeInt(int value) throws IOException {
    
    byte[] buf = ensureBuffer(4);
    int pos = bufferPosition;
    buf[pos++] = (byte) (value >>> 24);
    buf[pos++] = (byte) (value >>> 16);
    buf[pos++] = (byte) (value >>> 8);
    buf[pos++] = (byte) value;
    bufferPosition = pos;
  }
  
  /**
   * Writes a <code>Long</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeLongObject(Long value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeLong(value.longValue());
    }
  }
  
  public void writeLong(long value) throws IOException {
    
    byte[] buf = ensureBuffer(8);
    int pos = bufferPosition;
    buf[pos++] = (byte) (value >>> 56);
    buf[pos++] = (byte) (value >>> 48);
    buf[pos++] = (byte) (value >>> 40);
    buf[pos++] = (byte) (value >>> 32);
    buf[pos++] = (byte) (value >>> 24);
    buf[pos++] = (byte) (value >>> 16);
    buf[pos++] = (byte) (value >>> 8);
    buf[pos++] = (byte) value;
    bufferPosition = pos;
  }
  
  /**
   * Writes a <code>Float</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeFloatObject(Float value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeFloat(value.floatValue());
    }
  }
  
  public void writeFloat(float value) throws IOException {
    
    writeInt(Float.floatToRawIntBits(value));
  }
  
  /**
   * Writes a <code>Double</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeDoubleObject(Double value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeDouble(value.doubleValue());
    }
  }
  
  public void writeDouble(double value) throws IOException {
    
    writeLong(Double.doubleToRawLongBits(value));
  }
  
  /**
   * Writes a <code>String</code>.
   * @param value a string or other kind of <code>CharSequence</code>
   * @throws IOException
   */
  public void writeString(CharSequence value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeUTF(value);
    }
  }
  
  /**
   * Writes a character sequence using a modified UTF-8 encoding
   * in a machine independent manner.
   * The essential part of the 
   * encoding algorithm is a modified copy of
   * the appropriate <code>java.io.DataOutputStream</code> method.
   * @param value
   * @throws IOException
   */
  private void writeUTF(CharSequence value) throws IOException {
    
    int length = value.length();
    int utfLength = length * 3;
    boolean ownBuffer = utfLength + 2 > bufferSize;
    byte[] utf = ownBuffer ? new byte[utfLength] : ensureBuffer(utfLength + 2);
    int utfPos = ownBuffer ? 0 : bufferPosition + 2;

    for (int i = 0; i < length; i++) {
      char c = value.charAt(i);
      if ((c >= 0x0001) && (c <= 0x007F)) {
        utf[utfPos++] = (byte) c;
      } else if (c <= 0x07FF) {
        utf[utfPos++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
        utf[utfPos++] = (byte) (0x80 | ((c >>  0) & 0x3F));
      } else {
        utf[utfPos++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
        utf[utfPos++] = (byte) (0x80 | ((c >>  6) & 0x3F));
        utf[utfPos++] = (byte) (0x80 | ((c >>  0) & 0x3F));
      }
    }
    
    if (ownBuffer) {      
      if (utfPos > Character.MAX_VALUE) {
        throw new UTFDataFormatException(text.get("utfTooLong"));
      }
      writeChar(utfPos);
      write(utf, 0, utfPos);
    } else {
      int pos = bufferPosition;
      length = utfPos - pos - 2;
      utf[pos]     = (byte) (length >>> 8);
      utf[pos + 1] = (byte) length;
      bufferPosition = pos + 2 + length;
    }
  }
  
  /**
   * Writes a <code>Date</code>.
   * @param value a date
   * @throws IOException
   */
  public void writeDate(Date value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeLong(value.getTime());
    }
  }
  
  /**
   * Writes a <code>BigInteger</code>.
   * @param value a big integer
   * @throws IOException
   */
  public void writeBigInteger(BigInteger value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeNonNullByteArray(value.toByteArray());
    }
  }
  
  /**
   * Writes a <code>BigDecimal</code>.
   * @param value a big decimal
   * @throws IOException
   */
  public void writeBigDecimal(BigDecimal value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeNonNullByteArray(value.unscaledValue().toByteArray());
      writeInt(value.scale());
    }
  }
  
  /**
   * Writes a byte array which may be <code>null</code>.
   * @param value a byte array
   * @throws IOException
   */
  public void writeByteArray(byte[] value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeNonNullByteArray(value);
    }
  }
  
  private void writeNonNullByteArray(byte[] value) throws IOException {

    write(value, 0, value.length);
  }
  
  /**
   * Writes an <code>Enum</code> constant.
   * @param value an enum constant
   * @throws IOException
   */
  public void writeEnum(Enum<?> value) throws IOException {
    
    byte[] buf = ensureBuffer(1);
    if (value == null) {
      buf[bufferPosition++] = 0;
    } else {
      buf[bufferPosition++] = 1;
      writeChar(value.ordinal());
    }
  }
  
  /**
   * Writes any object.
   * <p/>
   * The object is written by an <code>OutputObjectHandler</code>.
   * We can provide our own individual handlers with the <code>addObjectHandler</code> method.
   * <p/>
   * If we do not provide an appropriate handler for a data class,
   * a factory will generate a handler automatically.
   * An automatically generated handler calls all getter methods of a data object
   * when there is a matching setter method with the same parameter type
   * as the getter return type.
   * When there is an unknown parameter type, the <code>writeObject</code>
   * method is called recursively.
   * @param object a data object
   * @throws Exception
   */
  public void writeObject(Object object) throws Exception {
    
    byte[] buf = ensureBuffer(1);
    
    if (object == null) {
      buf[bufferPosition++] = 0;
      return;
    }
    
    buf[bufferPosition++] = 1;
    
    OutputObjectHandler handler = handlerMap.get(object.getClass()); 
    
    if (handler == null) {
      handler = getGeneratedHandler(object.getClass());
      handlerMap.put(object.getClass(), handler);
    }
    
    handler.write(this, object);
  }
  
  private OutputObjectHandler getGeneratedHandler(Class<?> clazz) throws Exception {
    
    PropertyClassAnalyzer analyzer = new PropertyClassAnalyzer(clazz);
    List<PropertyDescription> properties = analyzer.getPropertyList();
    
    writeShort(properties.size());
    for (PropertyDescription property : properties) {
      writeUTF(property.getName());
      writeUTF(property.getClazz().getName());
    }
    
    return OutputObjectHandlerFactory.getHandler(clazz, properties);
  }
  
  /**
   * Adds an object handler for a data class type.
   * @param clazz data class type
   * @param handler handler
   */
  public void addObjectHandler(Class<?> clazz, OutputObjectHandler handler) {
    
    handlerMap.put(clazz, handler);
  }
}
