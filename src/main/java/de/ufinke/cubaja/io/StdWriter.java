// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Implementation of <tt>StdProcessor</tt> which writes lines to a file.
 * @author Uwe Finke
 */
public class StdWriter implements StdProcessor {

  private BufferedWriter writer;
  private boolean flush;

  /**
   * Constructor with <tt>FileConfig</tt>.
   * @param config
   * @throws IOException
   */
  public StdWriter(FileConfig config) throws IOException {
    
    writer = config.createWriter();
    flush = true;
  }
  
  /**
   * Constructor with <tt>File</tt>.
   * @param file
   * @throws IOException
   */
  public StdWriter(File file) throws IOException {
  
    writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file))));
    flush = true;
  }
  
  /**
   * Constructor with file name.
   * @param file
   * @throws IOException
   */
  public StdWriter(String file) throws IOException {
    
    this(new File(file));
  }
  
  /**
   * Sets the <tt>flush</tt> property.
   * When this property is set to <tt>true</tt> the buffers
   * are flushed after every line.
   * Default setting in the constructors is <tt>true</tt>.
   * @param flush
   */
  public void setFlush(boolean flush) {
    
    this.flush = flush;
  }

  /**
   * Writes a line to the file.
   */
  public void processLine(String line) throws Exception {

    writer.write(line, 0, line.length());
    writer.newLine();
    if (flush) {
      writer.flush();
    }
  }
  
  /**
   * Closes the file.
   */
  public void close() throws Exception {
    
    writer.close();
  }
}
