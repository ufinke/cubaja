// Copyright (c) 2010 - 2013, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import de.ufinke.cubaja.util.Text;

/**
 * Reads mainframe data.
 * <p>
 * Method <code>fillBuffer</code> reads an amount of bytes into an internal buffer.
 * The various <code>read</code> methods retrieve data from the buffer.
 * The position advances automatically.
 * @author Uwe Finke
 */
public class MainframeInput {

  static private Text text = Text.getPackageInstance(MainframeInput.class);
  
  private final InputStream stream;
  private final String charset; // string because of JDK 5.0 compatibility
  private final boolean doubleByte;
  private RandomAccessBuffer buffer;
  private boolean eof;
  private int recordCount;
  
  /**
   * Constructor.
   * <p>
   * The <code>charset</code> may be either a single byte or a double byte character set.
   * Do not use a character set with a variant number of bytes for a single character
   * (such as UTF-8)!  
   * @param stream input stream
   * @param charset character set
   * @throws UnsupportedEncodingException when the character set is not supported in the runtime environment 
   */
  public MainframeInput(InputStream stream, Charset charset) throws UnsupportedEncodingException {

    this(stream, charset.name());
  }
  
  /**
   * Constructor.
   * <p>
   * The <code>charset</code> may be either a single byte or a double byte character set.
   * Do not use a character set with a variant number of bytes for a single character
   * (such as UTF-8)!  
   * @param stream input stream
   * @param charset name of character set
   * @throws UnsupportedEncodingException when the character set does not exist or ist not supported in the runtime environment
   */
  public MainframeInput(InputStream stream, String charset) throws UnsupportedEncodingException {
    
    this.stream = stream;
    this.charset = charset;
    doubleByte = "A".getBytes(charset).length == 2;
    buffer = new RandomAccessBuffer();
  }
  
  /**
   * Closes the underlaying stream.
   * @throws IOException when the stream could not be closed
   */
  public void close() throws IOException {
    
    stream.close();
  }
  
  /**
   * Fills the internal buffer.
   * <p>
   * Returns <code>true</code> if the given number of bytes could be read,
   * or <code>false</code> when there where no more bytes to read.
   * Throws <code>EOFException</code> when the stream ends before all requested bytes were read. 
   * <p>
   * For fixed length records, <code>byteCount</code> should be the record length.
   * For variable records, the record length must be retrieved by 
   * a separate call before the rest of the record can be read.
   * <p>
   * The internal buffer position is set to <code>0</code>.
   * @param byteCount number of bytes to pass into the buffer
   * @return EOF flag
   * @throws IOException when the number of requested bytes could not be read from the input stream
   */
  public boolean fillBuffer(int byteCount) throws IOException {
    
    buffer.reset();
    
    int transferred = buffer.transferFrom(stream, byteCount);
    if (transferred < byteCount) {
      if (transferred == 0 && (! eof)) {
        eof = true;
        return false;
      }
      throw new EOFException(text.get("prematureEOF", byteCount, transferred));
    }
    
    buffer.setPosition(0);
    
    return true;
  }
  
  /**
   * Fills the internal buffer and increments record count.
   * Calls {@link #fillBuffer(int) fillBuffer}.
   * @param byteCount number of bytes to pass into the buffer
   * @return EOF flag
   * @throws IOException when the number of requested bytes could not be read from the input stream
   */
  public boolean nextRecord(int byteCount) throws IOException {
    
    boolean result = fillBuffer(byteCount);
    if (result) {
      recordCount++;
    }
    return result;
  }
  
  /**
   * Returns the record count.
   * The record count is incremented by {@link #nextRecord(int) nextRecord}.
   * @return record count
   */
  public int getRecordCount() {
    
    return recordCount;
  }
  
  /**
   * Sets the internal buffer's position.
   * @param offset position within the buffer, fist byte position is 0
   */
  public void setPosition(int offset) {
    
    buffer.setPosition(offset);
  }
  
