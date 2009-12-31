// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;
import java.util.Iterator;
import de.ufinke.cubaja.util.Text;

public class SequenceChecker<D> implements Iterable<D> {

  static Text text = new Text(SequenceChecker.class);
  
  static private class SequenceIterator<E> implements Iterator<E> {
    
    private Comparator<? super E> comparator;
    private Iterator<E> input;
    private E lastElement;
    private long elementNumber;
    private String name;
    
    public SequenceIterator(Comparator<? super E> comparator, Iterator<E> input, String name) {
      
      this.comparator = comparator;
      this.input = input;
      this.name = name;
    }

    public boolean hasNext() {

      return input.hasNext();
    }

    public E next() {

      E currentElement = input.next();
      
      elementNumber++;      
      if (elementNumber > 1) {
        if (comparator.compare(currentElement, lastElement) < 0) {
          throw new OutOfSequenceException(text.get("outOfSequence", elementNumber, name, currentElement));
        }
      }
      lastElement = currentElement;
      
      return currentElement;
    }

    public void remove() {

      throw new UnsupportedOperationException();
    }
  }
  
  static private int id;
  
  static private synchronized int getId() {
    
    return ++id;
  }
  
  private Iterator<D> iterator;
  
  public SequenceChecker(Comparator<? super D> comparator, Iterable<D> input) {
    
    this(comparator, input, "SequenceChecker_" + getId());
  }
  
  public SequenceChecker(Comparator<? super D> comparator, Iterable<D> input, String name) {
  
    iterator = new SequenceIterator<D>(comparator, input.iterator(), name);
  }
  
  public Iterator<D> iterator() {
    
    return iterator;
  }
}
