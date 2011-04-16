// Copyright (c) 2011, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import org.xml.sax.InputSource;
import de.ufinke.cubaja.util.Text;
import java.util.*;

/**
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
