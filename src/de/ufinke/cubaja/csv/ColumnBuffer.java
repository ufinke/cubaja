// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;

class ColumnBuffer {

  private String[] col;
  private int lastPosition;
  
  ColumnBuffer(int capacity) {
    
    col = new String[capacity]; 
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
    
    String[] newCol = new String[minPosition + 1];
    for (int i = 1; i < col.length; i++) {
      newCol[i] = col[i];
    }
    col = newCol;
  }
  
  public void writeRow(RowFormatter formatter) throws IOException, CsvException {
    
    for (int i = 1; i <= lastPosition; i++) {
      String value = col[i];
      if (value == null) {
        value = "";
      } else {
        col[i] = null;
      }
      formatter.writeColumn(value);
    }
    formatter.writeRow();
    
    lastPosition = 0;
  }
}
