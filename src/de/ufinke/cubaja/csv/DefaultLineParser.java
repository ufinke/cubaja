// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Arrays;

/**
 * Default <code>LineParser</code> implementation.
 * @author Uwe Finke
 */
public class DefaultLineParser implements LineParser {

  private char separator;
  private char escapeChar;
  private boolean escapeDefined;

  private LineNumberReader lineReader;
  private Reader reader;
  
  private char[] buffer;
  private String line;
  
  private int count;
  private int[] startArray;
  private int[] endArray;
  private boolean[] escapeArray; // true: column contains at least one escape char
  
  /**
   * Constructor.
   */
  public DefaultLineParser() {
    
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
      buffer = new char[256];
      reader = (in instanceof BufferedReader) ? in : new BufferedReader(in);
    } else {
      lineReader = (in instanceof LineNumberReader) ? (LineNumberReader) in : new LineNumberReader(in);
    }
  }

  /**
   * Reads the next line.
   */
  public String readLine() throws IOException {

    line = escapeDefined ? parseEscape() : parseSimple();    
    return line;
  }
  
  private String parseSimple() throws IOException {

    String line = lineReader.readLine();
    if (line == null) {
      return null;
    }
    
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
        escapeArray = Arrays.copyOf(escapeArray, newCapacity);
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
    
    return line;
  }
  
  private String parseEscape() throws IOException {

    char[] buf = buffer;
    int pos = 0;
    
    char sep = separator;
    char esc = escapeChar;
    
    int[] start = startArray;
    int[] end = endArray;
    boolean[] doubleEscape = escapeArray;
    
    int i = 0;
    int limit = line.length();
    
    int startIndex = 0;
    int endIndex = 0;
    boolean noLiteral = true;
    
    while (startIndex <= limit) {
      
      i++;
      
      if (i == start.length) {
        int newCapacity = start.length << 1;
        startArray = Arrays.copyOf(start, newCapacity);
        endArray = Arrays.copyOf(end, newCapacity);
        escapeArray = Arrays.copyOf(escapeArray, newCapacity);
        start = startArray;
        end = endArray;
      }
      
      doubleEscape[i] = false;
      
      boolean searchForEnd = true;
      while (searchForEnd && endIndex < limit) {
        char c = line.charAt(i);
        if (c == separator && noLiteral) {
          searchForEnd = false;
        } else if (c == escapeChar) {
          noLiteral = ! noLiteral;
        }
        endIndex++;
      }

      if (line.charAt(startIndex) == escapeChar) {
        startIndex++;
        if (line.charAt(endIndex) == escapeChar) {
          endIndex--;
        }
      }
            
      start[i] = startIndex;
      end[i] = endIndex;
      
      startIndex = endIndex + 1;
    }
    
    count = i;
    
    return String.valueOf(buffer, 0, pos);
  }
  
  /**
   * Returns number of columns of current line.
   */
  public int getColumnCount() {

    return count;
  }

  /**
   * Returns column content.
   */
  public String getColumn(int index) throws CsvException {

    if (escapeDefined && escapeArray[index]) {
      return removeDoubleEscape(index);
    } else {
      return line.substring(startArray[index], endArray[index]);
    }
  }
  
  private String removeDoubleEscape(int index) {
    
    int start = startArray[index];
    int end = endArray[index];
    
    StringBuilder sb = new StringBuilder(end - start);
    
    boolean ignoreEscape = false;
    for (int i = start; i < end; i++) {
      char c = line.charAt(i);
      if (c == escapeChar) {
        if (! ignoreEscape) {
          sb.append(c);
        }
        ignoreEscape = ! ignoreEscape;
      } else {
        ignoreEscape = false;
        sb.append(c);
      }
    }
    
    return sb.toString();
  }

}
