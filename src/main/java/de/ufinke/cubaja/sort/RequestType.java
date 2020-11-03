// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

enum RequestType {

  CLOSE,
  END_OF_DATA,
  SWITCH_STATE,
  RESULT,
  SORT_ARRAY,
  BEGIN_RUN,
  WRITE_BLOCKS,
  END_RUN,
  INIT_RUN_MERGE,
  READ_BLOCK
}