  /**
   * Retrieves the internal buffer's position.
   * @return current position
   */
  public int getPosition() {
    
    return buffer.getPosition();
  }
  
  /**
   * Retrieves the record size.
   * This is the maximum buffer position.
   * @return record size
   */
  public int getSize() {
    
    return buffer.size();
  }
  
  /**
   * Reads a raw byte.
   * @return value
   * @throws IOException when the byte could not be read from the buffer
   */
  public int readUnsignedByte() throws IOException {
    
    return buffer.read();
  }
  
  /**
   * Reads a binary signed <code>byte</code> value.
   * @return value
   * @throws IOException when the byte could not be read from the buffer
   */
  public byte readByte() throws IOException {
    
    return buffer.readByte();
  }
  
  /**
   * Reads a binary <code>short</code> value.
   * @return value
   * @throws IOException when the short could not be read from the buffer
   */
  public short readShort() throws IOException {
    
    return buffer.readShort();
  }
  
  /**
   * Reads a binary <code>int</code> value.
   * @return value
   * @throws IOException when the int could not be read from the buffer
   */
  public int readInt() throws IOException {
    
    return buffer.readInt();
  }
  
  /**
   * Reads a binary <code>long</code> value.
   * @return value
   * @throws IOException when the long could not be read from the buffer
   */
  public long readLong() throws IOException {
    
    return buffer.readLong();
  }
  
  /**
   * Reads a string.
   * @param charCount number of characters
   * @return value
   * @throws IOException when the requested number of characters could not be read from the buffer
   */
  public String readString(int charCount) throws IOException {

    int byteCount = doubleByte ? charCount * 2 : charCount;
    byte[] b = new byte[byteCount];
    buffer.readFully(b);
    return new String(b, charset);
  }
  
  /**
   * Reads a zoned <code>int</code> value.
   * @param digitCount number of digits
   * @return value
   * @throws IOException when the requested number of digits could not be read from the buffer
   */
  public int readZonedInt(int digitCount) throws IOException {
    
    return Integer.parseInt(readZoned(digitCount, 0, 9));
  }
  
  /**
   * Reads a zoned <code>long</code> value.
   * @param digitCount number of digits
   * @return value
   * @throws IOException when the requested number of digits could not be read from the buffer
   */
  public long readZonedLong(int digitCount) throws IOException {
    
    return Long.parseLong(readZoned(digitCount, 0, 18));
  }
  
  /**
   * Reads a zoned <code>double</code> value.
   * @param integerDigitCount number of digits before imaginary decimal point
   * @param fractionalDigitCount number of digits after imaginary decimal point
   * @return value
   * @throws IOException when the requested number of digits (sum of integer an fractional part) could not be read from the buffer
   */
  public double readZonedDouble(int integerDigitCount, int fractionalDigitCount) throws IOException {
    
    return Double.parseDouble(readZoned(integerDigitCount, fractionalDigitCount, 31));
  }

  /**
   * Reads a zoned <code>BigDecimal</code> value.
   * @param integerDigitCount number of digits before imaginary decimal point
   * @param fractionalDigitCount number of digits after imaginary decimal point
   * @return value
   * @throws IOException when the requested number of digits (sum of integer an fractional part) could not be read from the buffer
   */
  public BigDecimal readZonedBigDecimal(int integerDigitCount, int fractionalDigitCount) throws IOException {
    
    return new BigDecimal(readZoned(integerDigitCount, fractionalDigitCount, 31));
  }
  
