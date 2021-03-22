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
 * <p>
 * Configuration element for files.
 * </p>
 * <table class="striped">
 * <caption style="text-align:left">XML attributes and subelements</caption>
 * <thead>
 * <tr>
 * <th scope="col" style="text-align:left">Name</th>
 * <th scope="col" style="text-align:left">Description</th>
 * <th scope="col" style="text-align:center">A/E</th>
 * <th scope="col" style="text-align:center">M</th>
 * <th scope="col" style="text-align:center">U</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td style="text-align:left"><code>name</code></td>
 * <td style="text-align:left">file name</td>
 * <td style="text-align:center">A</td>
 * <td style="text-align:center">x</td>
 * <td style="text-align:center">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left"><code>charset</code></td>
 * <td style="text-align:left">character set name</td>
 * <td style="text-align:center">A</td>
 * <td style="text-align:center"> </td>
 * <td style="text-align:center">x</td>
 * </tr>
 * </tbody>
 * </table>
 * <p>
 * <code>A/E</code>: attribute or subelement<br>
 * <code>M</code>: mandatory<br>
 * <code>U</code>: unique
 * </p>
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
   * Retrieves the <code>Charset</code> according to the charset attribute.
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
   * @throws IOException when stream could not be created
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
   * @throws IOException when writer could not be created
   */
  public BufferedWriter createWriter() throws IOException {
    
    return new BufferedWriter(new OutputStreamWriter(createOutputStream(), getCharset()));
  }
  
  /**
   * Creates an input stream.
   * @return an input stream
   * @throws IOException when stream could not be created
   */
  public BufferedInputStream createInputStream() throws IOException {
    
    return new BufferedInputStream(new FileInputStream(createFile()));
  }
  
  /**
   * Creates a reader.
   * Uses the {@link #getCharset getCharset} method.
   * The reader and the underlaying stream are buffered.
   * @return a reader
   * @throws IOException when reader could not be created
   */
  public BufferedReader createReader() throws IOException {
    
    return new BufferedReader(new InputStreamReader(createInputStream(), getCharset()));
  }
}
