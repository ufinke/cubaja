// Copyright (c) 2006 - 2010, Uwe Finke. All rights reserved.
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

/**
 * Collector and interpreter of SQL text.
 * The various methods keep track 
 * of line breaks, separators, comments and variables.
 * The result is formatted executable SQL.
 * @author Uwe Finke
 */
public class Sql {

  static private final Text text = new Text(Sql.class);
  
  static class ResolveData {
    String name;
    String value;
  }

  private StringBuilder inBuffer;
  
  private boolean formatted;
  private int inPos;
  private int inLen;
  private StringBuilder outBuffer;
  private boolean writeSpace;
  
  private String string;
  private List<String> statementList;
  private List<String> variableList;
  
  private List<ResolveData> resolveList;
  
  /**
   * Default constructor.
   */
  public Sql() {

    inBuffer = new StringBuilder(1024);
    resolveList = new ArrayList<ResolveData>();
  }
  
  /**
   * Constructor with initial SQL text.
   * @param sql
   */
  public Sql(String sql) {
    
    this();
    append(sql);
  }
  
  /**
   * Constructor which reads initial SQL text from a reader.
   * @param reader
   * @throws IOException
   */
  public Sql(Reader reader) throws IOException {
    
    this();
    append(reader);
  }
  
  /**
   * Constructor which reads initial SQL text from a resource.
   * <p>
   * The SQL must be written in a separate file within a java source package
   * (usually the package where the class which uses the SQL belongs to).
   * We have to specify a class within that package as parameter. 
   * This may be any class, but usually it will be the class which uses
   * the SQL.
   * The file name's extension must be <tt>sql</tt> (lower case).
   * The <tt>sqlResource</tt> parameter contains only the
   * plain file name without extension and without path.
   * @param packageClass
   * @param sqlResource
   * @throws IOException
   */
  public Sql(Class<?> packageClass, String sqlResource) throws IOException {
    
    this();
    append(packageClass, sqlResource);
  }
    
  private void format() {
    
    if (formatted) {
      return;
    }

    resolve();
    
    statementList = new ArrayList<String>();
    variableList = new ArrayList<String>();
    variableList.add("*dummy*"); // position starts with 1
    string = null;
    
    writeSpace = false;
    inPos = 0;
    inLen = inBuffer.length();
    outBuffer = new StringBuilder(inLen);
    
    while (inPos < inLen) {
      char c = inBuffer.charAt(inPos);
      if (Character.isWhitespace(c)) {
        formatWhitespace();
      } else {
        formatNonWhitespace(c);
      }
    }
    
    formatEndStatement();
    
    formatted = true;
  }
  
  private void formatWhitespace() {
    
    if (writeSpace) {
      outBuffer.append(' ');
      writeSpace = false;
    }
    
    inPos++;
  }
  
  private void formatNonWhitespace(char c) {
    
    writeSpace = true;
    
    switch (c) {
      
      case '\'':
      case '\"':
        formatLiteral(c);
        break;
        
      case '-':
        formatPossibleLineComment();
        break;
        
      case '/':
        formatPossibleBlockComment();
        break;
        
      case ':':
        formatVariable();
        break;
        
      case ';':
        formatEndStatement();
        break;
        
      default:
        outBuffer.append(c);
        inPos++;
    }
  }
  
  private void formatLiteral(char delimiter) {

    int end = inPos + 1;
    while (end < inLen && inBuffer.charAt(end) != delimiter) {
      end++;
    }
    if (end < inLen) {
      end++;
    }
    outBuffer.append(inBuffer.subSequence(inPos, end));
    inPos = end;
  }
  
  private void formatPossibleLineComment() {
    
    int nextPos = inPos + 1;
    
    if (nextPos >= inLen || inBuffer.charAt(nextPos) != '-') {
      outBuffer.append('-');
      inPos = nextPos;
      return;
    }
    
    boolean end = false;
    while (! end) {
      char c = inBuffer.charAt(nextPos++);
      end = (c == '\n') || (c == '\r') || (nextPos >= inLen);
    }
    inPos = nextPos;
  }
  