  private String readZoned(int intDigits, int fracDigits, int maxDigits) throws IOException {
    
    int digits = intDigits + fracDigits;
    if (intDigits < 0 || fracDigits < 0 || digits < 1 || digits > maxDigits) {
      throw new IOException(text.get("digitCount", intDigits, fracDigits, maxDigits));
    }
    
    int offset = buffer.getPosition();
    
    int cLen = intDigits + 1;
    int pointPos = 0;
    if (fracDigits > 0) {
      pointPos = cLen;
      cLen = cLen + fracDigits + 1;
    }
    
    char[] c = new char[cLen];
    c[0] = '0';
    
    int out = 1;
    int in = 1;
    
    while (in <= digits) {
      
      int b = buffer.read();
      
      switch (b & 0xF0) {
        case 0xF0:
          break;
        case 0xD0:
          if (in == digits) {
            c[0] = '-';
          } else {
            invalidNumeric(offset, digits, "zoned");
          }
          break;
        case 0xC0:
          if (in != digits) {
            invalidNumeric(offset, digits, "zoned");
          }
          break;
        default:
          invalidNumeric(offset, digits, "zoned");
      }
      
      switch (b & 0x0F) {
        case 0:
          c[out] = '0';
          break;
        case 1:
          c[out] = '1';
          break;
        case 2:
          c[out] = '2';
          break;
        case 3:
          c[out] = '3';
          break;
        case 4:
          c[out] = '4';
          break;
        case 5:
          c[out] = '5';
          break;
        case 6:
          c[out] = '6';
          break;
        case 7:
          c[out] = '7';
          break;
        case 8:
          c[out] = '8';
          break;
        case 9:
          c[out] = '9';
          break;
        default:
          invalidNumeric(offset, digits, "zoned");
      }
      
      out++;
      
      if (out == pointPos) {
        c[out] = '.';
        out++;
      }
      
      in++;
    }
    
    return new String(c);
  }
  
  /**
   * Reads a packed <code>int</code> value.
   * @param digitCount number of digits
   * @return value
   * @throws IOException when the requested number of digits could not be read from the buffer
   */
  public int readPackedInt(int digitCount) throws IOException {
    
    return Integer.parseInt(readPacked(digitCount, 0, 9));
  }
  
  /**
   * Reads a packed <code>long</code> value.
   * @param digitCount number of digits
   * @return value
   * @throws IOException when the requested number of digits could not be read from the buffer
   */
  public long readPackedLong(int digitCount) throws IOException {
    
    return Long.parseLong(readPacked(digitCount, 0, 18));
  }
  
  /**
   * Reads a packed <code>double</code> value.
   * @param integerDigitCount number of digits before imaginary decimal point
   * @param fractionalDigitCount number of digits after imaginary decimal point
   * @return value
   * @throws IOException when the requested number of digits (integer plus fractional part) could not be read from the buffer
   */
  public double readPackedDouble(int integerDigitCount, int fractionalDigitCount) throws IOException {
    
    return Double.parseDouble(readPacked(integerDigitCount, fractionalDigitCount, 31));
  }
  
  /**
   * Reads a packed <code>BigDecimal</code> value.
   * @param integerDigitCount number of digits before imaginary decimal point
   * @param fractionalDigitCount number of digits after imaginary decimal point
   * @return value
   * @throws IOException when the requested number of digits (integer plus fractional part) could not be read from the buffer
   */
  public BigDecimal readPackedBigDecimal(int integerDigitCount, int fractionalDigitCount) throws IOException {
    
    return new BigDecimal(readPacked(integerDigitCount, fractionalDigitCount, 31));
  }
  
