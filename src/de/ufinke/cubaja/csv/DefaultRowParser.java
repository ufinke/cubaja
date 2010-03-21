// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import de.ufinke.cubaja.util.Text;

/**
 * Default <tt>RowParser</tt> implementation.
 * If an escape character is defined, this parser supposes
 * escape character usage as described in 
 * {@link <a href="http://tools.ietf.org/html/rfc4180">rfc4180</a>},
 * with the exception that this parser allows any character to be an escape character
 * and any CR / LF combination to be a line break.
 * @author Uwe Finke
 */
public class DefaultRowParser implements RowParser {

  static private final Text text = new Text(DefaultRowParser.class);
  
  private char separator;
  private char escapeChar;
  private boolean escapeDefined;

  private LineNumberReader lineReader;  
  private String row;
  
  private int count;
  private int[] startArray;
  private int[] endArray;
  private boolean[] escapeArray; // true: column contains at least one escape char
  
  /**
   * Constructor.
   */
  public DefaultRowParser() {
    
    startArray = new int[32];
    endArray = new int[32];
    escapeArray = new boolean[32];
  }

  /**
   * Initializes the parser.
   */
  public void init(Reader in, CsvConfig config) throws CsvException {

    separator = config.getSeparator();
    escapeDefined = (config.getEscapeChar() != null);
    if (escapeDefined) {
      escapeChar = config.getEscapeChar();
    }
    
    lineReader = (in instanceof LineNumberReader) ? (LineNumberReader) in : new LineNumberReader(in);
  }

  public String readRow() throws IOException, CsvException {

    row = escapeDefined ? parseEscape() : parseSimple();    
    return row;
  }
  
  private String parseSimple() throws IOException {

    String line = lineReader.readLine();
    if (line == null) {
      count = 0;
      return null;
    }
    
    char sep = separator;
    
    int[] start = startArray;
    int[] end = endArray;
    
    int i = 0;
    int limit = line.length();
    
    int startIndex = 0;
    int endIndex = 0;
    
    while (startIndex <= limit) {
      
      i++;
      
      if (i == start.length) {
        enlargeArrays();
        start = startArray;
        end = endArray;
      }
      
      endIndex = line.indexOf(sep, startIndex);      
      if (endIndex < 0) {
        endIndex = limit;
      }
      
      start[i] = startIndex;
      end[i] = endIndex;
      
      startIndex = endIndex + 1;
    }
    
    count = i;
    
    return line;
  }
  
  private String parseEscape() throws IOException, CsvException {

    String line = lineReader.readLine();
    if (line == null) {
      count = 0;
      return null;
    }
    
    char sep = separator;
    char esc = escapeChar;
    
    int[] start = startArray;
    int[] end = endArray;
    boolean[] doubleEscape = escapeArray;
    
    int i = 0;
    int limit = line.length();    
    int startIndex = 0;
    int nextColStart = 0;
    
    while (startIndex <= limit) {
      
      i++;
      
      if (i == start.length) {
        enlargeArrays();
        start = startArray;
        end = endArray;
        doubleEscape = escapeArray;
      }
      
      doubleEscape[i] = false;
      int endIndex = startIndex;
      
      if (startIndex < limit && line.charAt(startIndex) == esc) { // escaped
        
        startIndex++;
        endIndex++;
        boolean escaped = true;
        
        while (escaped) {
          
          while (endIndex == limit) {
            String contLine = lineReader.readLine();
            if (contLine == null) {
              throw new CsvException(text.get("eofEscaped"));
            } else {
              line = line + contLine;
              limit = line.length();
            }
          }
          
          if (line.charAt(endIndex) == esc) {
            nextColStart = endIndex + 1;
            if (nextColStart < limit) {
              char nextChar = line.charAt(nextColStart);
              if (nextChar == sep) {
                escaped = false;
                nextColStart++;
              } else if (nextChar == esc) {
                doubleEscape[i] = true;
                endIndex++;
              } else {
                throw new CsvException(text.get("escapeInEscape"));
              }
            } else {
              escaped = false;
            }
          } else {
            endIndex++;
          }
        }
        
      } else { // non-escaped
        
        while (endIndex < limit && line.charAt(endIndex) != sep) {
          endIndex++;
        }
        
        nextColStart = endIndex + 1;
        
      } // end escaped / non-escaped
      
      start[i] = startIndex;
      end[i] = endIndex;

      startIndex = nextColStart;
    }
    
    count = i;
    
    return line;
  }
  
  /**
   * Returns number of columns of current row.
   */
  public int getColumnCount() {

    return count;
  }
  
  /**
   * Returns the number of raw lines.
   */
  public int getLineCount() {
    
    return lineReader.getLineNumber();
  }
  
  /**
   * Returns whether all column data have zero length.
   */
  public boolean isEmptyRow() {
    
    for (int i = 0; i < count; i++) {
      if (startArray[i] != endArray[i]) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns column content.
   */
  public String getColumn(int index) throws CsvException {

    if (escapeDefined && escapeArray[index]) {
      return removeDoubleEscape(index);
    } else {
      return row.substring(startArray[index], endArray[index]);
    }
  }
  
  private String removeDoubleEscape(int index) {
    
    int start = startArray[index];
    int end = endArray[index];
    char esc = escapeChar;
    
    StringBuilder sb = new StringBuilder(end - start);
    
    int i = start;    
    while (i < end) {
      char c = row.charAt(i);
      if (c == esc) {
        i++;
      }
      sb.append(c);
      i++;
    }
    
    return sb.toString();
  }
  
  private void enlargeArrays() {
    
    int oldCapacity = startArray.length;
    int newCapacity = oldCapacity << 1;
    
    int[] newStartArray = new int[newCapacity];
    System.arraycopy(startArray, 0, newStartArray, 0, oldCapacity);
    startArray = newStartArray;
    
    int[] newEndArray = new int[newCapacity];
    System.arraycopy(endArray, 0, newEndArray, 0, oldCapacity);
    endArray = newEndArray;
    
    boolean[] newEscapeArray = new boolean[newCapacity];
    System.arraycopy(escapeArray, 0, newEscapeArray, 0, oldCapacity);
    escapeArray = newEscapeArray;
  }

}
