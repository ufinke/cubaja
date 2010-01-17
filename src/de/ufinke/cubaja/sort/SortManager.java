// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.Comparator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.util.Stopwatch;
import de.ufinke.cubaja.util.Text;

final class SortManager {

  static private final int DEFAULT_RUN_SIZE = 1024 * 128;
  static private final int MINIMUM_RUN_SIZE = 1024;
  static private final int MAX_ARRAY_SIZE = 1024 * 16;
  // ObjectOutputStream drains after buffer of 1K is filled;
  // resulting block to disk is 31K <= block <= 32K
  static private final int DEFAULT_BLOCK_SIZE = 1024 * 31;
  static private final int MINIMUM_BLOCK_SIZE = 1024 * 7;

  static private final Text text = new Text(Sorter.class);

  static private int id = 0;

  static private synchronized int getId() {

    return ++id;
  }

  private final int myId;
  private final String logPrefix;
  private final Log logger;
  private final Stopwatch stopwatch;

  private final SortConfig config;
  @SuppressWarnings("rawtypes")
  private final Comparator comparator;
  private final SortAlgorithm algorithm;

  private final int runSize;
  private final int arraySize;
  private final int arrayCount;
  private final int blockSize;

  private final ExecutorService executor;
  private final BlockingQueue<Request> sortQueue;
  private final BlockingQueue<Request> fileQueue;
  private final BlockingQueue<Request> mainQueue;

  private volatile Throwable error;

  @SuppressWarnings("rawtypes")
  public SortManager(SortConfig config, Comparator comparator) {

    myId = getId();

    this.config = config;
    this.comparator = comparator;

    if (config.isLog()) {
      logger = LogFactory.getLog(Sorter.class);
      logPrefix = "Sort#" + myId + ": ";
      stopwatch = new Stopwatch();
    } else {
      logger = null;
      logPrefix = null;
      stopwatch = null;
    }

    algorithm = config.getAlgorithm();
    algorithm.setComparator(comparator);

    int blockSize = config.getBlockSize();
    if (blockSize == 0) {
      blockSize = DEFAULT_BLOCK_SIZE;
    }
    if (blockSize < MINIMUM_BLOCK_SIZE) {
      blockSize = MINIMUM_BLOCK_SIZE;
    }
    this.blockSize = blockSize;

    int runSize = config.getRunSize();
    if (runSize == 0) {
      runSize = DEFAULT_RUN_SIZE;
    }
    if (runSize < MINIMUM_RUN_SIZE) {
      runSize = MINIMUM_RUN_SIZE;
    }
    this.runSize = runSize;

    int arrayCount = 1;
    int arraySize = runSize;
    while (arraySize > MAX_ARRAY_SIZE) {
      arraySize = arraySize >> 1;
      arrayCount = arrayCount << 1;
    }
    this.arrayCount = arrayCount;
    this.arraySize = arraySize;
    
    int queueCapacity = (arrayCount >> 1) + (arrayCount >> 4) + 1;
    sortQueue = new ArrayBlockingQueue<Request>(queueCapacity);
    fileQueue = new ArrayBlockingQueue<Request>(queueCapacity);
    mainQueue = new ArrayBlockingQueue<Request>(queueCapacity);
    
    executor = Executors.newCachedThreadPool();
  }

  public int id() {

    return myId;
  }

  public SortConfig getConfig() {

    return config;
  }

  @SuppressWarnings("rawtypes")
  public Comparator getComparator() {

    return comparator;
  }

  public SortAlgorithm getAlgorithm() {

    return algorithm;
  }

  public boolean isTrace() {

    final Log logger = this.logger;
    return logger != null && logger.isTraceEnabled();
  }

  public boolean isDebug() {

    final Log logger = this.logger;
    return logger != null && logger.isDebugEnabled();
  }

  public void trace(String key, Object... parm) {

    if (isTrace()) {
      logger.trace(logPrefix + text.get(key, parm));
    }
  }

  public void debug(String key, Object... parm) {

    if (isDebug()) {
      logger.debug(logPrefix + text.get(key, parm));
    }
  }

  public void setError(Throwable error) {

    if (this.error == null) {
      if (logger != null) {
        logger.error(logPrefix + text.get("sorterException"), error);
      }
      this.error = error;
    }
  }
  
  public boolean hasError() {

    return error != null;
  }
  
  public void checkError() {
    
    if (hasError()) {
      throw new SorterException(error);
    }
  }

  public int getRunSize() {

    return runSize;
  }

  public int getArraySize() {

    return arraySize;
  }

  public int getArrayCount() {

    return arrayCount;
  }

  public int getBlockSize() {

    return blockSize;
  }
  
  public BlockingQueue<Request> getSortQueue() {

    return sortQueue;
  }

  public BlockingQueue<Request> getFileQueue() {

    return fileQueue;
  }
  
  public BlockingQueue<Request> getMainQueue() {
    
    return mainQueue;
  }

  public Stopwatch getStopwatch() {
    
    return stopwatch;
  }
  
  public void submit(Runnable task) {
    
    executor.submit(task);
  }
  
  public void close() {
    
    executor.shutdownNow();
  }
}