  private String readPacked(int intDigits, int fracDigits, int maxDigits) throws IOException {
    
    int digits = intDigits + fracDigits;
    if (intDigits < 0 || fracDigits < 0 || digits < 1 || digits > maxDigits) {
      throw new IOException(text.get("digitCount", intDigits, fracDigits, maxDigits));
    }
    
    if ((digits & 0x01) == 0) {
      digits++;
      intDigits++;
    }
    
    int offset = buffer.getPosition();
    
    int cLen = intDigits + 1;
    int pointPos = 0;
    if (fracDigits > 0) {
      pointPos = cLen;
      cLen = cLen + fracDigits + 1;
    }
    int byteCount = (digits >> 1) + 1;
    
    char[] c = new char[cLen];
    c[0] = '0';
    
    int out = 1;
    int in = 1;
    
    while (in <= byteCount) {
      
      int b = buffer.read();
      
      switch ((b & 0xF0) >>> 4) {
        case 0:
          c[out] = '0';
          break;
        case 1:
          c[out] = '1';
          break;
        case 2:
          c[out] = '2';
          break;
        case 3:
          c[out] = '3';
          break;
        case 4:
          c[out] = '4';
          break;
        case 5:
          c[out] = '5';
          break;
        case 6:
          c[out] = '6';
          break;
        case 7:
          c[out] = '7';
          break;
        case 8:
          c[out] = '8';
          break;
        case 9:
          c[out] = '9';
          break;
        default:
          invalidNumeric(offset, byteCount, "packed");
      }
      out++;      
      if (out == pointPos) {
        c[out] = '.';
        out++;
      }
            
      if (in == byteCount) {
        switch (b & 0x0F) {
          case 0x0F:
          case 0x0C:
            break;
          case 0x0D:
            c[0] = '-';
            break;
          default:
            invalidNumeric(offset, byteCount, "packed");
        }
      } else {
        switch (b & 0x0F) {
          case 0:
            c[out] = '0';
            break;
          case 1:
            c[out] = '1';
            break;
          case 2:
            c[out] = '2';
            break;
          case 3:
            c[out] = '3';
            break;
          case 4:
            c[out] = '4';
            break;
          case 5:
            c[out] = '5';
            break;
          case 6:
            c[out] = '6';
            break;
          case 7:
            c[out] = '7';
            break;
          case 8:
            c[out] = '8';
            break;
          case 9:
            c[out] = '9';
            break;
          default:
            invalidNumeric(offset, byteCount, "packed");
        }
        out++;      
        if (out == pointPos) {
          c[out] = '.';
          out++;
        }
      }
      
      in++;
    }
    
    return new String(c);
  }
  
  /**
   * Reads an unsigned packed <code>int</code> value.
   * @param digitCount number of digits
   * @return value
   * @throws IOException when the requested number of digits could not be read from the buffer
   */
  public int readUnsignedPackedInt(int digitCount) throws IOException {
    
    return Integer.parseInt(readUnsignedPacked(digitCount, 0, 9));
  }
  
  /**
   * Reads an unsigned packed <code>long</code> value.
   * @param digitCount number of digits
   * @return value
   * @throws IOException when the requested number of digits could not be read from the buffer
   */
  public long readUnsignedPackedLong(int digitCount) throws IOException {
    
    return Long.parseLong(readUnsignedPacked(digitCount, 0, 18));
  }
  
  /**
   * Reads an unsigned packed <code>double</code> value.
   * @param integerDigitCount number of digits before imaginary decimal point
   * @param fractionalDigitCount number of digits after imaginary decimal point
   * @return value
   * @throws IOException when the requested number of digits (integer plus fractional part) could not be read from the buffer
   */
  public double readUnsignedPackedDouble(int integerDigitCount, int fractionalDigitCount) throws IOException {
    
    return Double.parseDouble(readUnsignedPacked(integerDigitCount, fractionalDigitCount, 32));
  }
  
  /**
   * Reads an unsigned packed <code>BigDecimal</code> value.
   * @param integerDigitCount number of digits before imaginary decimal point
   * @param fractionalDigitCount number of digits after imaginary decimal point
   * @return value
   * @throws IOException when the requested number of digits (integer plus fractional part) could not be read from the buffer
   */
  public BigDecimal readUnsignedPackedBigDecimal(int integerDigitCount, int fractionalDigitCount) throws IOException {
    
    return new BigDecimal(readUnsignedPacked(integerDigitCount, fractionalDigitCount, 32));
  }
  
