// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.*;
import java.util.concurrent.*;
import de.ufinke.cubaja.io.*;

public class Sorter<D> implements Iterable<D> {

  private boolean iteratorCreated;
  private SortAlgorithm algorithm;
  private MemoryManager memoryManager;  
  private ExecutorService workerService;
  private SortArray array;

  private List<HandlerDefinition> handlerList;
  private FileManager fileManager;
  private WriteTask writeTask;
  private SortTask sortTask;
  
  public Sorter(Comparator<? super D> comparator, SortConfig config) {
    
    algorithm = config.getAlgorithm();
    algorithm.setComparator(comparator);
    
    memoryManager = new MemoryManager(config);
    allocateArray();
    
    workerService = Executors.newCachedThreadPool();
    
    handlerList = new ArrayList<HandlerDefinition>();
    fileManager = new FileManager(config, handlerList);
    writeTask = new WriteTask(workerService, fileManager);
    sortTask = new SortTask(workerService, algorithm, writeTask);
  }
  
  public Sorter(Comparator<? super D> comparator) {
    
    this(comparator, new SortConfig());
  }
  
  public void addObjectHandler(Class<?> clazz, OutputObjectHandler outputHandler, InputObjectHandler inputHandler) {
    
    handlerList.add(new HandlerDefinition(clazz, outputHandler, inputHandler));
  }
  
  public void add(D element) {
  
    try {      
      doAdd(element);
    } catch (Throwable t) {
      throw new SortException(t);
    }
  }
  
  private void doAdd(D element) throws Exception {
    
    if (iteratorCreated) {
      throw new IllegalStateException();
    }

    if (array.isFull()) {
      sortTask.checkException();
      sortTask.runWorker(array);
      allocateArray();
    }
    
    array.add(element);
  }
  
  private void allocateArray() {
    
    Object[] newArray = new Object[memoryManager.getInputArrayCapacity()];
    array = new SortArray(newArray, 0);
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
    
    sortTask.checkException();
    array = sortTask.sort(array);
    fileManager.closeOutput();
  }
  
  boolean hasNext() throws Exception {
    
    workerService.shutdown(); //TODO shutdown when nothing more to do
    // TODO Auto-generated method stub
    return false;
  }
  
  D next() throws Exception {
    
    //TODO
    return null;
  }
}
