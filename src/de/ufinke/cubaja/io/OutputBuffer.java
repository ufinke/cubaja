// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.IOException;

public class OutputBuffer extends ByteArrayOutputStream {

  public OutputBuffer() {
    
  }
  
  public OutputBuffer(int size) {
    
    super(size);
  }
  
  public void writeTo(DataOutput dataOutput) throws IOException {
    
    dataOutput.write(buf, 0, count);
  }
  
  public byte[] getBuffer() {
    
    return buf;
  }
}