  private void formatPossibleBlockComment() {
    
    int nextPos = inPos + 1;
    
    if (nextPos >= inLen || inBuffer.charAt(nextPos) != '*') {
      outBuffer.append('/');
      inPos = nextPos;
      return;
    }
    
    nextPos++;
    if (nextPos < inLen && inBuffer.charAt(nextPos) == '+') { // Oracle optimizer hint: /*+xxx */
      outBuffer.append('/');
      outBuffer.append('*');
      inPos = nextPos;
      return;
    }
    
    boolean end = (nextPos >= inLen);
    while (! end) {
      if (inBuffer.charAt(nextPos) == '*') {
        nextPos++;
        end = (nextPos >= inLen || inBuffer.charAt(nextPos) == '/'); 
      } else {
        nextPos++;
        end = (nextPos >= inLen);
      }
    }
    inPos = nextPos;
  }
  
  private void formatVariable() {
    
    inPos++;
    int nextPos = inPos;
    while (nextPos < inLen && Character.isJavaIdentifierPart(inBuffer.charAt(nextPos))) {
      nextPos++;
    }
    variableList.add(inBuffer.substring(inPos, nextPos));
    
    outBuffer.append('?');
    
    inPos = nextPos;
  }
  
  private void formatEndStatement() {
    
    writeSpace = false;
    inPos++;
    
    if (outBuffer.length() > 0) {
      statementList.add(outBuffer.toString());    
      outBuffer.setLength(0);
    }    
  }
  
  /**
   * Returns the formatted SQL.
   */
  public String toString() {

    format();
    
    if (string == null) {
      switch (statementList.size()) {
        case 0:
          string = "";
          break;
        case 1:
          string = statementList.get(0);
          break;
        default:
          int len = statementList.size() * 2;
          for (int i = 0; i < statementList.size(); i++) {
            len += statementList.get(i).length();
          }
          StringBuilder sb = new StringBuilder(len);
          sb.append(statementList.get(0));
          for (int i = 1; i < statementList.size(); i++) {
            sb.append("; ");
            sb.append(statementList.get(i));
          }
          string = sb.toString();
      }
    }
    
    return string;
  }
  
  List<String> getStatements() {

    format();
    return statementList;
  }
  
  String getSingleStatement() throws SQLException {
    
    format();
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

    format();
    return variableList.size() > 1;
  }
  
  List<String> getVariables() {

    format();
    return variableList;
  }
  
  /**
   * Appends a line.
   * @param line
   * @return this
   */
  public Sql append(String line) {
    
    if (line == null) {
      return this;
    }

    formatted = false;
    
    inBuffer.append(line);
    inBuffer.append('\n');
    
    return this;
  }
  
  /**
   * Appends lines from a reader.
   * @param reader
   * @return this
   * @throws IOException
   */
  public Sql append(Reader reader) throws IOException {

    formatted = false;
    
    char[] array = new char[1024];
    
    int length = reader.read(array);
    while (length > 0) {
      inBuffer.append(array, 0, length);
      length = reader.read(array);
    }
    
    return this;
  }
  
  /**
   * Appends lines from a resource.
   * <p>
   * The SQL must be written in a separate file within a java source package.
   * The <tt>packageClass</tt> parameter specifies
   * a class which is located in the same package as the SQL resource. 
   * This may be any class, but usually it will be the class which uses
   * the SQL (then the parameter simply is '<tt>getClass()</tt>').
   * <p>
   * The file name's extension must be <tt>sql</tt> (lower case).
   * The <tt>sqlResource</tt> parameter contains the file name without extension
   * (the extension is appended by this method automatically).
   * There is no prefix or path required when the resource resides in the same package
   * as the <tt>packageClass</tt>. The resource is loaded following the rules
   * defined by {@link java.lang.Class#getResourceAsStream(String) getResourceAsStream}.
   * <p>
   * To be platform independent on runtime, the resource must be coded in <tt>UTF-8</tt>.
   * Don't forget to customize your development editor.
   * @param packageClass
   * @param sqlResource
   * @return this
   * @throws IOException
   */
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
  
  /**
   * Appends a list of values separated by comma.
   * If an object type is not a subtype of <tt>Number</tt>,
   * the value will be enclosed by apostrophs.
   * <p>
   * Useful for <tt>IN</tt> predicates.
   * The parantheses are not generated automatically.
   * @param value
   * @return this
   */
  public Sql appendList(Object[] value) {

    if (value == null) {
      return this;
    }
    
    formatted = false;
    
    for (int i = 0; i < value.length; i++) {
      if (i > 0) {
        inBuffer.append(", ");
      }
      if (value[i] instanceof Number) {
        inBuffer.append(value[i].toString());
      } else {
        inBuffer.append('\'');
        inBuffer.append(value[i].toString());
        inBuffer.append('\'');
      }
    }
    
    return this;
  }
  
