// Copyright (c) 2008, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import de.ufinke.cubaja.util.Text;

class IncludeDefinition {

  static private Text text = new Text(IncludeDefinition.class);
  
  private String name;
  private StringBuilder sb;
  private String string;
  private String publicId;
  
  IncludeDefinition(String name, Locator locator) {
  
    this.name = name;
    
    if (locator != null) {
      publicId = text.get("resourceName", name, locator.getPublicId(), Integer.valueOf(locator.getLineNumber()));
    } else {
      publicId = name;
    }
    
    sb = new StringBuilder(1000);
    sb.append("<?xml version=\"1.0\"?>\n<");
    sb.append(name);
    sb.append(">");
  }
  
  String getName() {
    
    return name;
  }
  
  String getPublicId() {
    
    return publicId;
  }
  
  void startElement(String elementName, Attributes atts) {
    
    sb.append("<");
    sb.append(elementName);
    for (int i = 0; i < atts.getLength(); i++) {
      sb.append(" ");
      sb.append(atts.getLocalName(i));
      sb.append("=\"");
      sb.append(atts.getValue(i).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quote;"));
      sb.append("\"");
    }
    sb.append(">");
  }
  
  void endElement(String elementName) {
    
    sb.append("</");
    sb.append(elementName);
    sb.append(">");
  }
  
  void addText(String s) {
    
    sb.append(s);
  }
  
  public String toString() {
    
    if (string == null) {
      sb.append("</");
      sb.append(name);
      sb.append(">");
      string = sb.toString();
      sb = null;
    }
    return string;
  }
}
