// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import de.ufinke.cubaja.util.Text;
import de.ufinke.cubaja.util.Util;

/**
 * Writes mainframe data.
 * <p>
 * The various <tt>write</tt> methods write data into an internal buffer.
 * Method <tt>drainBuffer</tt> writes the buffer's content 
 * to the target output stream.
 * <p>
 * To produce variable length records,
 * set the position manually to the start of the record's data area
 * before writing any data into the buffer.
 * Set the length value prior to the call to <tt>drainBuffer</tt>; 
 * the length may be retrieved with the <tt>getSize</tt> method.
 * @author Uwe Finke
 */
public class MainframeOutput {

  static private Text text = Text.getPackageInstance(MainframeOutput.class);
  
  private final OutputStream stream;
  private final Charset charset;
  private final RandomAccessBuffer buffer;
  
  /**
   * Constructor.
   * <p>
   * The <tt>charset</tt> may be either a single byte or a double byte character set.
   * Do not use a character set with a variant number of bytes for a single character
   * (such as UTF-8)!
   */  
  public MainframeOutput(OutputStream stream, Charset charset) {
  
    this.stream = stream;
    this.charset = charset;
    buffer = new RandomAccessBuffer();
  }
  
  /**
   * Closes the underlaying stream.
   * @throws IOException
   */
  public void close() throws IOException {
    
    stream.close();
  }
  
  /**
   * Drains the internal buffer's content to the underlaying stream.
   * @throws IOException
   */
  public void drainBuffer() throws IOException {

    buffer.drainTo(stream);
  }
  
  /**
   * Sets the internal buffer's position.
   * @param offset
   */
  public void setPosition(int offset) {
    
    buffer.setPosition(offset);
  }
  
  /**
   * Retrieves the internal buffer's position.
   * @return actual position
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
   * Writes a raw byte.
   * @param value
   * @throws IOException
   */
  public void writeUnsignedByte(int value) throws IOException {
    
    buffer.write(value);
  }
  
  /**
   * Writes a binary <tt>byte</tt> value.
   * @param value
   * @throws IOException
   */
  public void writeByte(int value) throws IOException {
    
    buffer.writeByte(value);
  }
  
  /**
   * Writes a binary <tt>short</tt> value.
   * @param value
   * @throws IOException
   */
  public void writeShort(int value) throws IOException {
    
    buffer.writeShort(value);
  }
  
  /**
   * Writes a binary <tt>int</tt> value.
   * @param value
   * @throws IOException
   */
  public void writeInt(int value) throws IOException {
    
    buffer.writeInt(value);
  }
  
  /**
   * Writes a binary <tt>long</tt> value.
   * @param value
   * @throws IOException
   */
  public void writeLong(long value) throws IOException {
    
    buffer.writeLong(value);
  }
  
  /**
   * Writes a string.
   * <p>
   * If the string's length is less than <tt>charCount</tt>,
   * it is padded to the right with spaces.
   * If the string's length is greater than <tt>charCount</tt>,
   * it is truncated.
   * <p>
   * A <tt>null</tt> value is handled as an empty string.
   * @param value
   * @param charCount
   * @throws IOException
   */
  public void writeString(String value, int charCount) throws IOException {
    
    if (value == null) {
      value = "";
    }
    
    if (value.length() > charCount) {
      value = value.substring(0, charCount);
    } else if (value.length() < charCount) {
      StringBuilder sb = new StringBuilder(charCount);
      sb.append(value);
      while (sb.length() < charCount) {
        sb.append(' ');
      }
      value = sb.toString();
    }
    
    buffer.write(value.getBytes(charset));
  }
  
  /**
   * Writes an <tt>int</tt> in zoned format.
   * @param value
   * @param digits number of digits
   * @throws IOException
   */
  public void writeZoned(int value, int digits) throws IOException {
    
    writeZoned(String.valueOf(value), digits);
  }
  
  /**
   * Writes a <tt>long</tt> in zoned format.
   * @param value
   * @param digits number of digits
   * @throws IOException
   */
  public void writeZoned(long value, int digits) throws IOException {
    
    writeZoned(String.valueOf(value), digits);
  }
  
  /**
   * Writes a <tt>double</tt> in zoned format
   * @param value
   * @param integerDigits number of digits before imaginary decimal point
   * @param fractionalDigits number of digits after imaginary decimal point
   * @throws IOException
   */
  public void writeZoned(double value, int integerDigits, int fractionalDigits) throws IOException {
    
    value *= Math.pow(10, fractionalDigits);
    writeZoned(Util.format(value, 0, '.', true), integerDigits + fractionalDigits);
  }
  
