// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

public class Sorter<D> implements Iterable<D> {

  private boolean iteratorCreated;
  private MemoryManager memoryManager;
  private ExecutorService executors;
  private SynchronousQueue<Info<D>> sortTaskQueue;
  
  private SortArray<D> array;
  
  @SuppressWarnings("unchecked")
  public Sorter(Comparator<? super D> comparator, SortConfig config) {
    
    SortAlgorithm<D> algorithm = (SortAlgorithm<D>) config.getAlgorithm();
    algorithm.setComparator(comparator);
    
    memoryManager = new MemoryManager(config);
    
    executors = Executors.newCachedThreadPool();
    
    sortTaskQueue = new SynchronousQueue<Info<D>>();
    SynchronousQueue<Info<D>> writeTaskQueue = new SynchronousQueue<Info<D>>();
    executors.submit(new SortTask(algorithm, sortTaskQueue, writeTaskQueue));
    executors.submit(new StreamTask(writeTaskQueue));
  }
  
  public Sorter(Comparator<? super D> comparator) {
    
    this(comparator, new SortConfig());
  }
  
  public void add(D element) throws Exception {
  
    if (iteratorCreated) {
      throw new IllegalStateException();
    }

    if (array == null) {
      allocateArray(element);
    } else if (array.isFull()) {
      activateSortTask();
      allocateArray(element);
    }
    
    array.add(element);
  }
  
  private void activateSortTask() throws Exception {
    
    Info<D> info = new Info<D>(Action.PROCESS_INPUT);
    info.setArray(array);
    sortTaskQueue.put(info);
  }
  
  @SuppressWarnings("unchecked")
  private void allocateArray(D element) {
    
    D[] newArray = (D[]) Array.newInstance(element.getClass(), memoryManager.getInputArrayCapacity());
    array = new SortArray<D>(newArray);
  }

  public Iterator<D> iterator() {

    if (iteratorCreated) {
      throw new IllegalStateException();
    }
    iteratorCreated = true;
    
    //TODO sort remaining items

    array = null;
    
    return new Iterator<D>() {

      public boolean hasNext() {

        executors.shutdown(); //TODO shutdown when nothing more to do
        // TODO Auto-generated method stub
        return false;
      }

      public D next() {

        // TODO Auto-generated method stub
        return null;
      }

      public void remove() {

        throw new UnsupportedOperationException();
      }
    };
  }
}
