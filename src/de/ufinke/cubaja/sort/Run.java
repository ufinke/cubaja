// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Iterator;
import de.ufinke.cubaja.util.IteratorException;

class Run implements Iterator<Object>, Iterable<Object>  {

  private Info info;
  private Block block;
  private Object[] array;
  private int size;
  private int position;
  
  public Run(Info info, Block block) {
    
    this.info = info;
    this.block = block;
    
    initArray();
  }
  
  private void initArray() {
    
    SortArray sortArray = block.getArray();
    array = sortArray.getArray();
    size = sortArray.getSize();
    position = 0;
  }

  public boolean hasNext() {
    
    return position < size || block.getNextBlockLength() > 0;
  }

  public Object next() {

    if (position >= size) {
      try {
        block = info.getBlockQueue().take();
      } catch (Exception e) {
        throw new IteratorException(e);
      }
      initArray();
    }
    
    Object result = array[position];
    array[position++] = null;
    return result;
  }

  public void remove() {

    throw new UnsupportedOperationException();
  }
  
  public Iterator<Object> iterator() {
    
    return this;
  }
}