  /**
   * Writes a <tt>BigDecimal</tt> in zoned format
   * @param value
   * @param integerDigits number of digits before imaginary decimal point
   * @param fractionalDigits number of digits after imaginary decimal point
   * @throws IOException
   */
  public void writeZoned(BigDecimal value, int integerDigits, int fractionalDigits) throws IOException {
    
    if (value.scale() != 0) {
      value = value.scaleByPowerOfTen(fractionalDigits);
    }
    writeZoned(Util.format(value, 0, '.', true), integerDigits + fractionalDigits);
  }
  
  private void writeZoned(String value, int digits) throws IOException {

    char[] in = normalizeNumber(value, digits);
    byte[] out = new byte[digits];
    
    int inPos = 1;
    int outPos = 0;
    
    while (outPos < digits) {
      
      switch (in[inPos]) {
        case '0':
          out[outPos] = (byte) 0xF0;
          break;
        case '1':
          out[outPos] = (byte) 0xF1;
          break;
        case '2':
          out[outPos] = (byte) 0xF2;
          break;
        case '3':
          out[outPos] = (byte) 0xF3;
          break;
        case '4':
          out[outPos] = (byte) 0xF4;
          break;
        case '5':
          out[outPos] = (byte) 0xF5;
          break;
        case '6':
          out[outPos] = (byte) 0xF6;
          break;
        case '7':
          out[outPos] = (byte) 0xF7;
          break;
        case '8':
          out[outPos] = (byte) 0xF8;
          break;
        case '9':
          out[outPos] = (byte) 0xF9;
          break;
      }
      
      inPos++;
      outPos++;
    }
    
    if (in[0] == '-') {
      outPos--;
      out[outPos] = (byte) (out[outPos] & 0xDF); 
    }
    
    buffer.write(out);
  }
  
  /**
   * Writes an <tt>int</tt> in packed format.
   * @param value
   * @param digits number of digits
   * @throws IOException
   */
  public void writePacked(int value, int digits) throws IOException {
    
    writePacked(String.valueOf(value), digits);
  }
  
  /**
   * Writes a <tt>long</tt> in packed format.
   * @param value
   * @param digits number of digits
   * @throws IOException
   */
  public void writePacked(long value, int digits) throws IOException {
    
    writePacked(String.valueOf(value), digits);
  }
  
  /**
   * Writes a <tt>double</tt> in packed format
   * @param value
   * @param integerDigits number of digits before imaginary decimal point
   * @param fractionalDigits number of digits after imaginary decimal point
   * @throws IOException
   */
  public void writePacked(double value, int integerDigits, int fractionalDigits) throws IOException {
    
    value *= Math.pow(10, fractionalDigits);
    writePacked(Util.format(value, 0, '.', true), integerDigits + fractionalDigits);
  }
  
  /**
   * Writes a <tt>BigDecimal</tt> in packed format
   * @param value
   * @param integerDigits number of digits before imaginary decimal point
   * @param fractionalDigits number of digits after imaginary decimal point
   * @throws IOException
   */
  public void writePacked(BigDecimal value, int integerDigits, int fractionalDigits) throws IOException {
    
    if (value.scale() != 0) {
      value = value.scaleByPowerOfTen(fractionalDigits);
    }
    writePacked(Util.format(value, 0, '.', true), integerDigits + fractionalDigits);
  }
  
  private void writePacked(String value, int digits) throws IOException {

    if ((digits & 0x01) == 0) {
      digits++;
    }
    int lastByte = digits >>> 1;
    int byteCount = lastByte + 1;
    
    char[] in = normalizeNumber(value, digits);
    byte[] out = new byte[byteCount];
    
    int inPos = 1;
    int outPos = 0;
    
    while (inPos <= digits) {
      
      int b = 0;
      
      switch (in[inPos++]) {
        case '0':
          break;
        case '1':
          b = 0x10;
          break;
        case '2':
          b = 0x20;
          break;
        case '3':
          b = 0x30;
          break;
        case '4':
          b = 0x40;
          break;
        case '5':
          b = 0x50;
          break;
        case '6':
          b = 0x60;
          break;
        case '7':
          b = 0x70;
          break;
        case '8':
          b = 0x80;
          break;
        case '9':
          b = 0x90;
          break;
      }
      
      if (outPos == lastByte) {
        
        b |= (in[0] == '-') ? 0x0D : 0x0C;
        
      } else {
        
        switch (in[inPos++]) {
          case '0':
            break;
          case '1':
            b |= 0x01;
            break;
          case '2':
            b |= 0x02;
            break;
          case '3':
            b |= 0x03;
            break;
          case '4':
            b |= 0x04;
            break;
          case '5':
            b |= 0x05;
            break;
          case '6':
            b |= 0x06;
            break;
          case '7':
            b |= 0x07;
            break;
          case '8':
            b |= 0x08;
            break;
          case '9':
            b |= 0x09;
            break;
        } 
      }
      
      out[outPos] = (byte) b;
      outPos++;
    }
    
    buffer.write(out);
  }
  
