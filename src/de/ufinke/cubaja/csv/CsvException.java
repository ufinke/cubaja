// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

/**
 * Exception thrown during CSV processing.
 * This is a <code>RuntimeException</code>
 * because of <code>Iterable</code> usage
 * whose methods accept no exception declarations.
 * @author Uwe Finke
 */
public class CsvException extends RuntimeException {

  private int lineNumber;
  private int columnNumber;
  private String line;
  private String column;

  /**
   * Simple constructor.
   * @param message
   */
  public CsvException(String message) {

    this(message, null, 0, 0, null, null);
  }

  /**
   * Simple constructor with cause.
   * @param message
   * @param cause
   */
  public CsvException(String message, Throwable cause) {

    this(message, cause, 0, 0, null, null);
  }

  /**
   * Constructor with line information.
   * @param message
   * @param lineNumber
   * @param line
   */
  public CsvException(String message, int lineNumber, String line) {

    this(message, null, lineNumber, 0, line, null);
  }

  /**
   * Constructor with cause and line information.
   * @param message
   * @param cause
   * @param lineNumber
   * @param line
   */
  public CsvException(String message, Throwable cause, int lineNumber, String line) {

    this(message, cause, lineNumber, 0, line, null);
  }

  /**
   * Constructor with line and column information.
   * @param message
   * @param lineNumber
   * @param columnNumber
   * @param line
   * @param column
   */
  public CsvException(String message, int lineNumber, int columnNumber, String line, String column) {

    this(message, null, lineNumber, columnNumber, line, column);
  }

  /**
   * Constructor with cause, line and column information.
   * @param message
   * @param cause
   * @param lineNumber
   * @param columnNumber
   * @param line
   * @param column
   */
  public CsvException(String message, Throwable cause, int lineNumber, int columnNumber, String line, String column) {

    super(cause);

    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
    this.line = line;
    this.column = column;
  }

  /**
   * Returns the line number where the error occured.
   * If not applicable, the line number is <code>0</code>.
   * @return line number
   */
  public int getLineNumber() {

    return lineNumber;
  }

  /**
   * Returns the column number where the error occured.
   * If not applicable, the column number is <code>0</code>.
   * @return column number
   */
  public int getColumnNumber() {

    return columnNumber;
  }

  /**
   * Returns the complete line of error.
   * If not applicable, the line is <code>null</code>.
   * @return line
   */
  public String getLine() {

    return line;
  }

  /**
   * Returns the content of the erroneous column.
   * If not applicable, the column is <code>null</code>.
   * @return line
   */
  public String getColumn() {

    return column;
  }

}
