// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

final class Request {

  private final RequestType type;
  private final Object data;
  
  public Request(RequestType type, Object data) {
    
    this.type = type;
    this.data = data;
  }
  
  public Request(RequestType type) {
    
    this.type = type;
    this.data = null;
  }
  
  public RequestType getType() {
    
    return type;
  }

  public Object getData() {
    
    return data;
  }
}
