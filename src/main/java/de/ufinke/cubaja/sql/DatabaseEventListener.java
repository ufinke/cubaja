// Copyright (c) 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

public interface DatabaseEventListener {

  public void handleDatabaseEvent(DatabaseEvent event) throws Exception;
}
