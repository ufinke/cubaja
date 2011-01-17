// Copyright (c) 2009 - 2011, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

/**
 * Sort configuration.
 * <p>
 * XML attributes and subelements:
 * <blockquote>
 * <table border="0" cellspacing="3" cellpadding="2" summary="Attributes and subelements.">
 *   <tr bgcolor="#ccccff">
 *     <th align="left">Name</th>
 *     <th align="left">Description</th>
 *     <th align="center">A/E</th>
 *     <th align="center">M</th>
 *     <th align="center">U</th>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>algorithm</tt></td>
 *     <td align="left" valign="top">the name of a class which implements {@link SortAlgorithm}; default is <tt>de.ufinke.cubaja.sort.Quicksort</tt></td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>workDirectory</tt></td>
 *     <td align="left" valign="top">directory for temporary files; default is the value of system property <tt>java.io.tmpdir</tt></td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>filePrefix</tt></td>
 *     <td align="left" valign="top">name prefix of temporary files; default is <tt>sort</tt></td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>log</tt></td>
 *     <td align="left" valign="top">if set to <tt>true</tt>, trace and debug messages are written to logger <tt>de.ufinke.cubaja.sort.Sorter</tt>; default is <tt>false</tt></td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>logInterval</tt></td>
 *     <td align="left" valign="top">time interval between trace messages in seconds; default is <tt>60</tt></td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>runSize</tt></td>
 *     <td align="left" valign="top">maximum number of objects in a run (see {@link #setRunSize setRunSize}); default is <tt>131072</tt></td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>blockSize</tt></td>
 *     <td align="left" valign="top">number of bytes in a temporary file block (see {@link #setBlockSize setBlockSize}; default is <tt>15360</tt></td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top"> </td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 * </table>
 * <tt>A/E</tt>: attribute or subelement
 * <br>
 * <tt>M</tt>: mandatory
 * <br>
 * <tt>U</tt>: unique
 * </blockquote>
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
      return algorithm.getClass().newInstance();
    } catch (Exception e) {
      throw new SorterException(e);
    }
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
   * Determines whether sort activities shall be logged.
   * Default is no logging.
   * If logging is set to <tt>true</tt> then
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
   * The number of objects which is hold by the JVM heap
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
   * when the buffers size exceeds the block size.
   * Default block size is <tt>15360</tt> (15K).
   * As result, the effective block size is between 15K and 16K for normal sized data objects.
   * @param blockSize
   */
  public void setBlockSize(int blockSize) {
  
    this.blockSize = blockSize;
  }

}
