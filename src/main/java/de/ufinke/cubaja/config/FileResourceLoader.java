// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import org.xml.sax.InputSource;
import de.ufinke.cubaja.util.Text;
import de.ufinke.cubaja.util.Util;

/**
 * File reading <code>ResourceLoader</code>.
 * <p>
 * If a resource name is not an absolute file name,
 * this loader will read a file relative to a
 * given home directory.
 * @author Uwe Finke
 */
public class FileResourceLoader implements ResourceLoader {

  static private Text text = Text.getPackageInstance(FileResourceLoader.class);
  
  private File homeDirectory;
  
  /**
   * Default constructor.
   * The home directory is the current working directory.
   */
  public FileResourceLoader() {
  
    this(System.getProperty("user.dir"));
  }
  
  /**
   * Constructor with home directory name.
   * @param homeDirectory directory name
   */
  public FileResourceLoader(String homeDirectory) {
    
    this(new File(homeDirectory));
  }
  
  /**
   * Constructor with home directory file.
   * @param homeDirectory directory file
   */
  public FileResourceLoader(File homeDirectory) {
    
    this.homeDirectory = homeDirectory;
  }

  public InputSource loadResource(String resourceName) throws ConfigException {

    File file = new File(resourceName);
    
    if (! file.isAbsolute()) {
      file = new File(homeDirectory, resourceName);
    }
    
    try {
      return new InputSource(new BufferedInputStream(new FileInputStream(file)));
    } catch (Exception e) {
      throw new ConfigException(text.get("resourceNotFound", Util.getPath(file)));        
    }
  }

}
