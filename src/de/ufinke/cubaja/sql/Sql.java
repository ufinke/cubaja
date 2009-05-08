// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import de.ufinke.cubaja.util.Text;
import java.sql.*;

public class Sql {

  static enum State {    
    DEFAULT,
    LITERAL,
    LINE_COMMENT,
    LINE_COMMENT_START,
    BLOCK_COMMENT,
    BLOCK_COMMENT_START,
    BLOCK_COMMENT_END
  }
  
  static private final Text text = new Text(Sql.class);
  
  private char[] inBuffer;
  private char[] outBuffer;
  private int outLength;
  private String string;
  
  private State state;
  private boolean spacePending;
  private boolean retainBlockComments;
  
  private List<String> statementList;
  
  public Sql() {
    
    inBuffer = new char[1024];
    outBuffer = new char[1024];
    state = State.DEFAULT;
  }
  
  public Sql(String sql) {
    
    this();
    append(sql);
  }
  
  public Sql(Reader reader) throws IOException {
    
    this();
    append(reader);
  }
  
  public Sql(Class<?> packageClass, String sqlResource) throws IOException {
    
    this();
    append(packageClass, sqlResource);
  }
  
  /**
   * Returns the formatted SQL statement.
   */
  public String toString() {
    
    if (string == null) {
      string = String.valueOf(outBuffer, 0, outLength);
    }
    return string;
  }
  
  List<String> getStatements() {
    
    saveStatement();
    return statementList;
  }
  
  String getSingleStatement() throws SQLException {
    
    saveStatement();
    
    switch (statementList.size()) {
      case 0:
        throw new SQLException(text.get("noStatement"));
      case 1:
        return statementList.get(0);
      default:
        throw new SQLException(text.get("multiStatement"));
    }
  }
  
  boolean hasVariables() {
    
    //TODO
    return false;
  }
  
  /**
   * Sets flag wether to retain block comments.
   * Default behavior is to remove comments.
   * @param retainBlockComments
   * @return this
   */
  public Sql retainBlockComments(boolean retainBlockComments) {
    
    this.retainBlockComments = retainBlockComments;
    return this;
  }
  
  public Sql append(String line) {
    
    if (line == null) {
      return this;
    }
    
    int length = line.length();
    if (length >= inBuffer.length) {
      inBuffer = new char[length + 1];
    }
    line.getChars(0, length, inBuffer, 0);
    inBuffer[length] = '\n';
    append(inBuffer, length + 1);
    
    return this;
  }
  
  public Sql append(Reader reader) throws IOException {

    int length = reader.read(inBuffer);
    while (length > 0) {
      append(inBuffer, length);
      length = reader.read(inBuffer);
    }
    
    return this;
  }
  
  public Sql append(Class<?> packageClass, String sqlResource) throws IOException {
    
    InputStream stream = packageClass.getResourceAsStream(sqlResource + ".sql");
    if (stream == null) {
      StringBuilder sb = new StringBuilder(150);
      if (sqlResource.startsWith("/")) {
        sb.append(sqlResource);
      } else {
        sb.append(packageClass.getPackage().getName().replace('.', '/'));
        sb.append('/');
        sb.append(sqlResource);
        sb.append(".sql");
      }
      throw new IOException(text.get("resourceNotFound", sb.toString()));
    }
    
    append(new BufferedReader(new InputStreamReader(new BufferedInputStream(stream), "UTF-8")));
    
    return this;
  }
  
  private void append(char[] in, int length) {
    
    string = null;
    
    char[] out = outBuffer;
    int pos = outLength;
    
    for (int i = 0; i < length; i++) {
      
      char c = in[i];
      boolean accept = true;
      boolean removeLastChar = false;
      
      switch (state) {
      
        case DEFAULT:
          switch (c) {
            case ' ':
            case '\t':
            case '\r':
            case '\n':
              accept = false;
              spacePending = true;
              break;
            case '\'':
            case '\"':
              state = State.LITERAL;
              break;
            case '-':
              state = State.LINE_COMMENT_START;
              break;
            case '/':
              state = State.BLOCK_COMMENT_START;
              break;
            case ';':
              accept = false;
              outLength = pos;
              saveStatement();
              pos = 0;
              break;
          }
          break;
          
        case LITERAL:
          state = State.DEFAULT;
          break;
          
        case LINE_COMMENT:
          accept = false;
          switch (c) {
            case '\r':
            case '\n':
              state = State.DEFAULT;
              break;
          }
          break;
          
        case LINE_COMMENT_START:
          switch (c) {
            case '-':
              state = State.LINE_COMMENT;
              accept = false;
              removeLastChar = true;
              break;
            case '/':
              state = State.BLOCK_COMMENT_START;
              break;
            default:
              state = State.DEFAULT;
          }
          break;
          
        case BLOCK_COMMENT:
          accept = false;
          switch (c) {
            case '*':
              state = State.BLOCK_COMMENT_END;
              break;
          }
          break;
          
        case BLOCK_COMMENT_START:
          switch (c) {
            case '*':
              if (retainBlockComments) {
                state = State.DEFAULT;
              } else {
                state = State.BLOCK_COMMENT;
                accept = false;
                removeLastChar = true;
              }
              break;
            case '-':
              state = State.LINE_COMMENT_START;
              break;
            case '/':
              break;
            default:
              state = State.DEFAULT;
          }
          break;
          
        case BLOCK_COMMENT_END:
          accept = false;
          switch (c) {
            case '/':
              state = State.DEFAULT;
              break;
            case '*':
              break;
            default:
              state = State.BLOCK_COMMENT;
          }
          break;
      }
      
      if (accept) {

        if (spacePending) {
          if (pos == out.length) {
            out = enlargeBuffer();
          }
          out[pos++] = ' ';
          spacePending = false;
        }
        
        if (pos == out.length) {
          out = enlargeBuffer();
        }
        out[pos++] = c;
        
      } else if (removeLastChar) {
        
        pos--;
        while (pos >= 0 && out[pos] == ' ') {
          pos--;
          spacePending = true;
        }
        
      }
    }
    
    outLength = pos;
  }
  
  private char[] enlargeBuffer() {
    
    char[] newOut = new char[outBuffer.length << 1];
    System.arraycopy(outBuffer, 0, newOut, 0, outBuffer.length);
    outBuffer = newOut;
    return newOut;
  }
  
  private void saveStatement() {
    
    if (statementList == null) {
      statementList = new ArrayList<String>();
    }
    
    if (outLength > 0) {
      statementList.add(toString());
      outLength = 0;
    }    
    spacePending = false;
  }
}
