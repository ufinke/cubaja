// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.*;
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
  
  public void add(D element) {
  
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
  
  private void activateSortTask() {
    
    Info<D> info = new Info<D>(Action.PROCESS_INPUT);
    info.setArray(array);
    activateTask(sortTaskQueue, info);
  }
  
  private void activateTask(BlockingQueue<Info<D>> queue, Info<D> info) {
    
    try {      
      queue.put(info);
    } catch (Throwable t) {
      throw new SortException(t);
    }
  }
  
  @SuppressWarnings("unchecked")
  private void allocateArray(D element) {
    
    D[] newArray = (D[]) Array.newInstance(element.getClass(), memoryManager.getInputArrayCapacity());
    array = new SortArray<D>(newArray, 0);
  }

  public Iterator<D> iterator() {

    try {      
      switchState();
    } catch (Throwable t) {      
      throw new SortException(t);
    }
    
    return new SortIterator<D>(this);
  }
  
  private void switchState() throws Exception {
    
    if (iteratorCreated) {
      throw new IllegalStateException();
    }
    iteratorCreated = true;
    
    if (array != null) {
      activateSortTask();
      array = null;
    }
  }
  
  boolean hasNext() throws Exception {
    
    executors.shutdown(); //TODO shutdown when nothing more to do
    // TODO Auto-generated method stub
    return false;
  }
  
  D next() throws Exception {
    
    //TODO
    return null;
  }
}
