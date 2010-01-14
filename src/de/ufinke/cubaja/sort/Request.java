// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

class Request {

  private RequestType type;
  private Object data;
  
  Request(RequestType type, Object data) {
    
    this.type = type;
  }
  
  public RequestType getType() {
    
    return type;
  }

  public Object getData() {
    
    return data;
  }
}
