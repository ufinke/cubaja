// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <code>DataOutputStream</code> extension for handling common object types.
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
public class BinaryOutputStream extends DataOutputStream {

  private final Map<Class<?>, OutputObjectHandler> handlerMap = new HashMap<Class<?>, OutputObjectHandler>();
  
  /**
   * Constructor.
   * @param out the underlying <code>OutputStream</code>
   */
  public BinaryOutputStream(OutputStream out) {
    
    super(out);
  }
  
  /**
   * Writes a <code>Boolean</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeBooleanObject(Boolean value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {
      write(1);
      writeBoolean(value.booleanValue());
    }
  }

  /**
   * Writes a <code>Byte</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeByteObject(Byte value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {
      write(1);
      writeByte(value.byteValue());
    }
  }
  
  /**
   * Writes a <code>Short</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeShortObject(Short value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {
      write(1);
      writeShort(value.shortValue());
    }
  }
  
  /**
   * Writes a <code>Character</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeCharObject(Character value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {
      write(1);
      writeChar(value.charValue());
    }
  }
  
  /**
   * Writes an <code>Integer</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeIntObject(Integer value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {
      write(1);
      writeInt(value.intValue());
    }
  }
  
  /**
   * Writes a <code>Long</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeLongObject(Long value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {
      write(1);
      writeLong(value.longValue());
    }
  }
  
  /**
   * Writes a <code>Float</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeFloatObject(Float value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {
      write(1);
      writeFloat(value.floatValue());
    }
  }
  
  /**
   * Writes a <code>Double</code> object.
   * @param value object
   * @throws IOException
   */
  public void writeDoubleObject(Double value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {
      write(1);
      writeDouble(value.doubleValue());
    }
  }
  
  /**
   * Writes a <code>String</code>.
   * @param value a string
   * @throws IOException
   */
  public void writeString(String value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {
      write(1);
      writeUTF(value);
    }
  }
  
  /**
   * Writes a <code>Date</code>.
   * @param value a date
   * @throws IOException
   */
  public void writeDate(Date value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {
      write(1);
      writeLong(value.getTime());
    }
  }
  
  /**
   * Writes a <code>BigInteger</code>.
   * @param value a big integer
   * @throws IOException
   */
  public void writeBigInteger(BigInteger value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {
      write(1);
      writeNonNullByteArray(value.toByteArray());
    }
  }
  
  /**
   * Writes a <code>BigDecimal</code>.
   * @param value a big decimal
   * @throws IOException
   */
  public void writeBigDecimal(BigDecimal value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {      
      write(1);    
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
    
    if (value == null) {
      write(0);
    } else {
      write(1);
      writeNonNullByteArray(value);
    }
  }
  
  private void writeNonNullByteArray(byte[] value) throws IOException {

    writeInt(value.length);
    write(value);
  }
  
  /**
   * Writes an <code>Enum</code> constant.
   * @param value an enum constant
   * @throws IOException
   */
  public void writeEnum(Enum<?> value) throws IOException {
    
    if (value == null) {
      write(0);
    } else {
      write(1);
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
    
    if (object == null) {
      write(0);
      return;
    }
    
    write(1);
    
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
