// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.config.Mandatory;
import de.ufinke.cubaja.util.Text;

/**
 * Configuration element for files.
 * <p>
 * XML attributes and subelements:
 * <blockquote>
 * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
 *   <tr bgcolor="#ccccff">
 *     <th align="left">Name</th>
 *     <th align="left">Description</th>
 *     <th align="center">A/E</th>
 *     <th align="center">M</th>
 *     <th align="center">U</th>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>name</code></td>
 *     <td align="left" valign="top">file name</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top">x</td>
 *     <td align="center" valign="top">x</td>
 *     <td>x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><code>charset</code></td>
 *     <td align="left" valign="top">character set name</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 * </table>
 * <code>A/E</code>: attribute or subelement
 * <br/>
 * <code>M</code>: mandatory
 * <br/>
 * <code>U</code>: unique
 * </blockquote>
 * </p>
 * @author Uwe Finke
 */
public class FileConfig {

  static private Text text = new Text(FileConfig.class);
  
  private String name;
  private Charset charset;
  
  /**
   * Constructor.
   */
  public FileConfig() {
    
  }
  
  /**
   * Sets the file name attribute.
   * @param name file name
   */
  @Mandatory
  public void setName(String name) {
    
    this.name = name;
  }
  
  /**
   * Retrieves the file name attribute.
   * @return file name
   */
  public String getName() {
    
    return name;
  }
  
  /**
   * Sets the character set attribute.
   * @param charset charset name
   * @throws ConfigException if this charset is not supported
   */
  public void setCharset(String charset) throws ConfigException {
    
    try {      
      this.charset = Charset.forName(charset);
    } catch (UnsupportedCharsetException e) {
      throw new ConfigException(text.get("unsupportedCharset", charset));
    }
  }
  
  /**
   * Retrieves the <code>Charset</code> according to the charset attribute.
   * If no charset was specified, we get the default charset of the runtime 
   * environment.
   * @return the <code>Charset</code>
   */
  public Charset getCharset() {
    
    if (charset == null) {
      charset = Charset.defaultCharset();
    }
    return charset;
  }
  
  /**
   * Creates a file object.
   * @return a <code>File</code>
   */
  public File createFile() {
    
    return new File(name);
  }
  
  /**
   * Creates a file object representing a directory.
   * All directories in the path which do not already exist
   * will be allocated automatically.
   * @return a <code>File</code>
   */
  public File createDirectory() {
    
    File file = createFile();
    file.mkdirs();
    return file;
  }
  
  private File createOutputFile() {
    
    File file = createFile();
    
    File dir = file.getParentFile();
    if (dir != null) {
      dir.mkdirs();
    }
    
    return file;
  }
  
  /**
   * Creates an output stream.
   * All parent directories which do not already exist
   * will be allocated automatically.
   * @return an <code>OutputStream</code>
   * @throws IOException
   */
  public OutputStream createOutputStream() throws IOException {
    
    return new BufferedOutputStream(new FileOutputStream(createOutputFile()));
  }
  
  /**
   * Creates a writer.
   * All parent directories which do not already exist
   * will be allocated automatically.
   * Uses the <code>getCharset</code> method.
   * @return a <code>Writer</code>
   * @throws IOException
   */
  public Writer createWriter() throws IOException {
    
    return new BufferedWriter(new OutputStreamWriter(createOutputStream(), getCharset()));
  }
  
  /**
   * Creates an input stream.
   * @return an <code>InputStream</code>
   * @throws IOException
   */
  public InputStream createInputStream() throws IOException {
    
    return new BufferedInputStream(new FileInputStream(createFile()));
  }
  
  /**
   * Creates a reader.
   * Uses the <code>getCharset</code> method.
   * @return a <code>Reader</code>
   * @throws IOException
   */
  public Reader createReader() throws IOException {
    
    return new BufferedReader(new InputStreamReader(createInputStream(), getCharset()));
  }
}
