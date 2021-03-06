// Copyright (c) 2006 - 2013, Uwe Finke. All rights reserved.
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.util.Text;

/**
 * Collector and interpreter of SQL text.
 * The various methods keep track 
 * of line breaks, separators, comments and variables.
 * The result is formatted executable SQL.
 * <p>
 * By default, this class splits consecutive statements
 * (separated by semicolon) into a list of statements.
 * Text between line comments <code>--{beginStatement}</code>
 * and <code>--{endStatement}</code> are treated as a single SQL statement.
 * These comments are necessary to process 
 * stored procedure definitions or proprietary SQL scripts.
 * @author Uwe Finke
 */
public class Sql {

  static private final Text text = Text.getPackageInstance(Sql.class);
  
  private StringBuilder appendBuffer;
  
  private boolean formatted;
  private int inPos;
  private int inLen;
  private StringBuilder inBuffer;
  private StringBuilder outBuffer;
  
  private String string;
  private List<String> statementList;
  private List<String> variableList;
  private boolean splitStatements;
  
  private Map<String, String> resolveMap;
  
  /**
   * Default constructor.
   */
  public Sql() {

    appendBuffer = new StringBuilder(1024);
    resolveMap = new LinkedHashMap<String, String>();
    splitStatements = true;
  }
  
  /**
   * Constructor with initial SQL text.
   * @param sql initial SQL text
   */
  public Sql(String sql) {
    
    this();
    append(sql);
  }
  
  /**
   * Constructor which reads initial SQL text from a reader.
   * @param reader reader
   * @throws IOException I/O problem
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
   * The file names extension must be <code>sql</code> (lower case).
   * The <code>sqlResource</code> parameter contains only the
   * plain file name without extension and without path.
   * @param packageClass class in the same package as the resource file
   * @param sqlResource name of resource file within the package
   * @throws IOException I/O problem
   */
  public Sql(Class<?> packageClass, String sqlResource) throws IOException {
    
    this();
    append(packageClass, sqlResource);
  }
  
  private void format() {
    
    if (formatted) {
      return;
    }

    statementList = new ArrayList<String>();
    variableList = new ArrayList<String>();
    variableList.add("*dummy*"); // position starts with 1
    string = null;
    
    if (resolveMap.size() == 0) {
      inBuffer = appendBuffer;
    } else {
      inBuffer = new StringBuilder(appendBuffer);
      resolve();
    }
    
    inPos = 0;
    inLen = inBuffer.length();
    outBuffer = new StringBuilder(inLen);
    
    while (inPos < inLen) {
      char c = inBuffer.charAt(inPos);
      if (Character.isWhitespace(c)) {
        c = ' ';
      }
      formatChar(c);
    }
    
    storeStatement();

    inBuffer = null;
    outBuffer = null;
    
    formatted = true;
  }
  
