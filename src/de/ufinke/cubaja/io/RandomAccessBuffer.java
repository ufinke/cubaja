// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RandomAccessBuffer implements DataOutput {

  static private class Output extends OutputStream {
  
    private RandomAccessBuffer master;
    
    public Output(RandomAccessBuffer master) {
    
      this.master = master;
    }

    public void write(int b) {

      master.write(b);
    }
    
    public void write(byte[] b, int off, int len) {
      
      master.write(b, off, len);
    }
  }
  
  static private final int DEFAULT_CAPACITY = 4096;
  
  private byte[] buffer;
  private int capacity;
  private int growthCapacity;
  private int size;
  private int position;
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
  }
  
  public int getPosition() {
    
    return position;
  }
  
  public int size() {
    
    return size;
  }
  
  private int getNewPosition(int bytesToWrite) {
    
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

  public void write(int b) {

    int pos = getNewPosition(1);
    buffer[position] = (byte) b;
    position = pos;
  }

  public void write(byte[] b) {

    write(b, 0, b.length);
  }

  public void write(byte[] b, int off, int len) {

    int pos = getNewPosition(len);
    System.arraycopy(b, off, buffer, position, len);
    position = pos;
  }

  public void writeBoolean(boolean v) {

    write(v ? 1 : 0);
  }

  public void writeByte(int v) {

    write(v);
  }

  public void writeShort(int v) {

    getNewPosition(2);
    int pos = position;
    buffer[pos++] = (byte) (v >> 8);
    buffer[pos++] = (byte) v;
    position = pos;
  }

  public void writeChar(int v) {

    writeShort(v);
  }

  public void writeInt(int v) {

    getNewPosition(4);
    int pos = position;
    buffer[pos++] = (byte) (v >> 24);
    buffer[pos++] = (byte) (v >> 16);
    buffer[pos++] = (byte) (v >> 8);
    buffer[pos++] = (byte) v;
    position = pos;
  }

  public void writeLong(long v) {

    getNewPosition(8);
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

  public void writeFloat(float v) {

    writeInt(Float.floatToIntBits(v));
  }

  public void writeDouble(double v) {

    writeLong(Double.doubleToLongBits(v));
  }

  public void writeBytes(String s) {

    int len = s.length();
    getNewPosition(len);
    int pos = position;
    for (int i = 0; i < len; i++) {
      buffer[pos++] = (byte) s.charAt(i);
    }
    position = pos;
  }

  public void writeChars(String s) throws IOException {

    int len = s.length();
    getNewPosition(len);
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
}
