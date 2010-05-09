// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
 *     <td align="left" valign="top"><tt>name</tt></td>
 *     <td align="left" valign="top">file name</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top">x</td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>charset</tt></td>
 *     <td align="left" valign="top">character set name</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 * </table>
 * <tt>A/E</tt>: attribute or subelement
 * <br>
 * <tt>M</tt>: mandatory
 * <br>
 * <tt>U</tt>: unique
 * </blockquote>
 * @author Uwe Finke
 */
public class FileConfig {

  static private Text text = Text.getPackageInstance(FileConfig.class);
  
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
    
    if (charset == null) {
      this.charset = null;
      return;
    }
    
    try {      
      this.charset = Charset.forName(charset);
    } catch (UnsupportedCharsetException e) {
      throw new ConfigException(text.get("unsupportedCharset", charset));
    }
  }
  
  /**
   * Retrieves the <tt>Charset</tt> according to the charset attribute.
   * If no charset was specified, the default charset of the runtime 
   * environment is returned.
   * @return the charset
   */
  public Charset getCharset() {
    
    if (charset == null) {
      charset = Charset.defaultCharset();
    }
    return charset;
  }
  
  /**
   * Creates a file object.
   * @return a file
   */
  public File createFile() {
    
    return new File(name);
  }
  
  /**
   * Creates a file object representing a directory.
   * All directories in the path which do not already exist
   * will be allocated automatically.
   * @return a file
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
   * @return an output stream
   * @throws IOException
   */
  public BufferedOutputStream createOutputStream() throws IOException {
    
    return new BufferedOutputStream(new FileOutputStream(createOutputFile()));
  }
  
  /**
   * Creates a writer.
   * All parent directories which do not already exist
   * will be allocated automatically.
   * Uses the {@link #getCharset getCharset} method.
   * The writer and the underlaying stream are buffered.
   * @return a writer
   * @throws IOException
   */
  public BufferedWriter createWriter() throws IOException {
    
    return new BufferedWriter(new OutputStreamWriter(createOutputStream(), getCharset()));
  }
  
  /**
   * Creates an input stream.
   * @return an input stream
   * @throws IOException
   */
  public BufferedInputStream createInputStream() throws IOException {
    
    return new BufferedInputStream(new FileInputStream(createFile()));
  }
  
  /**
   * Creates a reader.
   * Uses the {@link #getCharset getCharset} method.
   * The reader and the underlaying stream are buffered.
   * @return a reader
   * @throws IOException
   */
  public BufferedReader createReader() throws IOException {
    
    return new BufferedReader(new InputStreamReader(createInputStream(), getCharset()));
  }
}
