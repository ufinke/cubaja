// Copyright (c) 2015, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.util.ArrayList;
import java.util.Iterator;

public class IterableFilter<I, O> implements Iterable<O> {

  private Iterator<I> input;
  private SetFilter<I, O> filter;
  private Iterator<O> output;
 
  public IterableFilter(SetFilter<I, O> filter, Iterable<I> input) {

    this.filter = filter;
    this.input = input.iterator();
    output = new ArrayList<O>(0).iterator();
  }

  public Iterator<O> iterator() {

    return new Iterator<O>() {

      public boolean hasNext() {

        while ((! output.hasNext()) && input.hasNext()) {
          try {
            output = filter.filter(input.next()).iterator();
          } catch (Throwable t) {
            throw new IteratorException(t);
          }
        }
       
        return output.hasNext();
      }

      public O next() {

        return output.next();
      }

      public void remove() {

        throw new UnsupportedOperationException();
      }
    };
  }

}
