// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.concurrent.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.util.Text;
import java.util.*;

final class Info {

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
  
  public final int id() {
    
    return myId;
  }
  
  public final SortConfig getConfig() {
  
    return config;
  }

  public final void setConfig(SortConfig config) {
  
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
    
    int queueCapacity = (arraySize >> 1) + (arraySize >> 4) + 1;
    
    sortQueue = new ArrayBlockingQueue<Request>(queueCapacity);
  }
  
  @SuppressWarnings("rawtypes")
  public final Comparator getComparator() {
    
    return comparator;
  }
  
  @SuppressWarnings("rawtypes")
  public final void setComparator(Comparator comparator) {
    
    this.comparator = comparator;
  }
  
  public final boolean isTrace() {
    
    final Log logger = this.logger;
    return logger != null && logger.isTraceEnabled();
  }
  
  public final boolean isDebug() {
    
    final Log logger = this.logger;
    return logger != null && logger.isDebugEnabled();
  }
  
  public final void trace(String key, Object... parm) {
    
    if (isTrace()) {
      logger.trace(logPrefix + text.get(key, parm));
    }
  }
  
  public final void debug(String key, Object... parm) {

    if (isDebug()) {
      logger.debug(logPrefix + text.get(key, parm));
    }
  }
  
  public final void setError(Throwable error) {
    
    if (this.error == null) {
      if (logger != null) {
        logger.error(logPrefix + text.get("sorterException"), error);
      }
      this.error = error;
    }
  }
  
  public final boolean hasError() {
    
    return error != null;
  }
  
  public final Throwable getError() {
    
    return error;
  }
  
  public final BlockingQueue<Request> getSortQueue() {
    
    return sortQueue;
  }
}
