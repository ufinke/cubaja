// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.*;
import java.util.concurrent.*;

final class Run implements Iterable<Object>, Iterator<Object> {

  private final SortManager manager;

  private volatile long blockPosition;
  private volatile int blockLength;
  private volatile boolean hasNextBlock;
  private volatile SortArray nextArray;
  private volatile CountDownLatch latch;
  
  private Object[] array;
  private int size;
  private int position;

  public Run(SortManager manager, long blockPosition, int blockLength) throws Exception {

    this.manager = manager;
    this.blockPosition = blockPosition;
    this.blockLength = blockLength;
  }

  public boolean hasNext() {

    return position < size || hasNextBlock;
  }

  public Object next() {

    if (position == size) {
      try {
        switchBlock();
      } catch (Exception e) {
        throw new SorterException(e);
      }
    }

    return array[position++];
  }

  public void remove() {

    throw new UnsupportedOperationException();
  }

  public Iterator<Object> iterator() {

    return this;
  }

  private void switchBlock() throws Exception {

    boolean ready = false;
    while (! ready) {
      ready = latch.await(1, TimeUnit.SECONDS) || manager.hasError();
    }

    array = nextArray.getArray();
    size = nextArray.getSize();
    position = 0;
    nextArray = null;
    
    requestNextBlock();
  }

  public void requestNextBlock() throws Exception {

    hasNextBlock = (blockLength > 0);
    
    if (! hasNextBlock) {
      return;
    }
    
    latch = new CountDownLatch(1);
    
    final BlockingQueue<Request> queue = manager.getFileQueue();
    final Request request = new Request(RequestType.READ_BLOCK, this);
    boolean written = false;
    while (! written) {
      written = queue.offer(request, 1, TimeUnit.SECONDS) || manager.hasError();
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

  public void setNextArray(SortArray nextArray) {
    
    this.nextArray = nextArray;
  }
}
