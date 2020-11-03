// Copyright (c) 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

public enum DatabaseEventType {

  REGISTER,
  CLOSE,
  COMMIT,
  ROLLBACK,
  EXECUTE,
  PREPARE_QUERY,
  PREPARE_UPDATE
}
