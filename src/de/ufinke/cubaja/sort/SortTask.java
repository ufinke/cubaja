// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.concurrent.*;
import java.util.*;

final class SortTask implements Runnable {

  private final SortManager manager;
  private final SortAlgorithm algorithm;
  
  private boolean loop;
  private boolean fileTaskStarted;
  
  private List<SortArray> arrayList;
  
  public SortTask(SortManager manager) {
    
    this.manager = manager;
    algorithm = manager.getAlgorithm();
    arrayList = new ArrayList<SortArray>(manager.getArrayCount());
  }
  
  public void run() {
    
    try {
      work();
    } catch (Throwable t) {
      manager.setError(t);
    }
  }
  
  private void work() throws Exception {

    final BlockingQueue<Request> queue = manager.getSortQueue();
    loop = true;
    
    while (loop) {
      final Request request = queue.poll(1, TimeUnit.SECONDS);
      if (manager.hasError()) {
        loop = false;
      } else if (request != null) {
        handleRequest(request);
      }
    }
  }
  
  private void handleRequest(final Request request) throws Exception {
    
    switch (request.getType()) {
      
      case SORT_ARRAY:
        sortArray((SortArray) request.getData());
        break;
        
      case SWITCH_STATE:
        if (fileTaskStarted) {
          mergeFromFile();
        } else {
          mergeFromArrayList();
        }
        break;
    }
  }
    
  private void mergeFromFile() throws Exception {
    
    drainToFileTask();
  }
  
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void mergeFromArrayList() throws Exception {
    
    final Merger merger = new Merger(manager.getComparator(), arrayList);
    final BlockingQueue<Request> queue = manager.getResultQueue();
    
    mergeToQueue(merger, queue, RequestType.RESULT);
    writeQueue(queue, new Request(RequestType.END_OF_DATA));
  }
  
  private void sortArray(final SortArray sortArray) throws Exception {

    algorithm.sort(sortArray.getArray(), sortArray.getSize());

    arrayList.add(sortArray);
    
    if (arrayList.size() == manager.getArrayCount()) {
      drainToFileTask();
    }
  }
  
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void drainToFileTask() throws Exception {

    if (! fileTaskStarted) {
      manager.submit(new FileTask(manager));
    }    
    
    final Merger merger = new Merger(manager.getComparator(), arrayList);
    final BlockingQueue<Request> queue = manager.getFileQueue();
    
    mergeToQueue(merger, queue, RequestType.WRITE_BLOCKS);
    writeQueue(queue, new Request(RequestType.END_RUN));
  }
  
  @SuppressWarnings("rawtypes")
  private void mergeToQueue(final Merger merger, final BlockingQueue<Request> queue, final RequestType type) throws Exception {

    final int queueSize = manager.getQueueSize();    
    final Iterator iterator = merger.iterator();
    
    Object[] array = new Object[queueSize];
    int size = 0;
    
    while (iterator.hasNext() && loop) {
      
      if (size == array.length) {
        writeQueue(queue, new Request(type, new SortArray(array, size)));
        array = new Object[queueSize];
        size = 0;
      }
      
      array[size++] = iterator.next();
    }
    
    arrayList.clear();
  }
  
  private void writeQueue(final BlockingQueue<Request> queue, final Request request) throws Exception {
    
    boolean written = false;
    while ((! written) && loop) {
      written = queue.offer(request, 1, TimeUnit.SECONDS);
      if (manager.hasError()) {
        loop = false;
      }
    }
  }
}
