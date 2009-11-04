// Copyright (c) 2006 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import de.ufinke.cubaja.util.Text;

public class Sql {

  static enum State {    
    DEFAULT            (false),
    LITERAL            (false),
    LINE_COMMENT       (false),
    LINE_COMMENT_START (true),
    BLOCK_COMMENT      (false),
    BLOCK_COMMENT_START(true),
    BLOCK_COMMENT_END  (false),
    VARIABLE           (false);
    
    boolean commentStart;
    
    private State(boolean commentStart) {
      
      this.commentStart = commentStart;
    }
  }
  
  static private final Text text = new Text(Sql.class);
  
  private char[] inBuffer;
  private char[] outBuffer;
  private int outLength;
  private String string;
  
  private State state;
  private boolean spacePending;
  private boolean retainBlockComments;
  private char literalChar;
  
  private List<String> statementList;
  private List<String> variableList;
  private StringBuilder variable;
  
  public Sql() {
    
    inBuffer = new char[1024];
    outBuffer = new char[1024];
    
    statementList = new ArrayList<String>();
    variableList = new ArrayList<String>();
    variableList.add("*dummy*"); // position starts with 1
    
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
   * Returns the parsed SQL.
   */
  public String toString() {
    
    if (string == null) {
      
      StringBuilder sb = new StringBuilder();
      
      for (int i = 0; i < statementList.size(); i++) {
        if (sb.length() > 0) {
          sb.append("; ");
        }
        sb.append(statementList.get(i));
      }
      
      if (outLength > 0) {
        if (sb.length() > 0) {
          sb.append("; ");
        }
        sb.append(outBuffer, 0, outLength);
      }
      
      string = sb.toString();
    }
    
    return string;
  }
  
  private void saveStatement() {
    
    if (outLength > 0) {
      statementList.add(String.valueOf(outBuffer, 0, outLength));
      outLength = 0;
    }    
    spacePending = false;
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
    
    return variableList.size() > 1;
  }
  
  List<String> getVariables() {
    
    return variableList;
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
  
  public Sql append(Object[] value) {

    if (value == null) {
      return this;
    }
    
    StringBuilder sb = new StringBuilder(128);
    
    for (int i = 0; i < value.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      if (value[i] instanceof Number) {
        sb.append(value[i].toString());
      } else {
        sb.append('\'');
        sb.append(value[i].toString());
        sb.append('\'');
      }
    }
    
    return append(sb.toString());
  }
  
  public Sql append(Collection<?> value) {

    if (value == null) {
      return this;
    }
    
    return append(value.toArray());
  }
  
  public Sql append(int[] value) {
    
    if (value == null) {
      return this;
    }
    
    StringBuilder sb = new StringBuilder(128);
    
    for (int i = 0; i < value.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(value[i]);
    }

    return append(sb.toString());
  }
  
  /**
   * Generates SQL code for <code>update</code> statements.
   * The variables in the list are expanded to 
   * <code>set <i>var1</i> = :<i>var1</i>, <i>var2</i> = :<i>var2</i> ...</code>.
   * @param variables
   */
  public Sql appendUpdate(String... variables) throws SQLException {
    
    StringBuilder sb = new StringBuilder(256);
    
    String separator = "set ";
    for (String variable : variables) {
      sb.append(separator);
      sb.append(variable);
      sb.append(" = :");
      sb.append(variable);
      separator = ", ";
    }
    
    return append(sb.toString());
  }
  
  /**
   * Generates SQL code for <code>insert</code> statements.
   * The variables in the list are expanded to 
   * <code>(<i>var1</i>, <i>var2</i>, ...) values (:<i>var1</i>, :<i>var2</i> ...)</code>.
   * @param variables
   */
  public Sql appendInsert(String... variables) throws SQLException {

    StringBuilder sb = new StringBuilder(256);
    
    String separator = " (";
    for (String variable : variables) {
      sb.append(separator);
      sb.append(variable);
      separator = ", ";
    }
    separator = ") values (:";
    for (String variable : variables) {
      sb.append(separator);
      sb.append(variable);
      separator = ", :";
    }
    sb.append(") ");
    
    return append(sb.toString());
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
        
        case VARIABLE:
          if (Character.isJavaIdentifierPart(c)) {
            variable.append(c);
          } else {
            variableList.add(variable.toString());
            variable = null;
            state = State.DEFAULT;
          }
          break;
        
        case LINE_COMMENT_START:
          if (c != '-') {
            state = State.DEFAULT;
          }
          break;
          
        case BLOCK_COMMENT_START:
          if (c != '*' || retainBlockComments) {
            state = State.DEFAULT;
          }
          break;
      }
      
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
              literalChar = c;
              state = State.LITERAL;
              break;
            case '-':
              state = State.LINE_COMMENT_START;
              break;
            case '/':
              state = State.BLOCK_COMMENT_START;
              break;
            case ':':
              c = '?';
              variable = new StringBuilder(32);
              state = State.VARIABLE;
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
          if (literalChar == c) {
            state = State.DEFAULT;
          }
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
          state = State.LINE_COMMENT;
          accept = false;
          removeLastChar = true;
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
          state = State.DEFAULT;
          state = State.BLOCK_COMMENT;
          accept = false;
          removeLastChar = true;
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
          
        case VARIABLE:
          accept = false;
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
}