  /**
   * Writes an <tt>int</tt> in unsigned packed format.
   * @param value
   * @param digits number of digits
   * @throws IOException
   */
  public void writeUnsignedPacked(int value, int digits) throws IOException {
    
    writeUnsignedPacked(String.valueOf(value), digits);
  }
  
  /**
   * Writes a <tt>long</tt> in unsigned packed format.
   * @param value
   * @param digits number of digits
   * @throws IOException
   */
  public void writeUnsignedPacked(long value, int digits) throws IOException {
    
    writeUnsignedPacked(String.valueOf(value), digits);
  }
  
  /**
   * Writes a <tt>double</tt> in unsigned packed format
   * @param value
   * @param integerDigits number of digits before imaginary decimal point
   * @param fractionalDigits number of digits after imaginary decimal point
   * @throws IOException
   */
  public void writeUnsignedPacked(double value, int integerDigits, int fractionalDigits) throws IOException {
    
    value *= Math.pow(10, fractionalDigits);
    writeUnsignedPacked(Util.format(value, 0, '.', true), integerDigits + fractionalDigits);
  }
  
  /**
   * Writes a <tt>BigDecimal</tt> in unsigned packed format
   * @param value
   * @param integerDigits number of digits before imaginary decimal point
   * @param fractionalDigits number of digits after imaginary decimal point
   * @throws IOException
   */
  public void writeUnsignedPacked(BigDecimal value, int integerDigits, int fractionalDigits) throws IOException {
    
    if (value.scale() != 0) {
      value = value.scaleByPowerOfTen(fractionalDigits);
    }
    writeUnsignedPacked(Util.format(value, 0, '.', true), integerDigits + fractionalDigits);
  }
  
  private void writeUnsignedPacked(String value, int digits) throws IOException {

    if ((digits & 0x01) == 1) {
      digits++;
    }
    int byteCount = digits >>> 1;
    
    char[] in = normalizeNumber(value, digits);
    byte[] out = new byte[byteCount];
    
    int inPos = 1;
    int outPos = 0;
    
    while (inPos <= digits) {
      
      int b = 0;
      
      switch (in[inPos++]) {
        case '0':
          break;
        case '1':
          b = 0x10;
          break;
        case '2':
          b = 0x20;
          break;
        case '3':
          b = 0x30;
          break;
        case '4':
          b = 0x40;
          break;
        case '5':
          b = 0x50;
          break;
        case '6':
          b = 0x60;
          break;
        case '7':
          b = 0x70;
          break;
        case '8':
          b = 0x80;
          break;
        case '9':
          b = 0x90;
          break;
      }
      
      switch (in[inPos++]) {
        case '0':
          break;
        case '1':
          b |= 0x01;
          break;
        case '2':
          b |= 0x02;
          break;
        case '3':
          b |= 0x03;
          break;
        case '4':
          b |= 0x04;
          break;
        case '5':
          b |= 0x05;
          break;
        case '6':
          b |= 0x06;
          break;
        case '7':
          b |= 0x07;
          break;
        case '8':
          b |= 0x08;
          break;
        case '9':
          b |= 0x09;
          break;
      } 
      
      out[outPos] = (byte) b;
      outPos++;
    }
    
    buffer.write(out);
  }
  
  private char[] normalizeNumber(String value, int outputDigits) throws IOException {
    
    char[] input = value.toCharArray();
    char[] output = new char[outputDigits + 1];
    
    output[0] = '+';
    int inPos = 0;
    
    if (input[0] == '-') {
      output[0] = '-';
      inPos = 1;
    }
    
    int inputDigits = input.length - inPos; 
    if (inputDigits > outputDigits) {
      throw new IOException(text.get("exceedDigits", value, outputDigits));
    }

    int outPos = 1;
    
    int leadingZeroes = outputDigits - inputDigits;
    while (outPos <= leadingZeroes) {
      output[outPos++] = '0';
    }
    
    while (inPos < input.length) {
      output[outPos++] = input[inPos++];
    }
    
    return output;
  }
}
