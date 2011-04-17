// Copyright (c) 2011, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.LinkedList;
import java.util.List;
import org.xml.sax.InputSource;
import de.ufinke.cubaja.util.Text;

/**
 * A <tt>ResourceLoader</tt> calling other <tt>ResourceLoader</tt>s.
 * Any number of <tt>ResourceLoader</tt> instances may be added.
 * On a call to <tt>loadResource</tt> the call is delegated
 * to the previously added loaders in the sequence they where added
 * until a loader returns an <tt>InputSource</tt>.
 * @author Uwe Finke
 */
public class MultiResourceLoader implements ResourceLoader {

  static private Text text = Text.getPackageInstance(MultiResourceLoader.class);
  
  private List<ResourceLoader> loaderList;
  
  /**
   * Default constructor.
   */
  public MultiResourceLoader() {
  
    loaderList = new LinkedList<ResourceLoader>();
  }
  
  /**
   * Adds a loader to the internal list.
   * @param loader
   */
  public void addResourceLoader(ResourceLoader loader) {
    
    loaderList.add(loader);
  }
  
  public InputSource loadResource(String resourceName) throws ConfigException {

    for (ResourceLoader loader : loaderList) {
      try {
        InputSource result = loader.loadResource(resourceName);
        if (result != null) {
          return result;
        }
      } catch (Exception e) {
      }
    }
    
    throw new ConfigException(text.get("resourceNotFound", resourceName));        
  }

}
