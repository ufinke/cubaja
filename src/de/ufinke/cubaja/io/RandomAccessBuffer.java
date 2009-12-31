// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class RandomAccessBuffer implements DataInput, DataOutput {

  static private class Output extends OutputStream {
  
    private RandomAccessBuffer master;
    
    public Output(RandomAccessBuffer master) {
    
      this.master = master;
    }

    public void write(int b) throws IOException {

      master.write(b);
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
      
      master.write(b, off, len);
    }
  }
  
  static private class Input extends InputStream {
    
    private RandomAccessBuffer master;
    
    public Input(RandomAccessBuffer master) {
      
      this.master = master;
    }

    public int read() throws IOException {

      return master.read();
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
      
      return master.read(b, off, len);
    }
  }
  
  static private final int DEFAULT_CAPACITY = 4096;
  
  protected byte[] buffer;
  protected int position;
  
  private int capacity;
  private int growthCapacity;
  private int size;
  
  private Input inputStream;
  private Output outputStream;
  
  public RandomAccessBuffer() {
    
    capacity = DEFAULT_CAPACITY;
    growthCapacity = capacity;
    buffer = new byte[capacity]; 
  }
  
  public RandomAccessBuffer(int initialCapacity, int growthCapacity) {
    
    capacity = initialCapacity;
    this.growthCapacity = growthCapacity;
    buffer = new byte[capacity];
  }

  public void reset() {
    
    position = 0;
    size = 0;
  }
  
  public void setPosition(int position) {
    
    this.position = position;
    if (position > size) {
      if (position > capacity) {
        grow(position + 1);
      }
      size = position;
    }
  }
  
  public int getPosition() {
    
    return position;
  }
  
  public int size() {
    
    return size;
  }
  
  public byte[] toByteArray() {
    
    byte[] copy = new byte[size];
    System.arraycopy(buffer, 0, copy, 0, size);
    return copy;
  }
  
  protected int newWritePosition(int bytesToWrite) throws IOException {
    
    int newPosition = position + bytesToWrite;
    if (newPosition > capacity) {
      grow(newPosition);
    }
    if (newPosition > size) {
      size = newPosition;
    }
    return newPosition;
  }
  
  private void grow(int minCapacity) {
    
    int newCapacity = Math.min(minCapacity, capacity + growthCapacity);
    byte[] newBuffer = new byte[newCapacity];
    System.arraycopy(buffer, 0, newBuffer, 0, size);
    capacity = newCapacity;
    buffer = newBuffer;
  }
  
  public OutputStream getOutputStream() {
    
    if (outputStream == null) {
      outputStream = new Output(this);
    }
    return outputStream;
  }
  
  public void writeTo(OutputStream out) throws IOException {
    
    out.write(buffer, 0, size);
  }

  public void writeTo(DataOutput out) throws IOException {
    
    out.write(buffer, 0, size);
  }

  public void write(int b) throws IOException {

    int pos = newWritePosition(1);
    buffer[position] = (byte) b;
    position = pos;
  }

  public void write(byte[] b) throws IOException {

    write(b, 0, b.length);
  }

  public void write(byte[] b, int off, int len) throws IOException {

    int pos = newWritePosition(len);
    System.arraycopy(b, off, buffer, position, len);
    position = pos;
  }

  public void writeBoolean(boolean v) throws IOException {

    write(v ? 1 : 0);
  }

  public void writeByte(int v) throws IOException {

    write(v);
  }

  public void writeShort(int v) throws IOException {

    newWritePosition(2);
    int pos = position;
    buffer[pos++] = (byte) (v >> 8);
    buffer[pos++] = (byte) v;
    position = pos;
  }

  public void writeChar(int v) throws IOException {

    writeShort(v);
  }

  public void writeInt(int v) throws IOException {

    newWritePosition(4);
    int pos = position;
    buffer[pos++] = (byte) (v >> 24);
    buffer[pos++] = (byte) (v >> 16);
    buffer[pos++] = (byte) (v >> 8);
    buffer[pos++] = (byte) v;
    position = pos;
  }

  public void writeLong(long v) throws IOException {

    newWritePosition(8);
    int pos = position;
    int n = (int) (v >> 32);
    buffer[pos++] = (byte) (n >> 24);
    buffer[pos++] = (byte) (n >> 16);
    buffer[pos++] = (byte) (n >> 8);
    buffer[pos++] = (byte) n;
    n = (int) v;
    buffer[pos++] = (byte) (n >> 24);
    buffer[pos++] = (byte) (n >> 16);
    buffer[pos++] = (byte) (n >> 8);
    buffer[pos++] = (byte) n;
    position = pos;
  }

  public void writeFloat(float v) throws IOException {

    writeInt(Float.floatToIntBits(v));
  }

  public void writeDouble(double v) throws IOException {

    writeLong(Double.doubleToLongBits(v));
  }

  public void writeBytes(String s) throws IOException {

    int len = s.length();
    newWritePosition(len);
    int pos = position;
    for (int i = 0; i < len; i++) {
      buffer[pos++] = (byte) s.charAt(i);
    }
    position = pos;
  }

  public void writeChars(String s) throws IOException {

    int len = s.length();
    newWritePosition(len);
    int pos = position;
    for (int i = 0; i < len; i++) {
      int c = s.charAt(i);
      buffer[pos++] = (byte) (c >> 8);
      buffer[pos++] = (byte) c;
    }
    position = pos;
  }

  public void writeUTF(String s) throws IOException {

    DataOutputStream dos = new DataOutputStream(getOutputStream());
    dos.writeUTF(s);
    dos.flush();
  }
  
  protected int newReadPosition(int bytesToRead) throws IOException {
    
    int newPosition = position + bytesToRead;
    if (newPosition > size) {
      throw new EOFException();
    }
    return newPosition;
  }
  
  public InputStream getInputStream() {
    
    if (inputStream == null) {
      inputStream = new Input(this);
    }
    return inputStream;
  }
  
  public int read() throws IOException {
    
    if (position == size) {
      return -1;
    }
    return buffer[position++] & 0xFF;
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
  
    if (position == size) {
      return -1;
    }
    
    int copySize = Math.min(len, size - position);
    System.arraycopy(buffer, position, b, off, copySize);
    position = position + copySize;
    return copySize;
  }

  public void readFully(byte[] b) throws IOException {

    readFully(b, 0, b.length);
  }

  public void readFully(byte[] b, int off, int len) throws IOException {

    int pos = newReadPosition(len);
    System.arraycopy(buffer, position, b, off, len);
    position = pos;
  }

  public int skipBytes(int n) throws IOException {

    int skipped = Math.min(n, size - position);
    position += skipped;
    return skipped;
  }

  public boolean readBoolean() throws IOException {

    return readByte() != 0;
  }

  public byte readByte() throws IOException {

    int pos = newReadPosition(1);
    byte result = buffer[position];
    position = pos;
    return result;
  }

  public int readUnsignedByte() throws IOException {

    int pos = newReadPosition(1);
    int result = buffer[position] & 0xFF;
    position = pos;
    return result;
  }

  public short readShort() throws IOException {

    newReadPosition(2);
    int pos = position;
    int result = buffer[pos++] << 8;
    result |= (buffer[pos++] & 0xFF);
    position = pos;
    return (short) result;
  }

  public int readUnsignedShort() throws IOException {

    newReadPosition(2);
    int pos = position;
    int result = (buffer[pos++] & 0xFF) << 8;
    result |= (buffer[pos++] & 0xFF);
    position = pos;
    return result;
  }

  public char readChar() throws IOException {

    return (char) readUnsignedShort();
  }

  public int readInt() throws IOException {

    newReadPosition(4);
    int pos = position;
    int result = buffer[pos++] << 24;
    result |= (buffer[pos++] & 0xFF) << 16;
    result |= (buffer[pos++] & 0xFF) << 8;
    result |= (buffer[pos++] & 0xFF);
    position = pos;
    return result;
  }

  public long readLong() throws IOException {

    newReadPosition(8);
    int pos = position;
    int high = buffer[pos++] << 24;
    high |= (buffer[pos++] & 0xFF) << 16;
    high |= (buffer[pos++] & 0xFF) << 8;
    high |= (buffer[pos++] & 0xFF);
    int low = buffer[pos++] << 24;
    low |= (buffer[pos++] & 0xFF) << 16;
    low |= (buffer[pos++] & 0xFF) << 8;
    low |= (buffer[pos++] & 0xFF);
    long result = low;
    result |= (high << 32);
    position = pos;
    return result;
  }

  public float readFloat() throws IOException {

    return Float.intBitsToFloat(readInt());
  }

  public double readDouble() throws IOException {

    return Double.longBitsToDouble(readLong());
  }

  public String readLine() throws IOException {

    BufferedReader d = new BufferedReader(new InputStreamReader(getInputStream()));
    return d.readLine();
  }

  public String readUTF() throws IOException {

    DataInputStream d = new DataInputStream(getInputStream());
    return d.readUTF();
  }
}
