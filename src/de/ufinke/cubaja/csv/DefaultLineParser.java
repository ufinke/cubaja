// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Arrays;
import de.ufinke.cubaja.util.*;

/**
 * Default <code>LineParser</code> implementation.
 * @author Uwe Finke
 */
public class DefaultLineParser implements LineParser {

  static private final Text text = new Text(DefaultLineParser.class);
  
  private char separator;
  private char escapeChar;
  private boolean escapeDefined;

  private LineNumberReader lineReader;  
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
    }
    
    lineReader = (in instanceof LineNumberReader) ? (LineNumberReader) in : new LineNumberReader(in);
  }

  /**
   * Reads the next line.
   */
  public String readLine() throws IOException, CsvException {

    line = escapeDefined ? parseEscape() : parseSimple();    
    return line;
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
        int newCapacity = start.length << 1;
        startArray = Arrays.copyOf(start, newCapacity);
        endArray = Arrays.copyOf(end, newCapacity);
        escapeArray = Arrays.copyOf(escapeArray, newCapacity);
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
        int newCapacity = start.length << 1;
        startArray = Arrays.copyOf(start, newCapacity);
        endArray = Arrays.copyOf(end, newCapacity);
        escapeArray = Arrays.copyOf(escapeArray, newCapacity);
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
    char esc = escapeChar;
    
    StringBuilder sb = new StringBuilder(end - start);
    
    int i = start;    
    while (i < end) {
      char c = line.charAt(i);
      if (c == esc) {
        i++;
      }
      sb.append(c);
      i++;
    }
    
    return sb.toString();
  }

}
