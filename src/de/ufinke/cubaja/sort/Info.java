// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.concurrent.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.util.Text;
import java.util.*;

class Info {

  static private final int DEFAULT_RUN_SIZE = 1024 * 128;
  static private final int MINIMUM_RUN_SIZE = 1024;
  static private final int MAX_ARRAY_SIZE = 1024 * 16;
  static private final int DEFAULT_BLOCK_SIZE = 1024 * 32;
  static private final int MINIMUM_BLOCK_SIZE = 1024 * 8;
  
  static private Text text = new Text(Sorter.class);
  
  static private int id = 0;
  
  static private synchronized int getId() {
    
    return ++id;
  }

  private int myId;
  private String logPrefix;
  private Log logger;
  
  private SortConfig config;
  @SuppressWarnings("rawtypes")
  private Comparator comparator;
  
  private int runSize;
  private int arraySize;
  private int arrayCount;
  private int blockSize;
  
  private volatile Throwable error;
  
  private BlockingQueue<Request> sortQueue;
  
  public Info() {
    
    myId = getId();
  }
  
  public int id() {
    
    return myId;
  }
  
  public SortConfig getConfig() {
  
    return config;
  }

  public void setConfig(SortConfig config) {
  
    this.config = config;
    
    if (config.isLog()) {
      logger = LogFactory.getLog(Sorter.class);
      logPrefix = "Sort#" + myId + ": ";
    }
    
    blockSize = config.getBlockSize();
    if (blockSize == 0) {
      blockSize = DEFAULT_BLOCK_SIZE;
    }
    if (blockSize < MINIMUM_BLOCK_SIZE) {
      blockSize = MINIMUM_BLOCK_SIZE;
    }
    
    runSize = config.getRunSize();
    if (runSize == 0) {
      runSize = DEFAULT_RUN_SIZE;
    }
    if (runSize < MINIMUM_RUN_SIZE) {
      runSize = MINIMUM_RUN_SIZE;
    }
    
    arrayCount = 1;
    arraySize = runSize;
    while (arraySize > MAX_ARRAY_SIZE) {
      arraySize = arraySize >> 1;
      arrayCount = arrayCount << 1;
    }
    
    int queueCapacity = (arraySize >> 1) + (arraySize >> 2);
    if (queueCapacity == 0) {
      queueCapacity = 1;
    }
    
    sortQueue = new ArrayBlockingQueue<Request>(queueCapacity);
  }
  
  @SuppressWarnings("rawtypes")
  public Comparator getComparator() {
    
    return comparator;
  }
  
  @SuppressWarnings("rawtypes")
  public void setComparator(Comparator comparator) {
    
    this.comparator = comparator;
  }
  
  public boolean isTrace() {
    
    return logger != null && logger.isTraceEnabled();
  }
  
  public boolean isDebug() {
    
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
        logger.error(text.get("sorterException"), error);
      }
      this.error = error;
    }
  }
  
  public boolean hasError() {
    
    return error != null;
  }
  
  public Throwable getError() {
    
    return error;
  }
  
  public BlockingQueue<Request> getSortQueue() {
    
    return sortQueue;
  }
}
