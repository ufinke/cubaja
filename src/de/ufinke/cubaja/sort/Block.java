// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

class Block {

  private long startPosition;
  private int length;

  public Block() {

  }

  public long getStartPosition() {

    return startPosition;
  }

  public void setStartPosition(long startPosition) {

    this.startPosition = startPosition;
  }

  public int getLength() {

    return length;
  }

  public void setEndPosition(long endPosition) {

    length = (int) (endPosition - startPosition);
  }
}
