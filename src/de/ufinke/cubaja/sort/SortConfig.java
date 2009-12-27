// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

public class SortConfig {

  private int recordsPerRun;
  private int recordsPerBlock;
  private boolean compress;
  private boolean log;
  private boolean logDetails;
  private SortAlgorithm algorithm;
  private String workDirectory;
  private String filePrefix;

  public SortConfig() {

  }

  public SortAlgorithm getAlgorithm() {

    if (algorithm == null) {
      algorithm = new Quicksort();
    }
    return algorithm;
  }

  public void setAlgorithm(SortAlgorithm algorithm) {

    this.algorithm = algorithm;
  }

  public String getWorkDirectory() {

    return workDirectory;
  }

  public void setWorkDirectory(String workDirectory) {

    this.workDirectory = workDirectory;
  }

  public String getFilePrefix() {

    if (filePrefix == null) {
      filePrefix = "sort_";
    }
    return filePrefix;
  }

  public void setFilePrefix(String filePrefix) {

    this.filePrefix = filePrefix;
  }

  public int getRecordsPerRun() {

    return recordsPerRun;
  }

  public void setRecordsPerRun(int recordsPerRun) {

    if (recordsPerRun < 1000) {
      recordsPerRun = 1000;
    }
    if (recordsPerBlock > (recordsPerRun / 2)) {
      recordsPerBlock = recordsPerRun / 2;
    }
    this.recordsPerRun = recordsPerRun;
  }

  public int getRecordsPerBlock() {

    return recordsPerBlock;
  }

  public void setRecordsPerBlock(int recordsPerBlock) {

    if (recordsPerBlock < 500) {
      recordsPerBlock = 500;
    }
    this.recordsPerBlock = recordsPerBlock;
  }
  
  boolean isCalculated() {
    
    return (recordsPerRun > 0) && (recordsPerBlock > 0);
  }

  public boolean isCompress() {

    return compress;
  }

  public void setCompress(boolean compress) {

    this.compress = compress;
  }

  public boolean isLog() {

    return log;
  }

  public void setLog(boolean log) {

    this.log = log;
  }

  public boolean isLogDetails() {

    return logDetails;
  }

  public void setLogDetails(boolean logDetails) {

    this.logDetails = logDetails;
    log = log || logDetails;
  }
}
