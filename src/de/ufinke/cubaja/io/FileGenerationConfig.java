// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.io.*;
import de.ufinke.cubaja.config.*;
import java.util.*;

/**
 * File generation configuration (<i>under construction</i>).
 * @author Uwe Finke
 */
public class FileGenerationConfig implements EndElementHandler {

  private File directory;
  private String name;
  private String extension;
  private String charset;

  private Integer generations;
  private Integer days;
  private Date minDate;
  
  private Map<Integer, FileConfig> map;

  public FileGenerationConfig() {

  }

  @Mandatory
  public void setDirectory(String directory) {

    this.directory = new File(directory);
  }

  @Mandatory
  public void setName(String name) {

    this.name = name;
  }

  public void setExtension(String extension) {

    this.extension = extension;
  }

  public void setCharset(String charset) {

    this.charset = charset;
  }

  public void setGenerations(Integer generations) {

    this.generations = generations;
  }

  public void setDays(Integer days) {

    this.days = days;
  }

  public void setMinDate(Date minDate) {

    this.minDate = minDate;
  }
  
  public void endElement() throws ConfigException {

    map = new HashMap<Integer, FileConfig>();
  }
  
  public FileConfig getGeneration(int generation) {

    FileConfig result = map.get(generation);
    
    if (result == null) {
      result = createGeneration(generation);
      map.put(generation, result);
    }
    
    return result;
  }
  
  private FileConfig createGeneration(int generation) {
    
    //TODO
    return null;
  }

}
