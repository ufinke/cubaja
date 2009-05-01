// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

public class CsvException extends RuntimeException {

  private int lineNumber;
  private int columnNumber;
  private String line;
  private String column;

  public CsvException(String message) {

    this(message, null, 0, 0, null, null);
  }

  public CsvException(String message, int lineNumber, String line) {

    this(message, null, lineNumber, 0, line, null);
  }

  public CsvException(String message, Throwable cause, int lineNumber, String line) {

    this(message, cause, lineNumber, 0, line, null);
  }

  public CsvException(String message, int lineNumber, int columnNumber, String line, String column) {

    this(message, null, lineNumber, columnNumber, line, column);
  }

  public CsvException(String message, Throwable cause, int lineNumber, int columnNumber, String line, String column) {

    super(cause);

    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
    this.line = line;
    this.column = column;
  }

  public int getLineNumber() {

    return lineNumber;
  }

  public int getColumnNumber() {

    return columnNumber;
  }

  public String getLine() {

    return line;
  }

  public String getColumn() {

    return column;
  }

}
