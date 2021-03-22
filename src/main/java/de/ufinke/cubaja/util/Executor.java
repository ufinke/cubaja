// Copyright (c) 2007 - 2013, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Application main class template.
 * <p>
 * Usage: Implement a subclass, create an instance of it in the <code>main</code> method
 * and call {@link #start start}!
 * @author Uwe Finke
 */
public abstract class Executor {

  static private Log logger = LogFactory.getLog(Executor.class);
  static private Text text = Text.getPackageInstance(Executor.class);
  
  /**
   * Exit code <code>0</code>.
   */
  static public final int EXIT_CODE_OK = 0;
  
  /**
   * Exit code <code>4</code>.
   */
  static public final int EXIT_CODE_WARN = 4;
  
  /**
   * Exit code <code>8</code>.
   */
  static public final int EXIT_CODE_ERROR = 8;
  
  /**
   * Exit code <code>12</code>.
   */
  static public final int EXIT_CODE_SEVERE = 12;
  
  /**
   * Exit code <code>16</code>.
   */
  static public final int EXIT_CODE_FATAL = 16;

  private Stopwatch stopwatch;
  private String[] args;
  private int exitCode;

  /**
   * Default constructor.
   */
  protected Executor() {

  }

  /**
   * Returns the args if set.
   * @return args
   */
  protected String[] getArgs() {

    return args;
  }

  /**
   * Sets args.
   * May be called in the <code>main</code> method
   * when arguments have to be passed to the application.
   * @param args arguments
   */
  protected void setArgs(String[] args) {

    this.args = args;
  }

  /**
   * Returns the stopwatch.
   * The stopwatch is created in the <code>start</code> method.
   * @return stopwatch
   */
  protected Stopwatch getStopwatch() {

    return stopwatch;
  }
  
  /**
   * Sets the exit code.
   * Default exit code is <code>0</code>.
   * @param exitCode exit code
   */
  protected void setExitCode(int exitCode) {
    
    this.exitCode = exitCode;
  }

  /**
   * Starts the application.
   * <p>
   * This method should be called in the static <code>main</code> method.
   * <p>
   * This method writes a start message to the log, 
   * calls {@link #execute execute}, writes an end message containing
   * the elapsed time and calls <code>System.exit</code>
   * with the exit code.
   * <p>
   * Any Throwable is catched and logged before the application aborts.
   * In case of abort the exit code is set to <code>16</code> (fatal).
   */
  protected void start() {

    logMessage(false);
    stopwatch = new Stopwatch();
    
    try {
      execute();
    } catch (Throwable t) {
      logger.fatal(text.get("abort"), t);
      setExitCode(EXIT_CODE_FATAL);
    }
    
    logMessage(true);
    System.exit(exitCode);
  }
  
  private void logMessage(boolean endMessage) {
    
    StringBuilder sb = new StringBuilder(64);
    sb.append(getClass().getName());
    sb.append(" - ");
    sb.append(text.get(endMessage ? "stopwatchEnd" : "stopwatchStart"));
    if (endMessage) {
      sb.append(' ');
      sb.append(Stopwatch.format(stopwatch.elapsedMillis()));
      sb.append(')');
    }
    
    logger.info(sb);
  }

  /**
   * Does the work.
   * This method has to be implemented in a subclass;
   * it is called by <code>start</code>.
   * @throws Exception any exception
   */
  abstract protected void execute() throws Exception;
}
