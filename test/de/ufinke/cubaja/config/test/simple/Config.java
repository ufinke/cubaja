package de.ufinke.cubaja.config.test.simple;

import de.ufinke.cubaja.config.*;

public class Config {

  private String string;
  private SubElement subElement;
  
  public Config() {
    
  }
  
  public void setSubElement(SubElement subElement) {

    this.subElement = subElement;
  }
  
  public SubElement getSubElement() {
    
    return subElement;
  }
  
  @Mandatory
  public void setString(String value) {
    
    string = value;
  }
  
  public String getString() {
    
    return string;
  }
}
