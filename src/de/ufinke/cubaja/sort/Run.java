// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Iterator;

class Run implements Iterator<Run>, Iterable<Run> {

  private long nextBlockPosition;
  private int nextBlockLength;
  private Object lastObject;
  private boolean firstBlock;

  public Run() {

    firstBlock = true;
  }

  public void setLastObject(Object lastObject) {

    this.lastObject = lastObject;
    firstBlock = false;
  }
  
  public Object getLastObject() {
    
    return lastObject;
  }
  
  public boolean isFirstBlock() {
    
    return firstBlock;
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

  public boolean hasNext() {

    return nextBlockLength > 0;
  }

  public Run next() {

    return this;
  }

  public void remove() {

    throw new UnsupportedOperationException();
  }
  
  public Iterator<Run> iterator() {
    
    return this;
  }
}
