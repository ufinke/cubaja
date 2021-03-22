// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Merges an arbitrary number of sorted sources.
 * After the sources have been defined with a constructor,
 * the merged objects are retrieved by an iterator.
 * @author Uwe Finke
 * @param <D> data type
 */
public class Merger<D> implements Iterable<D> {

  static private final class Source<E> {
    
    private final Iterator<E> iterator;
    private E currentEntry;
    private boolean hasCurrent;
    
    public Source(Iterator<E> iterator) {
      
      this.iterator = iterator;
      advance();
    }
    
    public boolean hasObject() {
      
      return hasCurrent;
    }
    
    public E getObject() {
      
      return currentEntry;
    }
    
    public void advance() {
      
      hasCurrent = iterator.hasNext();
      if (hasCurrent) {
        currentEntry = iterator.next();
      }
    }
  }
  
  static private final class MergeIterator<F> implements Iterator<F> {

    private final Comparator<? super F> comparator;
    private final Source<F> leftSource;
    private final Source<F> rightSource;
    
    public MergeIterator(Comparator<? super F> comparator, Source<F> leftSource, Source<F> rightSource) {
      
      this.comparator = comparator;
      this.leftSource = leftSource;
      this.rightSource = rightSource;
    }
    
    public boolean hasNext() {

      return leftSource.hasObject() || rightSource.hasObject();
    }

    public F next() {

      if (leftSource.hasObject()) {
        final F left = leftSource.getObject();
        if (rightSource.hasObject()) {
          final F right = rightSource.getObject();
          if (comparator.compare(left, right) <= 0) {
            leftSource.advance();
            return left;
          } else {
            rightSource.advance();
            return right;
          }
        } else {
          leftSource.advance();
          return left;
        }
      } else if (rightSource.hasObject()) {
        final F result = rightSource.getObject();
        rightSource.advance();
        return result;
      } else {
        throw new NoSuchElementException();
      }
    }

    public void remove() {

      throw new UnsupportedOperationException();
    }    
  }

  private final Iterator<D> iterator;
  
  /**
   * Constructor with 2 sources.
   * @param comparator comparator
   * @param leftSource first source
   * @param rightSource second source
   */
  public Merger(Comparator<? super D> comparator, Iterable<D> leftSource, Iterable<D> rightSource) {

    Source<D> left = new Source<D>(leftSource.iterator());
    Source<D> right = new Source<D>(rightSource.iterator());
    iterator = new MergeIterator<D>(comparator, left, right);
  }
  
  /**
   * Constructor with a list of sources.
   * The sources are split into sub-lists
   * until the constructor with 2 sources can be used.
   * As a result, the objects are merged from a binary tree.
   * @param comparator comparator
   * @param sources list with sources to merge
   */
  public Merger(Comparator<? super D> comparator, List<Iterable<D>> sources) {
    
    switch (sources.size()) {
      case 0:
        List<D> emptyList = Collections.emptyList();
        iterator = emptyList.iterator();
        break;
      case 1:
        iterator = sources.get(0).iterator();
        break;
      case 2:
        Source<D> leftSourceA = new Source<D>(sources.get(0).iterator());
        Source<D> rightSourceA = new Source<D>(sources.get(1).iterator());
        iterator = new MergeIterator<D>(comparator, leftSourceA, rightSourceA);
        break;
      case 3:
        Source<D> leftSourceB = new Source<D>(sources.get(0).iterator());
        Merger<D> rightMergerB = new Merger<D>(comparator, sources.get(1), sources.get(2));
        Source<D> rightSourceB = new Source<D>(rightMergerB.iterator());
        iterator = new MergeIterator<D>(comparator, leftSourceB, rightSourceB);
        break;
      default:
        int half = sources.size() >> 1;
        List<Iterable<D>> leftListC = sources.subList(0, half);
        Merger<D> leftMergerC = new Merger<D>(comparator, leftListC);
        Source<D> leftSourceC = new Source<D>(leftMergerC.iterator());
        List<Iterable<D>> rightListC = sources.subList(half, sources.size());
        Merger<D> rightMergerC = new Merger<D>(comparator, rightListC);
        Source<D> rightSourceC = new Source<D>(rightMergerC.iterator());
        iterator = new MergeIterator<D>(comparator, leftSourceC, rightSourceC);
    }
  }
  
  /**
   * Retrieves one object after another in proper sequence.
   */
  public Iterator<D> iterator() {
    
    return iterator;
  }
}
