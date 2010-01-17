// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import de.ufinke.cubaja.util.IteratorException;

class Run implements Iterable<Object>, Iterator<Object> {

  private SortManager info;
  private IOManager ioManager;

  private long blockPosition;
  private int blockLength;

  private Object[] currentArray;
  private int currentSize;
  private int cursor;

  private Object[] nextArray;
  private int nextSize;
  private boolean hasNextBlock;

  private volatile CountDownLatch latch;

  public Run(SortManager info, IOManager ioManager, long blockPosition, int blockLength) throws Exception {

    this.info = info;
    this.ioManager = ioManager;
    this.blockPosition = blockPosition;
    this.blockLength = blockLength;

    currentArray = new Object[info.getBlockSize()];
    nextArray = new Object[info.getBlockSize()];

    requestNextBlock();
  }

  public boolean hasNext() {

    return cursor < currentSize || hasNextBlock;
  }

  public Object next() {

    if (cursor == currentSize) {
      try {
        switchBlock();
      } catch (Exception e) {
        throw new IteratorException(e);
      }
    }

    Object result = currentArray[cursor];
    currentArray[cursor++] = null;
    return result;
  }

  public void remove() {

    throw new UnsupportedOperationException();
  }

  public Iterator<Object> iterator() {

    return this;
  }

  private void switchBlock() throws Exception {

    latch.await();

    info.sync();
    
    Object[] temp = currentArray;
    currentArray = nextArray;
    nextArray = temp;

    currentSize = nextSize;
    cursor = 0;
    
    info.sync();
    
    requestNextBlock();
  }

  private void requestNextBlock() throws Exception {

    hasNextBlock = (blockLength > 0);
    
    if (hasNextBlock) {
      latch = new CountDownLatch(1);
      ioManager.requestNextBlock(this);
    }
  }
  
  public void releaseLatch() {
    
    latch.countDown();
  }

  public long getBlockPosition() {

    return blockPosition;
  }

  public void setBlockPosition(long blockPosition) {

    this.blockPosition = blockPosition;
  }

  public int getBlockLength() {

    return blockLength;
  }

  public void setBlockLength(int blockLength) {

    this.blockLength = blockLength;
  }

  public Object[] getNextArray() {

    return nextArray;
  }

  public void setNextSize(int nextSize) {

    this.nextSize = nextSize;
  }
}
