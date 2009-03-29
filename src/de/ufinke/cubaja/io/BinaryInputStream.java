package de.ufinke.cubaja.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * <code>DataInputStream</code> extension for handling common object types.
 * The input data must have been written
 * by <code>BinaryOutputStream</code>. 
 * @author Uwe Finke
 */
public class BinaryInputStream extends DataInputStream {

  /**
   * Constructor.
   * @param in the underlying <code>InputStream</code>
   */
  public BinaryInputStream(InputStream in) {
    
    super(in);
  }
  
  /**
   * Reads a <code>Boolean</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Boolean readBooleanObject() throws IOException {
    
    if (read() == 0) {
      return null;
    } else {
      return Boolean.valueOf(readBoolean());
    }
  }
  
  /**
   * Reads a <code>Byte</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Byte readByteObject() throws IOException {

    if (read() == 0) {
      return null;
    } else {
      return Byte.valueOf(readByte());
    }
  }
  
  /**
   * Reads a <code>Short</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Short readShortObject() throws IOException {

    if (read() == 0) {
      return null;
    } else {
      return Short.valueOf(readShort());
    }
  }
  
  /**
   * Reads a <code>Character</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Character readCharObject() throws IOException {

    if (read() == 0) {
      return null;
    } else {
      return Character.valueOf(readChar());
    }
  }
  
  /**
   * Reads an <code>Integer</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Integer readIntObject() throws IOException {

    if (read() == 0) {
      return null;
    } else {
      return Integer.valueOf(readInt());
    }
  }
  
  /**
   * Reads a <code>Long<code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Long readLongObject() throws IOException {

    if (read() == 0) {
      return null;
    } else {
      return Long.valueOf(readLong());
    }
  }
  
  /**
   * Reads a <code>Float</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Float readFloatObject() throws IOException {

    if (read() == 0) {
      return null;
    } else {
      return Float.valueOf(readFloat());
    }
  }
  
  /**
   * Reads a <code>Double</code> object.
   * @return object or <code>null</code>
   * @throws IOException
   */
  public Double readDoubleObject() throws IOException {

    if (read() == 0) {
      return null;
    } else {
      return Double.valueOf(readDouble());
    }
  }
  
  /**
   * Reads a <code>String</code>.
   * @return a string or <code>null</code>
   * @throws IOException
   */
  public String readString() throws IOException {

    if (read() == 0) {
      return null;
    } else {
      return readUTF();
    }
  }
  
  /**
   * Reads a <code>Date</code>.
   * @return a date or <code>null</code>
   * @throws IOException
   */
  public Date readDate() throws IOException {

    if (read() == 0) {
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

    if (read() == 0) {
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

    if (read() == 0) {
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

    if (read() == 0) {
      return null;
    } else {
      return readNonNullByteArray();
    }
  }
  
  private byte[] readNonNullByteArray() throws IOException {
    
    byte[] b = new byte[readInt()];
    readFully(b);
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

    if (read() == 0) {
      return null;
    } else {
      return clazz.getEnumConstants()[readInt()];
    }
  }
}
