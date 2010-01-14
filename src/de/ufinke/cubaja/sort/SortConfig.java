// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

public class SortConfig {

  private int runSize;
  private int blockSize;
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

  public int getRunSize() {
  
    return runSize;
  }
  
  public void setRunSize(int runSize) {
  
    this.runSize = runSize;
  }
  
  public int getBlockSize() {
  
    return blockSize;
  }

  public void setBlockSize(int blockSize) {
  
    this.blockSize = blockSize;
  }

}
