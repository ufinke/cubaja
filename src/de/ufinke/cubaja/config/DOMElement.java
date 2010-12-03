// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DOMElement extends DOMContent {

  private String name;
  private Map<String, String> attributes;
  private List<DOMContent> content;

  public DOMElement() {

    super(DOMType.ELEMENT);

    attributes = new HashMap<String, String>();
    content = new ArrayList<DOMContent>();
  }

  public String getName() {

    return name;
  }

  public void setName(String name) {

    this.name = name;
  }

  public Map<String, String> getAttributes() {

    return attributes;
  }

  public void setAttributes(Map<String, String> attributes) {
    
    this.attributes = attributes;
  }
  
  public List<DOMContent> getContent() {

    return content;
  }
  
  public void addContent(DOMContent content) {
    
    this.content.add(content);
  }
  
  public String getAttribute(String name) {
    
    return attributes.get(name);
  }
  
  public String getAttribute(String name, String defaultValue) {
    
    String result = getAttribute(name);
    return (result == null) ? defaultValue : result;
  }
  
  public void setAttribute(String name, String value) {
    
    attributes.put(name, value);
  }
  
}
