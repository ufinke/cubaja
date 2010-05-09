package de.ufinke.cubaja.io;

import java.io.*;

public class FixedMainframeInput implements MainframeInput {

  private DataInputStream stream;
  private int recordLength;
  
  public FixedMainframeInput(InputStream stream, int recordLength) {
  
    this.stream = new DataInputStream(stream);
    this.recordLength = recordLength;
  }

  public boolean nextRecord(RandomAccessBuffer buffer) throws Exception {

    try {
      buffer.transferFullyFrom(stream, recordLength);
      return true;
    } catch (EOFException eof) {
      return false;
    }
  }

  public void close() throws Exception {

    stream.close();
  }
}
