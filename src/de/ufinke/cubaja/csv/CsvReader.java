// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.*;
import de.ufinke.cubaja.config.*;
import java.util.*;

public class CsvReader {

  private LineNumberReader reader;
  
  public CsvReader(CsvConfig config) throws IOException, ConfigException {
    
    this(config.createReader(), config);
  }

  public CsvReader(Reader reader) {
  
    this(reader, new CsvConfig());
  }
  
  public CsvReader(Reader reader, CsvConfig config) {
  
    this.reader = new LineNumberReader(reader);
  }
  
  public void close() throws IOException {
    
    reader.close();
  }
  
  public Record getHeaderRecord() {
    
  }
  
  public Iterator<Record> getRecords() {
    
  }
}
