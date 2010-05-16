// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import de.ufinke.cubaja.util.Text;

public class MainframeInput {

  static private Text text = Text.getPackageInstance(MainframeInput.class);
  
  private final InputStream stream;
  private final Charset charset;
  private final boolean doubleByte;
  private final RandomAccessBuffer buffer;
  
  public MainframeInput(InputStream stream, Charset charset) {
  
    this.stream = stream;
    this.charset = charset;
    doubleByte = "A".getBytes(charset).length == 2;
    buffer = new RandomAccessBuffer();
  }
  
  public void close() throws IOException {
    
    stream.close();
  }
  
  public boolean fillBuffer(int byteCount) throws IOException {
    
    buffer.reset();
    
    int transferred = buffer.transferFrom(stream, byteCount);
    if (transferred < byteCount) {
      if (transferred == 0) {
        return false;
      }
      throw new IOException(text.get("prematureEOF", byteCount, transferred));
    }
    
    buffer.setPosition(0);
    
    return true;
  }
  
  public void setPosition(int offset) {
    
    buffer.setPosition(offset);
  }
  
  public int getPosition() {
    
    return buffer.getPosition();
  }
  
  public int readUnsignedByte() throws IOException {
    
    return buffer.read();
  }
  
  public byte readByte() throws IOException {
    
    return buffer.readByte();
  }
  
  public short readShort() throws IOException {
    
    return buffer.readShort();
  }
  
  public int readInt() throws IOException {
    
    return buffer.readInt();
  }
  
  public long readLong() throws IOException {
    
    return buffer.readLong();
  }
  
  public String readString(int charCount) throws IOException {

    int byteCount = doubleByte ? charCount * 2 : charCount;
    byte[] b = new byte[byteCount];
    buffer.readFully(b);
    return new String(b, charset);
  }
  
  public int readZonedInt(int digitCount) throws IOException {
    
    return Integer.parseInt(readZoned(digitCount, 0, 9));
  }
  
  public long readZonedLong(int digitCount) throws IOException {
    
    return Long.parseLong(readZoned(digitCount, 0, 18));
  }
  
  public double readZonedDouble(int integerDigitCount, int fractionalDigitCount) throws IOException {
    
    return Double.parseDouble(readZoned(integerDigitCount, fractionalDigitCount, 31));
  }
  
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
  
  public int readPackedInt(int digitCount) throws IOException {
    
    return Integer.parseInt(readPacked(digitCount, 0, 9));
  }
  
  public long readPackedLong(int digitCount) throws IOException {
    
    return Long.parseLong(readPacked(digitCount, 0, 18));
  }
  
  public double readPackedDouble(int integerDigitCount, int fractionalDigitCount) throws IOException {
    
    return Double.parseDouble(readPacked(integerDigitCount, fractionalDigitCount, 31));
  }
  
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
  
  public int readUnsignedPackedInt(int digitCount) throws IOException {
    
    return Integer.parseInt(readUnsignedPacked(digitCount, 0, 9));
  }
  
  public long readUnsignedPackedLong(int digitCount) throws IOException {
    
    return Long.parseLong(readUnsignedPacked(digitCount, 0, 18));
  }
  
  public double readUnsignedPackedDouble(int integerDigitCount, int fractionalDigitCount) throws IOException {
    
    return Double.parseDouble(readUnsignedPacked(integerDigitCount, fractionalDigitCount, 31));
  }
  
  public BigDecimal readUnsignedPackedBigDecimal(int integerDigitCount, int fractionalDigitCount) throws IOException {
    
    return new BigDecimal(readUnsignedPacked(integerDigitCount, fractionalDigitCount, 31));
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
  
}
