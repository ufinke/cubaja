package de.ufinke.cubaja.config;

import org.xml.sax.*;

class PassedSAXException extends SAXException {

  private Throwable cause;
  
  public PassedSAXException(Throwable cause) {
  
    this.cause = cause;
  }
  
  public Throwable getCause() {
    
    return cause;
  }
}