  private String readUnsignedPacked(int intDigits, int fracDigits, int maxDigits) throws IOException {
    
    int digits = intDigits + fracDigits;
    if (intDigits < 0 || fracDigits < 0 || digits < 1 || digits > maxDigits) {
      throw new IOException(text.get("digitCount", intDigits, fracDigits, maxDigits));
    }
    
    if ((digits & 0x01) == 1) {
      digits++;
      intDigits++;
    }
    
    int offset = buffer.getPosition();
    
    int cLen = intDigits + 1;
    int pointPos = 0;
    if (fracDigits > 0) {
      pointPos = cLen;
      cLen = cLen + fracDigits + 1;
    }
    int byteCount = (digits >> 1);
    
    char[] c = new char[cLen];
    c[0] = '0';
    
    int out = 1;
    int in = 1;
    
    while (in <= byteCount) {
      
      int b = buffer.read();
      
      switch ((b & 0xF0) >>> 4) {
        case 0:
          c[out] = '0';
          break;
        case 1:
          c[out] = '1';
          break;
        case 2:
          c[out] = '2';
          break;
        case 3:
          c[out] = '3';
          break;
        case 4:
          c[out] = '4';
          break;
        case 5:
          c[out] = '5';
          break;
        case 6:
          c[out] = '6';
          break;
        case 7:
          c[out] = '7';
          break;
        case 8:
          c[out] = '8';
          break;
        case 9:
          c[out] = '9';
          break;
        default:
          invalidNumeric(offset, byteCount, "unsigned packed");
      }
      out++;      
      if (out == pointPos) {
        c[out] = '.';
        out++;
      }
            
      switch (b & 0x0F) {
        case 0:
          c[out] = '0';
          break;
        case 1:
          c[out] = '1';
          break;
        case 2:
          c[out] = '2';
          break;
        case 3:
          c[out] = '3';
          break;
        case 4:
          c[out] = '4';
          break;
        case 5:
          c[out] = '5';
          break;
        case 6:
          c[out] = '6';
          break;
        case 7:
          c[out] = '7';
          break;
        case 8:
          c[out] = '8';
          break;
        case 9:
          c[out] = '9';
          break;
        default:
          invalidNumeric(offset, byteCount, "unsigned packed");
      }
      out++;      
      if (out == pointPos) {
        c[out] = '.';
        out++;
      }
      
      in++;
    }
    
    return new String(c);
  }
  
  private void invalidNumeric(int offset, int count, String format) throws IOException {
    
    buffer.setPosition(offset);
    
    StringBuilder sb = new StringBuilder(count * 2);
    for (int i = 0; i < count; i++) {
      String s = Integer.toHexString(buffer.read());
      if (s.length() == 1) {
        sb.append('0');
      }
      sb.append(s);
    }
    
    throw new IOException(text.get("invalidNumeric", sb, offset, format));
  }

  /**
   * Advances the buffers position.
   * @param n number of bytes to skip
   * @return number of skipped bytes
   * @throws IOException when the requested number of bytes could not be skipped
   */
  public int skipBytes(int n) throws IOException {

    return buffer.skipBytes(n);
  }

  /**
   * Reads a raw byte array.
   * @param count number of bytes to read
   * @return byte array
   * @throws IOException when the requested number of bytes could not be read from the buffer
   */
  public byte[] readBytes(int count) throws IOException {
    
    byte[] array = new byte[count];
    buffer.readFully(array);
    return array;
  }
  
  /**
   * Fills a byte array.
   * @param b array
   * @throws IOException when the array could not be filled from the buffer
   */
  public void readFully(byte[] b) throws IOException {
    
    buffer.readFully(b);
  }
  
  /**
   * Fills a portion of a byte array.
   * @param b array
   * @param off offset in the array
   * @param len number of bytes
   * @throws IOException when the requested number of bytes could not be read from the buffer
   */
  public void readFully(byte[] b, int off, int len) throws IOException {

    buffer.readFully(b, off, len);
  }
  
  /**
   * Gives access to the internal buffer.
   * Use the buffer only if you know what you're doing.
   * @return the buffer
   */
  public RandomAccessBuffer getBuffer() {
    
    return buffer;
  }
  
  /**
   * Replaces the internal buffer.
   * May be useful to share the buffer with other instances.
   * @param buffer the new buffer instance
   */
  public void setBuffer(RandomAccessBuffer buffer) {
    
    this.buffer = buffer;
  }
}
