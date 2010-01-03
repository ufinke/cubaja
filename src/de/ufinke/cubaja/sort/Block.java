// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

class Block {

  private SortArray array;
  private long nextBlockPosition;
  private int nextBlockLength;

  public Block() {

  }
  
  public SortArray getArray() {

    return array;
  }

  public void setArray(SortArray array) {

    this.array = array;
  }

  public long getNextBlockPosition() {

    return nextBlockPosition;
  }

  public void setNextBlockPosition(long nextBlockPosition) {

    this.nextBlockPosition = nextBlockPosition;
  }

  public int getNextBlockLength() {

    return nextBlockLength;
  }

  public void setNextBlockLength(int nextBlockLength) {

    this.nextBlockLength = nextBlockLength;
  }
}
