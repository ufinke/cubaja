// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.DataInput;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public interface BinaryInput extends DataInput {

  /**
   * Reads a <code>Boolean</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Boolean readBooleanObject() throws IOException;
  
  /**
   * Reads a <code>Byte</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Byte readByteObject() throws IOException;
  
  /**
   * Reads a <code>Short</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Short readShortObject() throws IOException;  
  /**
   * Reads a <code>Character</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  
  public Character readCharObject() throws IOException;
  
  /**
   * Reads an <code>Integer</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Integer readIntObject() throws IOException;
  
  /**
   * Reads a <code>Long<code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Long readLongObject() throws IOException;
  
  /**
   * Reads a <code>Float</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Float readFloatObject() throws IOException;
  
  /**
   * Reads a <code>Double</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Double readDoubleObject() throws IOException;
  
  /**
   * Reads a <code>String</code>.
   * @return a string or <code>null</code>
   * @throws IOException
   */
  public String readString() throws IOException;
  
  /**
   * Reads a <code>Date</code>.
   * @return a date or <code>null</code>
   * @throws IOException
   */
  public Date readDate() throws IOException;
  
  /**
   * Reads a <code>BigInteger</code>.
   * @return a big integer or <code>null</code>
   * @throws IOException
   */
  public BigInteger readBigInteger() throws IOException;
  
  /**
   * Reads a <code>BigDecimal</code>.
   * @return a big decimal or <code>null</code>
   * @throws IOException
   */
  public BigDecimal readBigDecimal() throws IOException;
  
  /**
   * Reads a byte array which may be <code>null</code>.
   * @return a byte array or <code>null</code>
   * @throws IOException
   */
  public byte[] readByteArray() throws IOException;
    
  /**
   * Reads an <code>Enum</code> constant.
   * @param <E> enum type
   * @param clazz the <code>Enum</code> class
   * @return enum constant or <code>null</code>
   * @throws IOException
   */
  public <E extends Enum<E>> E readEnum(Class<E> clazz) throws IOException;
  
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
  public <D> D readObject(Class<? extends D> clazz) throws Exception;
    
  /**
   * Adds an object handler for a data class type.
   * @param clazz data class type
   * @param handler handler
   */
  public void addObjectHandler(Class<?> clazz, InputObjectHandler handler); 
}
