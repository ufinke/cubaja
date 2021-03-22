// Copyright (c) 2010 - 2013, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a DOM element.
 * @author Uwe Finke
 */
public class DOMElement extends DOMContent {

  private String name;
  private Map<String, String> attributes;
  private List<DOMContent> content;

  /**
   * Constructor.
   */
  public DOMElement() {

    super(DOMType.ELEMENT);

    attributes = new LinkedHashMap<String, String>();
    content = new ArrayList<DOMContent>();
  }
  
  /**
   * Constructor with the element's name.
   * @param name tag name
   */
  public DOMElement(String name) {
    
    this();
    setName(name);
  }

  /**
   * Returns the element's name.
   * @return name
   */
  public String getName() {

    return name;
  }

  /**
   * Sets the element's name.
   * @param name tag name
   */
  public void setName(String name) {

    this.name = name;
  }

  /**
   * Returns the element's attributes.
   * @return map with attribute names (keys) and values
   */
  public Map<String, String> getAttributes() {

    return attributes;
  }

  /**
   * Sets the element's attributes.
   * @param attributes attributes of element
   */
  public void setAttributes(Map<String, String> attributes) {
    
    this.attributes = attributes;
  }
  
  /**
   * Returns the element's subnodes.
   * @return child elements and text
   */
  public List<DOMContent> getContent() {

    return content;
  }
  
  /**
   * Adds a content.
   * @param content child node
   */
  public void addContent(DOMContent content) {
    
    this.content.add(content);
  }
  
  /**
   * Returns an attribute value.
   * May be <code>null</code> when there is no attribute with the given name.
   * @param name name of an attribute
   * @return value
   */
  public String getAttribute(String name) {
    
    return attributes.get(name);
  }
  
  /**
   * Returns an attribute value or a default if attribute doesn't exist.
   * @param name name of an attribute
   * @param defaultValue default value if attribute does not exist
   * @return value
   */
  public String getAttribute(String name, String defaultValue) {
    
    String result = getAttribute(name);
    return (result == null) ? defaultValue : result;
  }
  
  /**
   * Sets an attribute.
   * @param name name of the attribute
   * @param value value of the attribute
   */
  public void setAttribute(String name, String value) {
    
    attributes.put(name, value);
  }
  
  /**
   * Returns an XML representation of this element.
   */
  public String toString() {
    
    StringBuilder sb = new StringBuilder(256);
    
    sb.append('<');
    sb.append(name);
    
    for (Map.Entry<String, String> attr : attributes.entrySet()) {
      sb.append(' ');
      sb.append(attr.getKey());
      sb.append('=');
      sb.append('"');
      sb.append(attr.getValue());
      sb.append('"');
    }
    
    if (content.size() == 0) {
      sb.append('/');
    } else {
      sb.append('>');
      for (DOMContent sub : content) {
        sb.append(sub.toString());
      }
      sb.append("</");
      sb.append(name);
    }
    sb.append('>');
    
    return sb.toString();
  }
  
}