  private void formatChar(char c) {
    
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

      case ' ':
        formatSpace();
        break;

      case ',':
      case ')':
        formatNoSpacePrefix(c);
        break;
        
      default:
        outBuffer.append(c);
        inPos++;
    }
  }
  
  private void formatSpace() {
    
    if (outBuffer.length() > 0) {
      char p = outBuffer.charAt(outBuffer.length() - 1);
      switch (p) {
        case ' ':
        case '(':
          break;
        default:
          outBuffer.append(' ');
      }
    }
    inPos++;
  }
  
  private void formatNoSpacePrefix(char c) {
    
    if (outBuffer.length() > 0) {
      int pos = outBuffer.length() - 1;
      if (outBuffer.charAt(pos) == ' ') {
        outBuffer.setLength(pos);
      }
    }
    outBuffer.append(c);
    inPos++;
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

    nextPos++;
    int instructionStart = (nextPos < inLen && inBuffer.charAt(nextPos) == '{') ? nextPos + 1 : 0;
    int instructionEnd = 0;
    
    boolean end = false;
    while (! end) {
      char c = inBuffer.charAt(nextPos);
      if (c == '}') {
        instructionEnd = nextPos;
      }
      nextPos++;
      end = (c == '\n') || (c == '\r') || (nextPos >= inLen);
    }
    
    if (instructionEnd > instructionStart) {
      formatInstruction(inBuffer.substring(instructionStart, instructionEnd).toLowerCase());
    }
    
    inPos = nextPos;
  }
  
  private void formatInstruction(String instruction) {
    
    if (instruction.equals("endstatement")) {
      storeStatement();
      splitStatements = true;
    } else if (instruction.equals("beginstatement")) {
      splitStatements = false;
    }
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
        end = (nextPos + 1 >= inLen || inBuffer.charAt(++nextPos) == '/');
      }
      nextPos++;
      end |= (nextPos >= inLen);
    }
    inPos = nextPos;
  }
  
  private void formatVariable() {
    
    inPos++;
    int nextPos = inPos;
    
    while (nextPos < inLen && Character.isJavaIdentifierPart(inBuffer.charAt(nextPos))) {
      nextPos++;
    }
    
    if (inPos != nextPos) {
      variableList.add(inBuffer.substring(inPos, nextPos));
      outBuffer.append('?');
    } else {
      outBuffer.append(':');
    }
    
    inPos = nextPos;
  }
  
  private void formatEndStatement() {
    
    inPos++;
    
    if (splitStatements) {
      storeStatement();
    } else {
      outBuffer.append(';');
    }
  }
  
  private void storeStatement() {

    while (outBuffer.length() > 0 && outBuffer.charAt(outBuffer.length() - 1) == ' ') {
      outBuffer.setLength(outBuffer.length() - 1);
    }
    
    if (outBuffer.length() > 0) {
      statementList.add(outBuffer.toString());    
      outBuffer = new StringBuilder(Math.max(128, inBuffer.length() - outBuffer.length()));
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
  
  protected List<String> getStatements() {

    format();
    return statementList;
  }
  
  protected String getSingleStatement() throws SQLException {
    
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
   * @param line another line of the SQL statement
   * @return this
   */
  public Sql append(String line) {
    
    if (line == null) {
      return this;
    }

    formatted = false;
    
    appendBuffer.append(line);
    appendBuffer.append('\n');
    
    return this;
  }
  
  /**
   * Appends lines from a reader.
   * @param reader reader
   * @return this
   * @throws IOException I/O problem
   */
  public Sql append(Reader reader) throws IOException {

    formatted = false;
    
    char[] array = new char[1024];
    
    int length = reader.read(array);
    while (length > 0) {
      appendBuffer.append(array, 0, length);
      length = reader.read(array);
    }
    
    return this;
  }
  
  /**
   * Appends lines from a resource.
   * <p>
   * The SQL must be written in a separate file within a java source package.
   * The <code>packageClass</code> parameter specifies
   * a class which is located in the same package as the SQL resource. 
   * This may be any class, but usually it will be the class which uses
   * the SQL (then the parameter simply is '<code>getClass()</code>').
   * <p>
   * The file names extension must be <code>sql</code> (lower case).
   * The <code>sqlResource</code> parameter contains the file name without extension
   * (the extension is appended by this method automatically).
   * There is no prefix or path required when the resource resides in the same package
   * as the <code>packageClass</code>. The resource is loaded following the rules
   * defined by {@link java.lang.Class#getResourceAsStream(String) getResourceAsStream}.
   * <p>
   * To be platform independent on runtime, the resource must be coded in <code>UTF-8</code>.
   * Don't forget to customize your development editor.
   * @param packageClass class in the same package as the resource file
   * @param sqlResource name of resource file within the package
   * @return this
   * @throws IOException I/O problem
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
    
    Reader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(stream), "UTF-8"));
    append(reader);
    reader.close();
    
    return this;
  }
  
  /**
   * Appends a list of values separated by comma.
   * If an object type is not a subtype of <code>Number</code>,
   * the value will be enclosed by apostrophs.
   * <p>
   * Useful for <code>IN</code> predicates.
   * The parantheses are not generated automatically.
   * @param value array with values
   * @return this
   */
  public Sql appendList(Object[] value) {

    if (value == null) {
      return this;
    }
    
    formatted = false;
    
    for (int i = 0; i < value.length; i++) {
      if (i > 0) {
        appendBuffer.append(", ");
      }
      if (value[i] instanceof Number) {
        appendBuffer.append(value[i].toString());
      } else {
        appendBuffer.append('\'');
        appendBuffer.append(value[i].toString());
        appendBuffer.append('\'');
      }
    }
    
    return this;
  }
  
  /**
   * Appends a list of values separated by comma.
   * If an object type is not a subtype of <code>Number</code>,
   * the value will be enclosed by apostrophs.
   * <p>
   * Useful for <code>IN</code> predicates.
   * The parantheses are not generated automatically.
   * @param value collection with values
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
   * Useful for <code>IN</code> predicates.
   * The parantheses are not generated automatically.
   * @param value array with values
   * @return this
   */
  public Sql appendList(int[] value) {
    
    if (value == null) {
      return this;
    }
    
    formatted = false;
    
    for (int i = 0; i < value.length; i++) {
      if (i > 0) {
        appendBuffer.append(", ");
      }
      appendBuffer.append(value[i]);
    }

    return this;
  }
  
  /**
   * Generates SQL code for <code>update</code> statements.
   * The variables in the list are expanded to 
   * <code>set <i>var1</i> = :<i>var1</i>, <i>var2</i> = :<i>var2</i> ...</code>.
   * @param variables field names
   * @return this
   */
  public Sql appendUpdate(String... variables) {
    
    formatted = false;
    
    String separator = " set ";
    for (String variable : variables) {
      appendBuffer.append(separator);
      appendBuffer.append(variable);
      appendBuffer.append(" = :");
      appendBuffer.append(variable);
      separator = ", ";
    }
    appendBuffer.append(' ');
    
    return this;
  }
  
  /**
   * Generates SQL code for <code>insert</code> statements.
   * The variables in the list are expanded to 
   * <code>(<i>var1</i>, <i>var2</i>, ...) values (:<i>var1</i>, :<i>var2</i> ...)</code>.
   * @param variables field names
   * @return this
   */
  public Sql appendInsert(String... variables) {

    formatted = false;
    
    String separator = " (";
    for (String variable : variables) {
      appendBuffer.append(separator);
      appendBuffer.append(variable);
      separator = ", ";
    }
    separator = ") values (:";
    for (String variable : variables) {
      appendBuffer.append(separator);
      appendBuffer.append(variable);
      separator = ", :";
    }
    appendBuffer.append(") ");
    
    return this;
  }
  
  /**
   * Sets a property to a value.
   * Properties like '<code>${<i>name</i>}</code> are replaced by the value
   * before the formatted statement is retrieved.
   * @param name name of property
   * @param value value which replaces the property
   * @return this
   */
  public Sql resolve(String name, String value) {

    if (name == null || value == null) {
      return this;
    }
    
    formatted = false;

    StringBuilder sb = new StringBuilder(name.length() + 3);
    sb.append('$');
    sb.append('{');
    sb.append(name);
    sb.append('}');
    
    resolveMap.put(sb.toString(), value);
    
    return this;
  }
  
  /**
   * Sets a property to a generated collection value.
   * The array elements are formatted as in {@link #appendList(Object[]) appendList},
   * then {@link #resolve(String, String) resolve} is called.
   * @param name name of property
   * @param value array with values which replace the properties as a comma separated list
   * @return this
   */
  public Sql resolve(String name, Object[] value) {
    
    if (name == null || value == null) {
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
   * @param name name of property
   * @param value collection of values which replace the properties as a comma separated list
   * @return this
   */
  public Sql resolve(String name, Collection<Object> value) {
    
    return resolve(name, value.toArray());
  }
  
  /**
   * Sets a property to a generated collection value.
   * The collection elements are formatted as in {@link #appendList(int[]) appendList},
   * then {@link #resolve(String, String) resolve} is called.
   * @param name name of property
   * @param value array of values which replace the properties as a comma separated list
   * @return this
   */
  public Sql resolve(String name, int[] value) {
    
    if (name == null || value == null) {
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

    for (Map.Entry<String, String> entry : resolveMap.entrySet()) {
      String key = entry.getKey();
      int index = inBuffer.indexOf(key);
      while (index >= 0) {
        int end = index + key.length();
        inBuffer.replace(index, end, entry.getValue());
        index = inBuffer.indexOf(key, index);
      }
    }
  }
}