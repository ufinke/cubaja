// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import de.ufinke.cubaja.util.Text;

/**
 * Exception thrown during CSV processing.
 * @author Uwe Finke
 */
public class CsvException extends Exception {

  static private final Text text = Text.getPackageInstance(CsvException.class);
  
  private String message;
  private int lineNumber;
  private int rowNumber;
  private int columnNumber;
  private String columnName;
  private String rowContent;
  private String columnContent;

  private String locationMessage;

  /**
   * Simple constructor.
   * @param message
   */
  public CsvException(String message) {

    this(message, null, 0, 0, null, 0, null, null);
  }

  /**
   * Simple constructor with cause.
   * @param message
   * @param cause
   */
  public CsvException(String message, Throwable cause) {

    this(message, cause, 0, 0, null, 0, null, null);
  }

  /**
   * Constructor with row information.
   * @param message
   * @param lineNumber
   * @param rowNumber
   * @param rowContent
   */
  public CsvException(String message, int lineNumber, int rowNumber, String rowContent) {

    this(message, null, lineNumber, rowNumber, rowContent, 0, null, null);
  }

  /**
   * Constructor with cause and row information.
   * @param message
   * @param cause
   * @param lineNumber
   * @param rowNumber
   * @param rowContent
   */
  public CsvException(String message, Throwable cause, int lineNumber, int rowNumber, String rowContent) {

    this(message, cause, lineNumber, rowNumber, rowContent, 0, null, null);
  }

  /**
   * Constructor with row and column information.
   * @param message
   * @param lineNumber
   * @param rowNumber
   * @param rowContent
   * @param columnNumber
   * @param columnName
   * @param columnContent
   */
  public CsvException(String message, int lineNumber, int rowNumber, String rowContent, int columnNumber, String columnName, String columnContent) {

    this(message, null, lineNumber, rowNumber, rowContent, columnNumber, columnName, columnContent);
  }

  /**
   * Constructor with cause, row and column information.
   * @param message
   * @param cause
   * @param lineNumber
   * @param rowNumber
   * @param rowContent
   * @param columnNumber
   * @param columnName
   * @param columnContent
   */
  public CsvException(String message, Throwable cause, int lineNumber, int rowNumber, String rowContent, int columnNumber, String columnName, String columnContent) {

    super(cause);

    this.message = message;
    this.lineNumber = lineNumber;
    this.rowNumber = rowNumber;
    this.rowContent = rowContent;
    this.columnNumber = columnNumber;
    this.columnName = columnName;
    this.columnContent = columnContent;
  }
  
  /**
   * Returns a detailed message with location information.
   */
  public String getMessage() {
    
    if (locationMessage == null) {
      if (message == null && getCause() != null) {
        message = getCause().toString();
      }
      if (lineNumber == 0) {
        locationMessage = message;
      } else {
        StringBuilder sb = new StringBuilder(200);
        sb.append(message);
        sb.append(" (");
        sb.append(text.get("constLine"));
        sb.append(' ');
        sb.append(lineNumber);
        sb.append(", ");
        sb.append(text.get("constRow"));
        sb.append(' ');
        sb.append(rowNumber);
        if (columnNumber != 0) {
          sb.append(", ");
          sb.append(text.get("constColumn"));
          sb.append(' ');
          if (columnName != null) {
            sb.append(columnName);
          } else {
            sb.append(columnNumber);
          }
        }
        sb.append(')');
        locationMessage = sb.toString();
      }
    }
    
    return locationMessage;
  }
  
  /**
   * Returns the plain message text.
   * @return message supplied by constructor
   */
  public String getPlainMessage() {
    
    return message;
  }
  
  /**
   * Returns the line number where the error occured.
   * If not applicable, the line number is <tt>0</tt>.
   * @return line number
   */
  public int getLineNumber() {

    return lineNumber;
  }
  
  /**
   * Returns the row number where the error occured.
   * If not applicable, the row number is <tt>0</tt>.
   * @return row number
   */
  public int getRowNumber() {
    
    return rowNumber;
  }

  /**
   * Returns the number of the erroneous column.
   * If not applicable, the column number is <tt>0</tt>.
   * @return column number
   */
  public int getColumnNumber() {

    return columnNumber;
  }
  
  /**
   * Returns the name of the erroneous column.
   * If not applicable, the column name is <tt>null</tt>.
   * @return column name
   */
  public String getColumnName() {
    
    return columnName;
  }

  /**
   * Returns the complete row in error.
   * If not applicable, the row is <tt>null</tt>.
   * @return row content
   */
  public String getRowContent() {

    return rowContent;
  }

  /**
   * Returns the content of the erroneous column.
   * If not applicable, the column is <tt>null</tt>.
   * @return column content
   */
  public String getColumnContent() {

    return columnContent;
  }

}
