// Copyright (c) 2007 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Application main class template.
 * <p>
 * Usage: Implement a subclass, create an instance of it in the <tt>main</tt> method
 * and call {@link #start start}.
 * @author Uwe Finke
 */
public abstract class Executor {

  static private Log logger = LogFactory.getLog(Executor.class);
  static private Text text = new Text(Executor.class);
  
  /**
   * Exit code <tt>0</tt>.
   */
  static public final int EXIT_CODE_OK = 0;
  
  /**
   * Exit code <tt>4</tt>.
   */
  static public final int EXIT_CODE_WARN = 4;
  
  /**
   * Exit code <tt>8</tt>.
   */
  static public final int EXIT_CODE_ERROR = 8;
  
  /**
   * Exit code <tt>12</tt>.
   */
  static public final int EXIT_CODE_SEVERE = 12;
  
  /**
   * Exit code <tt>16</tt>.
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
   * May be called in the <tt>main</tt> method
   * when arguments have to be passed to the application.
   * @param args
   */
  protected void setArgs(String[] args) {

    this.args = args;
  }

  /**
   * Returns the stopwatch.
   * The stopwatch is created in the <tt>start</tt> method.
   * @return stopwatch
   */
  protected Stopwatch getStopwatch() {

    return stopwatch;
  }
  
  /**
   * Sets the exit code.
   * Default exit code is <tt>0</tt>.
   * @param exitCode
   */
  protected void setExitCode(int exitCode) {
    
    this.exitCode = exitCode;
  }

  /**
   * Starts the application.
   * <p>
   * This method should be called in the static <tt>main</tt> method.
   * <p>
   * This method writes a start message to the log, 
   * calls {@link #execute execute}, writes an end message containing
   * the elapsed time and calls <tt>System.exit</tt>
   * with the exit code.
   * <p>
   * Any Throwable is catched and logged before the application aborts.
   * In case of abort the exit code is set to <tt>16</tt> (fatal).
   */
  protected void start() {

    stopwatch = new Stopwatch(getClass().getName());
    
    try {
      execute();
    } catch (Throwable t) {
      logger.fatal(text.get("abort"), t);
      setExitCode(EXIT_CODE_FATAL);
    }
    
    stopwatch.elapsedMillis();
    
    System.exit(exitCode);
  }

  /**
   * Does the work.
   * This method has to be implemented in a subclass;
   * it is called by <tt>start</tt>.
   * @throws Exception
   */
  abstract protected void execute() throws Exception;
}
