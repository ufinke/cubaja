// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.util.Arrays;

public class DefaultLineParser implements LineParser {

  private char separator;
  private String line;
  private int count;
  private int[] startArray;
  private int[] endArray;
  
  public DefaultLineParser() {
    
    startArray = new int[32];
    endArray = new int[32];
  }

  public void init(CsvConfig config) throws CsvException {

    separator = config.getSeparator();
  }

  public void setLine(String line, int lineNumber) throws CsvException {

    this.line = line;

    int[] start = startArray;
    int[] end = endArray;
    
    int i = 0;
    int limit = line.length();
    
    int startIndex = 0;
    int endIndex = 0;
    
    while (startIndex <= limit) {
      
      i++;
      
      if (i == start.length) {
        int newCapacity = start.length << 1;
        startArray = Arrays.copyOf(start, newCapacity);
        endArray = Arrays.copyOf(end, newCapacity);
        start = startArray;
        end = endArray;
      }
      
      endIndex = line.indexOf(separator, startIndex);      
      if (endIndex < 0) {
        endIndex = limit;
      }
      
      start[i] = startIndex;
      end[i] = endIndex;
      
      startIndex = endIndex + 1;
    }
    
    count = i;
  }
  
  public int getColumnCount() {

    return count;
  }

  public String getColumn(int index) throws CsvException {

    return line.substring(startArray[index], endArray[index]);
  }

}
