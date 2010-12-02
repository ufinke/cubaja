// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class XMLWriter {

  private Writer writer;
  
  public XMLWriter(OutputStream stream) throws Exception {
    
    this(stream, new XMLWriterConfig());
  }
  
  public XMLWriter(OutputStream stream, XMLWriterConfig config) throws Exception {
  
    writer = new BufferedWriter(new OutputStreamWriter(stream, config.getEncoding()));
  }
  
  public void close() throws Exception {
    
    writer.close();
  }
  
  public void write(Object config) throws Exception {
    
  }
}
