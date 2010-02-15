// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.etl.core;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

class ExtensionLoader extends URLClassLoader {

  ExtensionLoader() {
    
    super(new URL[0]);
  }
  
  public void addLib(String lib) throws Exception {
    
    if (! lib.endsWith(".jar")) {
      if (! lib.endsWith("/")) {
        lib = lib + "/";
      }
    }
    
    File file = new File(lib);
    URL url = new URL("file://" + file.getCanonicalPath());
    addURL(url);
  }
}
