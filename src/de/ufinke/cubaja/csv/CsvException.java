// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import de.ufinke.cubaja.util.*;

/**
 * Exception thrown during CSV processing.
 * @author Uwe Finke
 */
public class CsvException extends Exception {

  static private final Text text = new Text(CsvException.class);
  
  private String message;
  private int lineNumber;
  private int recordNumber;
  private int columnNumber;
  private String columnName;
  private String recordContent;
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
   * Constructor with record information.
   * @param message
   * @param lineNumber
   * @param recordNumber
   * @param recordContent
   */
  public CsvException(String message, int lineNumber, int recordNumber, String recordContent) {

    this(message, null, lineNumber, recordNumber, recordContent, 0, null, null);
  }

  /**
   * Constructor with cause and record information.
   * @param message
   * @param cause
   * @param lineNumber
   * @param recordNumber
   * @param recordContent
   */
  public CsvException(String message, Throwable cause, int lineNumber, int recordNumber, String recordContent) {

    this(message, cause, lineNumber, recordNumber, recordContent, 0, null, null);
  }

  /**
   * Constructor with record and column information.
   * @param message
   * @param lineNumber
   * @param recordNumber
   * @param recordContent
   * @param columnNumber
   * @param columnName
   * @param columnContent
   */
  public CsvException(String message, int lineNumber, int recordNumber, String recordContent, int columnNumber, String columnName, String columnContent) {

    this(message, null, lineNumber, recordNumber, recordContent, columnNumber, columnName, columnContent);
  }

  /**
   * Constructor with cause, record and column information.
   * @param message
   * @param cause
   * @param lineNumber
   * @param recordNumber
   * @param recordContent
   * @param columnNumber
   * @param columnName
   * @param columnContent
   */
  public CsvException(String message, Throwable cause, int lineNumber, int recordNumber, String recordContent, int columnNumber, String columnName, String columnContent) {

    super(cause);

    this.message = message;
    this.lineNumber = lineNumber;
    this.recordNumber = recordNumber;
    this.recordContent = recordContent;
    this.columnNumber = columnNumber;
    this.columnName = columnName;
    this.columnContent = columnContent;
  }
  
  /**
   * Returns a message with location information if applicable.
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
        sb.append(text.get("constRecord"));
        sb.append(' ');
        sb.append(recordNumber);
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
   * Returns the line number where the error occured.
   * If not applicable, the line number is <code>0</code>.
   * @return line number
   */
  public int getLineNumber() {

    return lineNumber;
  }
  
  /**
   * Returns the record number where the error occured.
   * If not applicable, the record number is <code>0</code>.
   * @return record number
   */
  public int getRecordNumber() {
    
    return recordNumber;
  }

  /**
   * Returns the number of the erroneous column.
   * If not applicable, the column number is <code>0</code>.
   * @return column number
   */
  public int getColumnNumber() {

    return columnNumber;
  }
  
  /**
   * Returns the name of the erroneous column.
   * If not applicable, the column name is <code>null</code>.
   * @return column name
   */
  public String getColumnName() {
    
    return columnName;
  }

  /**
   * Returns the complete record in error.
   * If not applicable, the record is <code>null</code>.
   * @return record content
   */
  public String getRecordContent() {

    return recordContent;
  }

  /**
   * Returns the content of the erroneous column.
   * If not applicable, the column is <code>null</code>.
   * @return column content
   */
  public String getColumnContent() {

    return columnContent;
  }

}
