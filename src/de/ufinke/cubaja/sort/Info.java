// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.util.Text;

class Info {

  static final int BYTES_PER_BLOCK = 1024 * 72;
  static final int PROBE_SIZE = 1000;
  static final int MINIMUM_RUN_SIZE = 1000;
  
  static private Text text = new Text(Sorter.class);
  
  static private int id = 0;
  
  static private synchronized int getId() {
    
    return ++id;
  }

  private int myId;
  private String logPrefix;
  private Log logger;
  
  private AtomicInteger sync;
  private SortConfig config;
  private int runSize;
  private int blockSize;
  private int maxMergeRuns;
  
  public Info() {
    
    myId = getId();
    sync = new AtomicInteger();
  }
  
  public int id() {
    
    return myId;
  }
  
  public void sync() {
    
    sync.incrementAndGet();
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
  
  public void calculateSizes(int bufferSize) {

    int recordsPerRun = config.getRecordsPerRun();
    int recordsPerBlock = config.getRecordsPerBlock();
    int runsPerMerge = config.getRunsPerMerge();
    
    long maxMemory = Runtime.getRuntime().maxMemory();
    
    long availableMemory = maxMemory / 6;
    long calculatedRunSize = Math.min(availableMemory * PROBE_SIZE / bufferSize, 250000);
    if (calculatedRunSize > Integer.MAX_VALUE) {
      calculatedRunSize = Integer.MAX_VALUE;
    }
    if (recordsPerRun > 0) {
      runSize = recordsPerRun;
    } else {
      runSize = (int) calculatedRunSize;
    }
    if (runSize < MINIMUM_RUN_SIZE) {
      runSize = MINIMUM_RUN_SIZE;
    }
    
    int calculatedBlockSize = BYTES_PER_BLOCK * PROBE_SIZE / bufferSize;
    if (recordsPerBlock > 0) {
      blockSize = recordsPerBlock;
    } else {
      blockSize = calculatedBlockSize;
    }
    if (blockSize < (MINIMUM_RUN_SIZE / 2)) {
      blockSize = MINIMUM_RUN_SIZE / 2;
    }
    if (blockSize > (runSize / 2)) {
      blockSize = runSize / 2;
    }
    
    long calculatedMaxMergeRuns = maxMemory / 3 * blockSize / PROBE_SIZE / bufferSize;
    if (calculatedMaxMergeRuns > Integer.MAX_VALUE) {
      calculatedMaxMergeRuns = Integer.MAX_VALUE;
    }
    if (runsPerMerge > 0) {
      maxMergeRuns = runsPerMerge;
    } else {
      maxMergeRuns = (int) calculatedMaxMergeRuns;
    }
    if (maxMergeRuns < 2) {
      maxMergeRuns = 2;
    }
    
    if (config.isLog()) {
      debug("calcSizes", text.get("calcConfig"), recordsPerRun, recordsPerBlock, runsPerMerge);
      debug("calcSizes", text.get("calcRecommend"), calculatedRunSize, calculatedBlockSize, calculatedMaxMergeRuns);
      debug("calcSizes", text.get("calcEffective"), runSize, blockSize, maxMergeRuns);
    }
  }

  public int getRunSize() {
  
    return runSize;
  }
  
  public int getBlockSize() {
  
    return blockSize;
  }
  
  public int getMaxMergeRuns() {
    
    return maxMergeRuns;
  }
}
