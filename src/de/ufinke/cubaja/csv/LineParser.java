// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

public interface LineParser {

  public void init(CsvConfig config) throws CsvException;
  
  public void setLine(String line, int lineNumber) throws CsvException;
  
  public String getColumn(int index) throws CsvException;
  
  public int getColumnCount();
  
}