  /**
   * Appends a list of values separated by comma.
   * If an object type is not a subtype of <tt>Number</tt>,
   * the value will be enclosed by apostrophs.
   * <p>
   * Useful for <tt>IN</tt> predicates.
   * The parantheses are not generated automatically.
   * @param value
   * @return this
   */
  public Sql appendList(Collection<Object> value) {

    if (value == null) {
      return this;
    }
    
    return appendList(value.toArray());
  }
  
  /**
   * Appends a list of integer values separated by comma.
   * <p>
   * Useful for <tt>IN</tt> predicates.
   * The parantheses are not generated automatically.
   * @param value
   * @return this
   */
  public Sql appendList(int[] value) {
    
    if (value == null) {
      return this;
    }
    
    formatted = false;
    
    for (int i = 0; i < value.length; i++) {
      if (i > 0) {
        inBuffer.append(", ");
      }
      inBuffer.append(value[i]);
    }

    return this;
  }
  
  /**
   * Generates SQL code for <tt>update</tt> statements.
   * The variables in the list are expanded to 
   * <tt>set <i>var1</i> = :<i>var1</i>, <i>var2</i> = :<i>var2</i> ...</tt>.
   * @param variables
   * @return this
   */
  public Sql appendUpdate(String... variables) throws SQLException {
    
    formatted = false;
    
    String separator = " set ";
    for (String variable : variables) {
      inBuffer.append(separator);
      inBuffer.append(variable);
      inBuffer.append(" = :");
      inBuffer.append(variable);
      separator = ", ";
    }
    inBuffer.append(' ');
    
    return this;
  }
  
  /**
   * Generates SQL code for <tt>insert</tt> statements.
   * The variables in the list are expanded to 
   * <tt>(<i>var1</i>, <i>var2</i>, ...) values (:<i>var1</i>, :<i>var2</i> ...)</tt>.
   * @param variables
   * @return this
   */
  public Sql appendInsert(String... variables) throws SQLException {

    formatted = false;
    
    String separator = " (";
    for (String variable : variables) {
      inBuffer.append(separator);
      inBuffer.append(variable);
      separator = ", ";
    }
    separator = ") values (:";
    for (String variable : variables) {
      inBuffer.append(separator);
      inBuffer.append(variable);
      separator = ", :";
    }
    inBuffer.append(") ");
    
    return this;
  }
  
  /**
   * Sets a property to a value.
   * Properties like '<tt>${<i>name</i>}</tt> are replaced by the value
   * before the formatted statement is retrieved.
   * @param name
   * @param value
   * @return this
   */
  public Sql resolve(String name, String value) {

    formatted = false;

    StringBuilder sb = new StringBuilder(name.length() + 3);
    sb.append('$');
    sb.append('{');
    sb.append(name);
    sb.append('}');
    
    ResolveData data = new ResolveData();
    data.name = sb.toString();
    data.value = value;
    
    resolveList.add(data);
    
    return this;
  }
  
  /**
   * Sets a property to a generated collection value.
   * The array elements are formatted as in {@link #appendList(Object[]) appendList},
   * then {@link #resolve(String, String) resolve} is called.
   * @param name
   * @param value
   * @return this
   */
  public Sql resolve(String name, Object[] value) {
    
    if (value == null) {
      return this;
    }
    
    StringBuilder sb = new StringBuilder(250);
    
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
    
    return resolve(name, sb.toString());
  }
  
  /**
   * Sets a property to a generated collection value.
   * The collection elements are formatted as in {@link #appendList(Collection) appendList},
   * then {@link #resolve(String, String) resolve} is called.
   * @param name
   * @param value
   * @return this
   */
  public Sql resolve(String name, Collection<Object> value) {
    
    return resolve(name, value.toArray());
  }
  
  /**
   * Sets a property to a generated collection value.
   * The collection elements are formatted as in {@link #appendList(int[]) appendList},
   * then {@link #resolve(String, String) resolve} is called.
   * @param name
   * @param value
   * @return this
   */
  public Sql resolve(String name, int[] value) {
    
    if (value == null) {
      return this;
    }
    
    StringBuilder sb = new StringBuilder(100);
    
    for (int i = 0; i < value.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(value[i]);
    }

    return resolve(name, sb.toString());
  }
  
  private void resolve() {

    for (ResolveData rd : resolveList) {
      int index = inBuffer.indexOf(rd.name);
      while (index >= 0) {
        int end = index + rd.name.length();
        inBuffer.replace(index, end, rd.value);
        index = inBuffer.indexOf(rd.name, index);
      }
    }
  }
}
