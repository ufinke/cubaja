// Copyright (c) 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

public class DefaultExecFilter implements ExecFilter {

  public DefaultExecFilter() {
    
  }
  
  public String filterExecStatement(String input) {
    
    return input;
  }
}
