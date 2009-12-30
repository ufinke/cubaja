// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.util.Text;

class Info {

  static final int BYTES_PER_BLOCK = 1024 * 256;
  static final int PROBE_SIZE = 1000;
  static final int MINIMUM_RUN_SIZE = 1000;
  
  static private Text text = new Text(Sorter.class);
  static private Log logger = LogFactory.getLog(Sorter.class);
  
  static private int id = 0;
  
  static private synchronized int getId() {
    
    return ++id;
  }

  private int myId;
  
  private SortConfig config;
  private int runSize;
  private int blockSize;
  
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
  }

  public void calculateSizes(int bufferSize) {

    int recordsPerRun = config.getRecordsPerRun();
    int recordsPerBlock = config.getRecordsPerBlock();
    
    long availableMemory = Runtime.getRuntime().maxMemory() / 4;
    long calculatedRunSize = availableMemory * PROBE_SIZE / bufferSize;
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
    
    if (config.isLog()) {
      logger.debug(text.get("calcSizes", id, text.get("calcConfig"), recordsPerRun, recordsPerBlock));
      logger.debug(text.get("calcSizes", id, text.get("calcRecommend"), calculatedRunSize, calculatedBlockSize));
      logger.debug(text.get("calcSizes", id, text.get("calcEffective"), runSize, blockSize));
    }
  }

  public int getRunSize() {
  
    return runSize;
  }
  
  public int getBlockSize() {
  
    return blockSize;
  }
}
