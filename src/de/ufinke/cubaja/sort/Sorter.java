// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;

public class Sorter<D> implements Iterable<D> {

  private boolean iteratorCreated;
  private SortAlgorithm<D> algorithm;
  private MemoryManager memoryManager;
  private ExecutorService executors;
  private SynchronousQueue<SyncInfo> sortTaskQueue;
  
  private Object[] array;
  private int arraySize;
  
  @SuppressWarnings("unchecked")
  public Sorter(Comparator<? super D> comparator, SortConfig config) {
    
    algorithm = (SortAlgorithm<D>) config.getAlgorithm();
    algorithm.setComparator(comparator);
    
    memoryManager = new MemoryManager(config);
    allocateArray();
    
    executors = Executors.newCachedThreadPool();
    
    sortTaskQueue = new SynchronousQueue<SyncInfo>();
    SynchronousQueue<SyncInfo> writeTaskQueue = new SynchronousQueue<SyncInfo>();
    executors.submit(new AlgorithmTask(sortTaskQueue, writeTaskQueue));
    executors.submit(new WriteTask(writeTaskQueue));
  }
  
  public Sorter(Comparator<? super D> comparator) {
    
    this(comparator, new SortConfig());
  }
  
  public void add(D element) throws Exception {
  
    if (iteratorCreated) {
      throw new IllegalStateException();
    }

    if (array.length == arraySize) {
      activateAlgorithm();
      allocateArray();
    }
    
    array[arraySize++] = element;
  }
  
  private void activateAlgorithm() throws Exception {
    
    InputInfo sortInfo = new InputInfo();
    sortInfo.setAlgorithm(algorithm);
    sortInfo.setArray(array);
    sortInfo.setArraySize(arraySize);
    sortTaskQueue.put(new SyncInfo(SyncAction.PROCESS_INPUT_ARRAY, sortInfo));
  }
  
  private void allocateArray() {
    
    array = new Object[memoryManager.getInputArrayCapacity()];
    arraySize = 0;
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
