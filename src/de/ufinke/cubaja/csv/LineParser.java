// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

public interface LineParser {

  public void init(CsvConfig config) throws Exception;
  
  public void setLine(String line) throws Exception;
  
  public String getColumn(int index) throws Exception;
  
  public int getColumnCount();
}
