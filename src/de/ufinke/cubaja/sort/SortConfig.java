// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

/**
 * Sort configuration.
 * @author Uwe Finke
 */
public class SortConfig {

  private int runSize;
  private int blockSize;
  private SortAlgorithm algorithm;
  
  private boolean log;
  private int logInterval;
  
  private String workDirectory;
  private String filePrefix;

  /**
   * Default constructor.
   */
  public SortConfig() {

  }  

  /**
   * Returns the sort algorithm.
   * @return algorithm
   */
  public SortAlgorithm getAlgorithm() {

    if (algorithm == null) {
      algorithm = new Quicksort();
    }
    return algorithm;
  }

  /**
   * Sets the sort algorithm.
   * Default is <tt>Quicksort</tt>
   * @param algorithm
   */
  public void setAlgorithm(SortAlgorithm algorithm) {

    this.algorithm = algorithm;
  }

  /**
   * Returns the work directory for temporary files.
   * @return work directory name
   */
  public String getWorkDirectory() {

    if (workDirectory == null) {
      workDirectory = System.getProperty("java.io.tmpdir");
    }
    return workDirectory;
  }

  /**
   * Sets the work directory for temporary files.
   * Default is the directory supplied by system property <tt>java.io.tmpdir</tt>.
   * @param workDirectory
   */
  public void setWorkDirectory(String workDirectory) {

    this.workDirectory = workDirectory;
  }

  /**
   * Returns the prefix of temporary files.
   * @return file name prefix
   */
  public String getFilePrefix() {

    if (filePrefix == null) {
      filePrefix = "sort";
    }
    return filePrefix;
  }

  /**
   * Sets the file name prefix for temporary files.
   * Default is <tt>sort</tt>.
   * @param filePrefix
   */
  public void setFilePrefix(String filePrefix) {

    this.filePrefix = filePrefix;
  }

  /**
   * Returns the log property.
   * @return flag
   */
  public boolean isLog() {

    return log;
  }

  /**
   * Determines if sort activities should be logged.
   * Default is no logging.
   * If logging is set to <tt>true</tt>
   * debug and trace messages are written
   * to logger <tt>de.ufinke.cubaja.sort.Sorter</tt>.
   * @param log
   */
  public void setLog(boolean log) {

    this.log = log;
  }

  /**
   * Returns the delay between trace log messages in seconds.
   * @return log interval
   */
  public int getLogInterval() {

    if (logInterval == 0) {
      logInterval = 60;
    }
    return logInterval;
  }
  
  /**
   * Sets the delay between trace log messages in seconds.
   * Default is <tt>60</tt>.
   * If trace is enabled for the logger,
   * the number of processed objects is logged after 
   * every <tt>logInterval</tt> seconds.
   * @param logInterval
   */
  public void setLogInterval(int logInterval) {
  
    this.logInterval = logInterval;
  }

  /**
   * Returns the number of objects within a run.
   * @return run size
   */
  public int getRunSize() {
  
    return runSize;
  }
  
  /**
   * Sets the maximum number of objects in a run.
   * A run is a set of objects which is presorted
   * before it is written to temporary file.
   * When retrieving the sorted objects (during the get phase)
   * the presorted runs are merged.
   * The number of objects which is hold by the JVM's heap
   * is approximately two times the run size.
   * Default run size is 131072.
   * @param runSize
   */
  public void setRunSize(int runSize) {
  
    this.runSize = runSize;
  }
  
  /**
   * Returns the block size.
   * @return block size.
   */
  public int getBlockSize() {
  
    return blockSize;
  }

  /**
   * Sets the block size in bytes for temporary file.
   * Serialized objects are written to disk in a block of bytes.
   * The content of the internal buffer is written
   * when the buffer's size exceeds the block size.
   * Default block size is <tt>15360</tt> (15K).
   * As result, the effective block size is between 15K and 16K for normal sized data objects.
   * @param blockSize
   */
  public void setBlockSize(int blockSize) {
  
    this.blockSize = blockSize;
  }

}
