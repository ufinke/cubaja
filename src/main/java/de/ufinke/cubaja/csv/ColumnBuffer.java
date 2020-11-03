// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;

class ColumnBuffer {

  private CsvConfig config;
  private String[] col;
  private String[] nullValue;
  private int lastPosition;
  
  ColumnBuffer(CsvConfig config) {
    
    this.config = config;
    col = new String[32]; 
    initNullValues();
  }
  
  public void setColumn(int position, String value) {
    
    if (col.length <= position) {
      enlarge(position);
    }
    
    col[position] = value;
    
    if (lastPosition < position) {
      lastPosition = position;
    }
  }
  
  private void enlarge(int minPosition) {
    
    String[] newCol = new String[Math.max(col.length + 32, minPosition + 1)];
    for (int i = 1; i < col.length; i++) {
      newCol[i] = col[i];
    }
    col = newCol;
    
    initNullValues();
  }
  
  public void writeRow(RowFormatter formatter) throws IOException, CsvException {
    
    for (int i = 1; i <= lastPosition; i++) {
      String value = col[i];
      if (value == null) {
        value = nullValue[i];
      } else {
        col[i] = nullValue[i];
      }
      formatter.writeColumn(value);
    }
    formatter.writeRow();
    
    lastPosition = 0;
  }
  
  private void initNullValues() {
    
    nullValue = new String[col.length];
    for (int i = 1; i < nullValue.length; i++) {
      nullValue[i] = config.getColConfig(i).getNullValue();
    }
  }
}
