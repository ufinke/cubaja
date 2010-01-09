// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

public class SortConfig {

  private int recordsPerRun;
  private int recordsPerBlock;
  private int runsPerMerge;
  
  private boolean compress;
  private SortAlgorithm algorithm;
  
  private boolean log;
  private int logInterval;
  
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

    if (workDirectory == null) {
      workDirectory = System.getProperty("java.io.tmpdir");
    }
    return workDirectory;
  }

  public void setWorkDirectory(String workDirectory) {

    this.workDirectory = workDirectory;
  }

  public String getFilePrefix() {

    if (filePrefix == null) {
      filePrefix = "sort";
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

    this.recordsPerRun = recordsPerRun;
  }

  public int getRecordsPerBlock() {

    return recordsPerBlock;
  }

  public void setRecordsPerBlock(int recordsPerBlock) {

    this.recordsPerBlock = recordsPerBlock;
  }
  
  public int getRunsPerMerge() {
  
    return runsPerMerge;
  }
  
  public void setRunsPerMerge(int runsPerMerge) {
  
    this.runsPerMerge = runsPerMerge;
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

  public int getLogInterval() {

    if (logInterval == 0) {
      logInterval = 60;
    }
    return logInterval;
  }
  
  public void setLogInterval(int logInterval) {
  
    this.logInterval = logInterval;
  }

}
