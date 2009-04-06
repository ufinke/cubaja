// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.*;
import de.ufinke.cubaja.util.*;

public abstract class Streamer<D> implements Closeable {
  
  static protected Text text = new Text(Streamer.class);
  
  static protected void nullStream() {
    
    throw new IllegalStateException(text.get("nullStream"));
  }
  
  protected BinaryOutputStream out;
  protected BinaryInputStream in;
  
  protected Streamer() {
    
  }
  
  public void openOutput(OutputStream stream) {
    
    checkOpened();
    out = new BinaryOutputStream(stream);
  }
  
  public void openInput(InputStream stream) {
    
    checkOpened();
    in = new BinaryInputStream(stream);
  }
  
  private void checkOpened() {
    
    if (out != null || in != null) {
      throw new IllegalStateException(text.get("alreadyOpened"));
    }
  }
  
  public void close() throws IOException {
    
    if (out != null) {
      out.close();
      out = null;
    }
    
    if (in != null) {
      in.close();
      in = null;
    }
  }
  
  public abstract void write(D object) throws Exception;
  
  public abstract D read() throws Exception;
  
}