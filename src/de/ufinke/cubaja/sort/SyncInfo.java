// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

class SyncInfo {

  private SyncAction action;
  private Object info;
  
  public SyncInfo(SyncAction action, Object info) {

    this.action = action;
    this.info = info;
  }

  public SyncAction getAction() {

    return action;
  }

  public Object getInfo() {
    
    return info;
  }
}
