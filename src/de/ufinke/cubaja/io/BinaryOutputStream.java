// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  private final Map<Class<?>, OutputObjectHandler> handlerMap = new HashMap<Class<?>, OutputObjectHandler>();
  
  private byte[] buffer;
  private char[] charBuffer;
  
  /**
   * Constructor.
   * @param out the underlying <code>OutputStream</code>
   */
  public BinaryOutputStream(OutputStream out) {
    
    super(out);
    
    buffer = new byte[32];
    charBuffer = new char[32];
  }

  private byte[] ensureBuffer(int minSize) {
  
    if (buffer.length < minSize) {
      buffer = new byte[Math.max(buffer.length << 1, minSize)];
    }
    
    return buffer;
  }
  
  private char[] ensureCharBuffer(int minSize) {
    
    if (charBuffer.length < minSize) {
      charBuffer = new char[Math.max(buffer.length << 1, minSize)];
    }
    
    return charBuffer;
  }
  
  private void putSmallInt(int mask, int value) throws IOException {
    
    byte[] buf = buffer;
    int pos = 0;
    
    if (value == 0) {
      buf[pos++] = (byte) (mask | 0x01);
    } else if ((value & 0xFF00) == 0x0000) {      
      buf[pos++] = (byte) (mask | 0x03);
      buf[pos++] = (byte) value;
    } else if ((value & 0xFF00) == 0xFF00) {      
      buf[pos++] = (byte) (mask | 0x04);
      buf[pos++] = (byte) value;
    } else {
      buf[pos++] = (byte) (mask | 0x02);
      buf[pos++] = (byte) (value >>> 8);
      buf[pos++] = (byte) value;      
    }
    
    write(buf, 0, pos);
  }
  
  private void putInt(int mask, int value) throws IOException {
    
    byte[] buf = buffer;
    int pos = 0;
    
    if (value == 0) {
      buf[pos++] = (byte) (mask | 0x01);
    // positive with leading x00 ?
    } else if ((value & 0xFF000000) == 0x00000000) {
      if ((value & 0xFFFF0000) == 0x00000000) {
        if ((value & 0xFFFFFF00) == 0x00000000) {
          buf[pos++] = (byte) (mask | 0x05);
          buf[pos++] = (byte) value;
        } else {
          buf[pos++] = (byte) (mask | 0x04);
          buf[pos++] = (byte) (value >>> 8);
          buf[pos++] = (byte) value;
        }
      } else {
        buf[pos++] = (byte) (mask | 0x03);
        buf[pos++] = (byte) (value >>> 16);
        buf[pos++] = (byte) (value >>> 8);
        buf[pos++] = (byte) value;
      }
    // negative with leading xFF ?
    } else if ((value & 0xFF000000) == 0xFF000000) {
      if ((value & 0xFFFF0000) == 0xFFFF0000) {
        if ((value & 0xFFFFFF00) == 0xFFFFFF00) {
          buf[pos++] = (byte) (mask | 0x08);
          buf[pos++] = (byte) value;
        } else {
          buf[pos++] = (byte) (mask | 0x07);
          buf[pos++] = (byte) (value >>> 8);
          buf[pos++] = (byte) value;
        }
      } else {
        buf[pos++] = (byte) (mask | 0x06);
        buf[pos++] = (byte) (value >>> 16);
        buf[pos++] = (byte) (value >>> 8);
        buf[pos++] = (byte) value;
      }
    // write fully
    } else {
      buf[pos++] = (byte) (mask | 0x02);
      buf[pos++] = (byte) (value >>> 24);
      buf[pos++] = (byte) (value >>> 16);
      buf[pos++] = (byte) (value >>> 8);
      buf[pos++] = (byte) value;
    }
    
    write(buf, 0, pos);
  }
  
  private void putLong(int mask, long value) throws IOException {
    
    byte[] buf = buffer;
    int pos = 0;
    
    if (value == 0) {
      buf[pos++] = (byte) (mask | 0x01);
    } else {
      int highBytes = (int) (value >>> 32);
      int lowBytes = (int) value;
      // positive with leading x00 ?
      if ((highBytes & 0xFF000000) == 0x00000000) {  
        if ((highBytes & 0xFFFF0000) == 0x00000000) {
          if ((highBytes & 0xFFFFFF00) == 0x00000000) {
            if ((highBytes & 0xFFFFFFFF) == 0x00000000) {
              if ((lowBytes & 0xFF000000) == 0x00000000) {
                if ((lowBytes & 0xFFFF0000) == 0x00000000) {
                  if ((lowBytes & 0xFFFFFF00) == 0x00000000) {
                    buf[pos++] = (byte) (mask | 0x09);
                    buf[pos++] = (byte) lowBytes;
                  } else {
                    buf[pos++] = (byte) (mask | 0x08);
                    buf[pos++] = (byte) (lowBytes >>> 8);
                    buf[pos++] = (byte) lowBytes;
                  }
                } else {
                  buf[pos++] = (byte) (mask | 0x07);
                  buf[pos++] = (byte) (lowBytes >>> 16);
                  buf[pos++] = (byte) (lowBytes >>> 8);
                  buf[pos++] = (byte) lowBytes;
                }
              } else {
                buf[pos++] = (byte) (mask | 0x06);
                buf[pos++] = (byte) (lowBytes >>> 24);
                buf[pos++] = (byte) (lowBytes >>> 16);
                buf[pos++] = (byte) (lowBytes >>> 8);
                buf[pos++] = (byte) lowBytes;
              }
            } else {
              buf[pos++] = (byte) (mask | 0x05);
              buf[pos++] = (byte) highBytes;
              buf[pos++] = (byte) (lowBytes >>> 24);
              buf[pos++] = (byte) (lowBytes >>> 16);
              buf[pos++] = (byte) (lowBytes >>> 8);
              buf[pos++] = (byte) lowBytes;
            }
          } else {
            buf[pos++] = (byte) (mask | 0x04);
            buf[pos++] = (byte) (highBytes >>> 8);
            buf[pos++] = (byte) highBytes;
            buf[pos++] = (byte) (lowBytes >>> 24);
            buf[pos++] = (byte) (lowBytes >>> 16);
            buf[pos++] = (byte) (lowBytes >>> 8);
            buf[pos++] = (byte) lowBytes;
          }
        } else {
          buf[pos++] = (byte) (mask | 0x03);
          buf[pos++] = (byte) (highBytes >>> 16);
          buf[pos++] = (byte) (highBytes >>> 8);
          buf[pos++] = (byte) highBytes;
          buf[pos++] = (byte) (lowBytes >>> 24);
          buf[pos++] = (byte) (lowBytes >>> 16);
          buf[pos++] = (byte) (lowBytes >>> 8);
          buf[pos++] = (byte) lowBytes;
        }
      // negative with leading xFF ?
      } else if ((highBytes & 0xFFFF0000) == 0xFFFF0000) {
        if ((highBytes & 0xFFFFFF00) == 0xFFFFFF00) {
          if ((highBytes & 0xFFFFFFFF) == 0xFFFFFFFF) {
            if ((lowBytes & 0xFF000000) == 0xFF000000) {
              if ((lowBytes & 0xFFFF0000) == 0xFFFF0000) {
                if ((lowBytes & 0xFFFFFF00) == 0xFFFFFF00) {
                  buf[pos++] = (byte) (mask | 0x0F);
                  buf[pos++] = (byte) lowBytes;
                } else {
                  buf[pos++] = (byte) (mask | 0x0E);
                  buf[pos++] = (byte) (lowBytes >>> 8);
                  buf[pos++] = (byte) lowBytes;
                }
              } else {
                buf[pos++] = (byte) (mask | 0x0D);
                buf[pos++] = (byte) (lowBytes >>> 16);
                buf[pos++] = (byte) (lowBytes >>> 8);
                buf[pos++] = (byte) lowBytes;
              }
            } else {
              buf[pos++] = (byte) (mask | 0x0C);
              buf[pos++] = (byte) (lowBytes >>> 24);
              buf[pos++] = (byte) (lowBytes >>> 16);
              buf[pos++] = (byte) (lowBytes >>> 8);
              buf[pos++] = (byte) lowBytes;
            }
          } else {
            buf[pos++] = (byte) (mask | 0x0B);
            buf[pos++] = (byte) highBytes;
            buf[pos++] = (byte) (lowBytes >>> 24);
            buf[pos++] = (byte) (lowBytes >>> 16);
            buf[pos++] = (byte) (lowBytes >>> 8);
            buf[pos++] = (byte) lowBytes;
          }
        } else {
          buf[pos++] = (byte) (mask | 0x0A);
          buf[pos++] = (byte) (highBytes >>> 8);
          buf[pos++] = (byte) highBytes;
          buf[pos++] = (byte) (lowBytes >>> 24);
          buf[pos++] = (byte) (lowBytes >>> 16);
          buf[pos++] = (byte) (lowBytes >>> 8);
          buf[pos++] = (byte) lowBytes;
        }
      // write fully
      } else {
        buf[pos++] = (byte) (mask | 0x02);
        buf[pos++] = (byte) (highBytes >>> 24);
        buf[pos++] = (byte) (highBytes >>> 16);
        buf[pos++] = (byte) (highBytes >>> 8);
        buf[pos++] = (byte) highBytes;
        buf[pos++] = (byte) (lowBytes >>> 24);
        buf[pos++] = (byte) (lowBytes >>> 16);
        buf[pos++] = (byte) (lowBytes >>> 8);
        buf[pos++] = (byte) lowBytes;
      }
    }
    
    write(buf, 0, pos);
  }
  
  /**
   * Writes a <code>Boolean</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeBooleanObject(Boolean value) throws IOException {
    
    if (value == null) {
      write(0x10);
    } else {
      writeBoolean(value.booleanValue());
    }
  }
  
  public void writeBoolean(boolean value) throws IOException {
    
    write(value ? 0x12 : 0x11);
  }

  /**
   * Writes a <code>Byte</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeByteObject(Byte value) throws IOException {
    
    if (value == null) {
      write(0x40);
    } else {
      writeByte(value.byteValue());
    }
  }
  
  public void writeByte(int value) throws IOException {
    
    byte[] buf = buffer;
    int pos = 0;
    
    if (value == 0) {
      buf[pos++] = (byte) 0x41;
    } else {      
      buf[pos++] = (byte) 0x42;
      buf[pos++] = (byte) value;
    }

    write(buf, 0, pos);
  }
  
  /**
   * Writes a <code>Short</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeShortObject(Short value) throws IOException {
    
    if (value == null) {
      write(0x50);
    } else {
      putSmallInt(0x50, value.shortValue());
    }
  }
  
  public void writeShort(int value) throws IOException {

    putSmallInt(0x50, value);
  }
  
  /**
   * Writes a <code>Character</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeCharObject(Character value) throws IOException {
    
    if (value == null) {
      write(0x60);
    } else {
      putSmallInt(0x60, value.charValue());
    }
  }
  
  public void writeChar(int value) throws IOException {

    putSmallInt(0x60, value);
  }
  
  /**
   * Writes an <code>Integer</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeIntObject(Integer value) throws IOException {
    
    if (value == null) {
      write(0x70);
    } else {
      putInt(0x70, value.intValue());
    }
  }
  
  public void writeInt(int value) throws IOException {
    
    putInt(0x70, value);
  }
  
  /**
   * Writes a <code>Long</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeLongObject(Long value) throws IOException {
    
    if (value == null) {
      write(0x80);
    } else {
      putLong(0x80, value.longValue());
    }
  }
  
  public void writeLong(long value) throws IOException {

    putLong(0x80, value);
  }
  
  /**
   * Writes a <code>Float</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeFloatObject(Float value) throws IOException {
    
    if (value == null) {
      write(0x20);
    } else {
      writeFloat(value.floatValue());
    }
  }
  
  public void writeFloat(float value) throws IOException {

    byte[] buf = buffer;
    int pos = 0;
    
    if (value == 0) {
      buf[pos++] = (byte) 0x21;
    } else {      
      int iValue = Float.floatToRawIntBits(value);
      if ((iValue & 0x000000FF) == 0) {
        if ((iValue & 0x0000FFFF) == 0) {
          buf[pos++] = (byte) 0x24;
          buf[pos++] = (byte) (iValue >>> 24);
          buf[pos++] = (byte) (iValue >>> 16);
        } else {
          buf[pos++] = (byte) 0x23;
          buf[pos++] = (byte) (iValue >>> 24);
          buf[pos++] = (byte) (iValue >>> 16);
          buf[pos++] = (byte) (iValue >>> 8);
        }
      } else {
        buf[pos++] = (byte) 0x22;
        buf[pos++] = (byte) (iValue >>> 24);
        buf[pos++] = (byte) (iValue >>> 16);
        buf[pos++] = (byte) (iValue >>> 8);
        buf[pos++] = (byte) iValue;
      }
    }

    write(buf, 0, pos);
  }
  
  /**
   * Writes a <code>Double</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeDoubleObject(Float value) throws IOException {
    
    if (value == null) {
      write(0x30);
    } else {
      writeDouble(value.doubleValue());
    }
  }
  
  public void writeDouble(double value) throws IOException {

    byte[] buf = buffer;
    int pos = 0;
    
    if (value == 0) {
      buf[pos++] = (byte) 0x31;
    } else {      
      long longValue = Double.doubleToRawLongBits(value);
      int highBytes = (int) (longValue >>> 32);
      int lowBytes = (int) longValue;
      if ((lowBytes & 0x000000FF) == 0) {
        if ((lowBytes & 0x0000FFFF) == 0) {
          if ((lowBytes & 0x00FFFFFF) == 0) {
            if ((lowBytes & 0xFFFFFFFF) == 0) {
              if ((highBytes & 0x000000FF) == 0) {
                if ((highBytes & 0x0000FFFF) == 0) {
                  buf[pos++] = (byte) 0x38;
                  buf[pos++] = (byte) (highBytes >>> 24);
                  buf[pos++] = (byte) (highBytes >>> 16);
                } else {
                  buf[pos++] = (byte) 0x37;
                  buf[pos++] = (byte) (highBytes >>> 24);
                  buf[pos++] = (byte) (highBytes >>> 16);
                  buf[pos++] = (byte) (highBytes >>> 8);
                }
              } else {
                buf[pos++] = (byte) 0x36;
                buf[pos++] = (byte) (highBytes >>> 24);
                buf[pos++] = (byte) (highBytes >>> 16);
                buf[pos++] = (byte) (highBytes >>> 8);
                buf[pos++] = (byte) highBytes;
              }
            } else {
              buf[pos++] = (byte) 0x35;
              buf[pos++] = (byte) (highBytes >>> 24);
              buf[pos++] = (byte) (highBytes >>> 16);
              buf[pos++] = (byte) (highBytes >>> 8);
              buf[pos++] = (byte) highBytes;
              buf[pos++] = (byte) (lowBytes >>> 24);
            }
          } else {
            buf[pos++] = (byte) 0x34;
            buf[pos++] = (byte) (highBytes >>> 24);
            buf[pos++] = (byte) (highBytes >>> 16);
            buf[pos++] = (byte) (highBytes >>> 8);
            buf[pos++] = (byte) highBytes;
            buf[pos++] = (byte) (lowBytes >>> 24);
            buf[pos++] = (byte) (lowBytes >>> 16);
          }
        } else {
          buf[pos++] = (byte) 0x33;
          buf[pos++] = (byte) (highBytes >>> 24);
          buf[pos++] = (byte) (highBytes >>> 16);
          buf[pos++] = (byte) (highBytes >>> 8);
          buf[pos++] = (byte) highBytes;
          buf[pos++] = (byte) (lowBytes >>> 24);
          buf[pos++] = (byte) (lowBytes >>> 16);
          buf[pos++] = (byte) (lowBytes >>> 8);
        }
      } else {
        buf[pos++] = (byte) 0x32;
        buf[pos++] = (byte) (highBytes >>> 24);
        buf[pos++] = (byte) (highBytes >>> 16);
        buf[pos++] = (byte) (highBytes >>> 8);
        buf[pos++] = (byte) highBytes;
        buf[pos++] = (byte) (lowBytes >>> 24);
        buf[pos++] = (byte) (lowBytes >>> 16);
        buf[pos++] = (byte) (lowBytes >>> 8);
        buf[pos++] = (byte) lowBytes;
      }
    }

    write(buf, 0, pos);
  }
  
  /**
   * Writes a <code>String</code>.
   * @param value a string or other kind of <code>CharSequence</code>
   * @throws IOException
   */
  public void writeString(CharSequence value) throws IOException {
    
    if (value == null) {
      write(0x90);
      return;
    }
    
    int length = value.length();
    
    if (length == 0) {
      write(0x91);
      return;
    }

    char[] charBuf = ensureCharBuffer(length);
    int limit = length;
    for (int i = 0; i < length; i++) {
      char c = value.charAt(i);
      charBuf[i] = c;
      if ((c & 0xFF80) != 0) {
        if ((c & 0xC000) != 0) {
          limit += 2;
        } else {
          limit++;
        }
      }
    }
    
    byte[] buf = ensureBuffer(limit);
    int pos = 0;
    
    if (limit == length) { // ascii
      putInt(0x90, length);
      for (int i = 0; i < length; i++) {
        buf[i] = (byte) charBuf[i];
      }
      pos = length;
    } else if (limit > (length << 1)) { // char array
      putInt(0x98, length);
      for (int i = 0; i < length; i++) {
        buf[pos++] = (byte) (charBuf[i] >>> 8);
        buf[pos++] = (byte) charBuf[i];
      }
    } else { // compressed
      putInt(0x90, limit * -1);
      for (int i = 0; i < length; i++) {
        char c = charBuf[i];
        if ((c & 0xFF80) == 0) {
          buf[pos++] = (byte) c;
        } else if ((c & 0xC000) == 0) {
          buf[pos++] = (byte) (0x80 | (c >>> 8));
          buf[pos++] = (byte) c;
        } else {
          buf[pos++] = (byte) 0xC0;
          buf[pos++] = (byte) (charBuf[i] >>> 8);
          buf[pos++] = (byte) charBuf[i];
        }
      }
    }
    
    write(buf, 0, pos);
  }
  
  /**
   * Writes a <code>Date</code>.
   * @param value a date
   * @throws IOException
   */
  public void writeDate(Date value) throws IOException {
    
    if (value == null) {
      write(0xC0);
    } else {
      putLong(0xC0, value.getTime());
    }
  }
  
  /**
   * Writes a <code>BigInteger</code>.
   * @param value a big integer
   * @throws IOException
   */
  public void writeBigInteger(BigInteger value) throws IOException {
    
    if (value == null) {
      write(0xA0);
    } else if (value.equals(BigInteger.ZERO)) {
      write(0xA1);
    } else {
      writeNonNullByteArray(0xA0, value.toByteArray());
    }
  }
  
  /**
   * Writes a <code>BigDecimal</code>.
   * @param value a big decimal
   * @throws IOException
   */
  public void writeBigDecimal(BigDecimal value) throws IOException {
    
    if (value == null) {
      write(0xB0);
    } else if (value.equals(BigDecimal.ZERO)) {
      write(0xB1);
    } else {
      writeNonNullByteArray(0xB0, value.unscaledValue().toByteArray());
      writeInt(value.scale());
    }
  }
  
  /**
   * Writes a byte array which may be <code>null</code>.
   * @param value a byte array
   * @throws IOException
   */
  public void writeByteArray(byte[] value) throws IOException {
    
    if (value == null) {
      write(0xE0);
    } else if (value.length == 0) {
      write(0xE1);
    } else {
      writeNonNullByteArray(0xE0, value);
    }
  }
  
  private void writeNonNullByteArray(int mask, byte[] value) throws IOException {

    putInt(mask, value.length);
    write(value, 0, value.length);
  }
  
  /**
   * Writes an <code>Enum</code> constant.
   * @param value an enum constant
   * @throws IOException
   */
  public void writeEnum(Enum<?> value) throws IOException {
    
    if (value == null) {
      write(0xD0);
    } else {
      putSmallInt(0xD0, value.ordinal());
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
    
    if (object == null) {
      write(0xF0);
      return;
    }
    
    OutputObjectHandler handler = handlerMap.get(object.getClass()); 
    
    if (handler == null) {
      write(0xF2);
      handler = getGeneratedHandler(object.getClass());
      handlerMap.put(object.getClass(), handler);
    } else {
      write(0xF1);      
    }
    
    handler.write(this, object);
  }
  
  private OutputObjectHandler getGeneratedHandler(Class<?> clazz) throws Exception {
    
    PropertyClassAnalyzer analyzer = new PropertyClassAnalyzer(clazz);
    List<PropertyDescription> properties = analyzer.getPropertyList();
    
    writeShort(properties.size());
    for (PropertyDescription property : properties) {
      writeString(property.getName());
      writeString(property.getClazz().getName());
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
