package de.ufinke.cubaja.io;

import java.nio.charset.Charset;

public class MainframeReader {

  private MainframeInput source;
  private Charset charset;
  private RandomAccessBuffer buffer;
  private int recordCount;
  
  public MainframeReader(MainframeInput source, String charset) {
    
    this.source = source;
    this.charset = Charset.forName(charset);
    buffer = new RandomAccessBuffer();
  }
  
  public void close() throws Exception {
    
    source.close();
  }
  
  public boolean nextRecord() throws Exception {
    
    buffer.reset();
    boolean recordExists = source.nextRecord(buffer);
    if (recordExists) {
      recordCount++;
    }
    buffer.setPosition(0);
    return recordExists;
  }
  
  public int getRecordCount() {
    
    return recordCount;
  }
  
  public int getRecordLength() {
    
    return buffer.size();
  }
  
  public void setPosition(int offset) {
    
    buffer.setPosition(offset);
  }
  
  public int read() throws Exception {
    
    return buffer.read();
  }
  
  public void read(byte[] array) throws Exception {
    
    buffer.readFully(array);
  }
  
  public String readString(int length) throws Exception {
    
    byte[] array = new byte[length];
    buffer.readFully(array);
    return new String(array, charset);
  }
  
  public int readInt() throws Exception {
    
    return buffer.readInt();
  }
  
  public short readShort() throws Exception {
    
    return buffer.readShort();
  }
  
  public long readPackedLong(int precision) throws Exception {
    
    //TODO
    return 0;
  }
}
