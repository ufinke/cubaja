// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import de.ufinke.cubaja.util.*;

public class MainframeOutput {

  static private Text text = Text.getPackageInstance(MainframeOutput.class);
  
  private final OutputStream stream;
  private final Charset charset;
  private final RandomAccessBuffer buffer;
  
  public MainframeOutput(OutputStream stream, Charset charset) {
  
    this.stream = stream;
    this.charset = charset;
    buffer = new RandomAccessBuffer();
  }
  
  public void close() throws IOException {
    
    stream.close();
  }
  
  public void drainBuffer() throws IOException {

    buffer.drainTo(stream);
  }
  
  public void setPosition(int offset) {
    
    buffer.setPosition(offset);
  }
  
  public int getPosition() {
    
    return buffer.getPosition();
  }
  
  public void writeUnsignedByte(int value) throws IOException {
    
    buffer.write(value);
  }
  
  public void writeByte(int value) throws IOException {
    
    buffer.writeByte(value);
  }
  
  public void writeShort(int value) throws IOException {
    
    buffer.writeShort(value);
  }
  
  public void writeInt(int value) throws IOException {
    
    buffer.writeInt(value);
  }
  
  public void writeLong(long value) throws IOException {
    
    buffer.writeLong(value);
  }
  
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
  
  public void writeZoned(int value, int digits) throws IOException {
    
    writeZoned(String.valueOf(value), digits);
  }
  
  public void writeZoned(long value, int digits) throws IOException {
    
    writeZoned(String.valueOf(value), digits);
  }
  
  public void writeZoned(double value, int integerDigits, int fractionalDigits) throws IOException {
    
    value *= Math.pow(10, fractionalDigits);
    writeZoned(Util.format(value, 0, '.', true), integerDigits + fractionalDigits);
  }
  
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
