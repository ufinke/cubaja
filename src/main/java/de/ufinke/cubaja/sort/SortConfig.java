// Copyright (c) 2009 - 2011, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

/**
 * <p>
 * Sort configuration.
 * </p>
 * <table class="striped">
 * <caption style="text-align:left">XML attributes and subelements</caption>
 * <thead>
 * <tr>
 * <th scope="col" style="text-align:left">Name</th>
 * <th scope="col" style="text-align:left">Description</th>
 * <th scope="col" style="text-align:center">A/E</th>
 * <th scope="col" style="text-align:center">M</th>
 * <th scope="col" style="text-align:center">U</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td style="text-align:left"><code>algorithm</code></td>
 * <td style="text-align:left">the name of a class which implements {@link SortAlgorithm}; default is <code>de.ufinke.cubaja.sort.Quicksort</code></td>
 * <td style="text-align:center">A</td>
 * <td style="text-align:center"> </td>
 * <td style="text-align:center">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left"><code>workDirectory</code></td>
 * <td style="text-align:left">directory for temporary files; default is the value of system property <code>java.io.tmpdir</code></td>
 * <td style="text-align:center">A</td>
 * <td style="text-align:center"> </td>
 * <td style="text-align:center">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left"><code>filePrefix</code></td>
 * <td style="text-align:left">name prefix of temporary files; default is <code>sort</code></td>
 * <td style="text-align:center">A</td>
 * <td style="text-align:center"> </td>
 * <td style="text-align:center">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left"><code>log</code></td>
 * <td style="text-align:left">if set to <code>true</code>, trace and debug messages are written to logger <code>de.ufinke.cubaja.sort.Sorter</code>; default is <code>false</code></td>
 * <td style="text-align:center">A</td>
 * <td style="text-align:center"> </td>
 * <td style="text-align:center">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left"><code>logInterval</code></td>
 * <td style="text-align:left">time interval between trace messages in seconds; default is <code>60</code></td>
 * <td style="text-align:center">A</td>
 * <td style="text-align:center"> </td>
 * <td style="text-align:center">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left"><code>runSize</code></td>
 * <td style="text-align:left">maximum number of objects in a run (see {@link #setRunSize setRunSize}); default is <code>131072</code></td>
 * <td style="text-align:center">A</td>
 * <td style="text-align:center"> </td>
 * <td style="text-align:center">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left"><code>blockSize</code></td>
 * <td style="text-align:left">number of bytes in a temporary file block (see {@link #setBlockSize setBlockSize}; default is <code>15360</code></td>
 * <td style="text-align:center">A</td>
 * <td style="text-align:center"> </td>
 * <td style="text-align:center">x</td>
 * </tr>
 * </tbody>
 * </table>
 * <p>
 * <code>A/E</code>: attribute or subelement<br>
 * <code>M</code>: mandatory<br>
 * <code>U</code>: unique
 * </p>
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
   * Creates and returns a new instance of the sort algorithm.
   * @return algorithm
   */
  public SortAlgorithm getAlgorithm() {

    // create a new instance on every call
    // because parallel running Sorter instances may use the same SortConfig
    
    if (algorithm == null) {
      return new Quicksort();
    }
    
    try {
      return algorithm.getClass().getConstructor().newInstance();
    } catch (Exception e) {
      throw new SorterException(e);
    }
  }

  /**
   * Sets the sort algorithm.
   * Default is <code>Quicksort</code>
   * @param algorithm sort algorithm implementation
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
   * Default is the directory supplied by system property <code>java.io.tmpdir</code>.
   * @param workDirectory directory for temporary file
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
   * Default is <code>sort</code>.
   * @param filePrefix file name prefix for temporary file
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
   * Determines whether sort activities shall be logged.
   * Default is no logging.
   * If logging is set to <code>true</code> then
   * debug and trace messages are written
   * to logger <code>de.ufinke.cubaja.sort.Sorter</code>.
   * @param log true or false
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
   * Default is <code>60</code>.
   * If trace is enabled for the logger,
   * the number of processed objects is logged after 
   * every <code>logInterval</code> seconds.
   * @param logInterval seconds between log messages
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
   * The number of objects which is hold by the JVM heap
   * is approximately two times the run size.
   * Default run size is 131072.
   * @param runSize maximum number of objects held in internal arrays
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
   * when the buffers size exceeds the block size.
   * Default block size is <code>15360</code> (15K).
   * As result, the effective block size is between 15K and 16K for normal sized data objects.
   * @param blockSize minimum number of bytes before serialized objects are written to the temporary file 
   */
  public void setBlockSize(int blockSize) {
  
    this.blockSize = blockSize;
  }

}
