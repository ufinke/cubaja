// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;
import java.util.Iterator;
import de.ufinke.cubaja.util.Text;

/**
 * Checks an <code>Iterable</code> for proper sequence.
 * Throws an <code>OutOfSequenceException</code> if any data object
 * is out of sequence.
 * @author Uwe Finke
 * @param <D> data type
 */
public class SequenceChecker<D> implements Iterable<D> {

  static Text text = Text.getPackageInstance(SequenceChecker.class);
  
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
  
  static private volatile int id;
  
  static private synchronized int getId() {
    
    return ++id;
  }
  
  private Iterator<D> iterator;
  
  /**
   * Constructor.
   * @param comparator comparator
   * @param input source
   */
  public SequenceChecker(Comparator<? super D> comparator, Iterable<D> input) {
    
    this(comparator, input, "SequenceChecker_" + getId());
  }
  
  /**
   * Constructor with a name which is used in error messages.
   * @param comparator comparator
   * @param input source
   * @param name individual name for this sequence checker
   */
  public SequenceChecker(Comparator<? super D> comparator, Iterable<D> input, String name) {
  
    iterator = new SequenceIterator<D>(comparator, input.iterator(), name);
  }
  
  /**
   * Iterates over the checked data objects.
   */
  public Iterator<D> iterator() {
    
    return iterator;
  }
}
