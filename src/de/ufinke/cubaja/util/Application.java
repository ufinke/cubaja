// Copyright (c) 2007 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Application main class template.
 * <p>
 * Usage: Implement a subclass, create an instance of it in the <code>main</code> method
 * and call <code>start()</code>.
 * @author Uwe Finke
 */
public abstract class Application {

  static private Log logger = LogFactory.getLog(Application.class);
  static private Text text = new Text(Application.class);
  
  static public final int EXIT_CODE_OK = 0;
  static public final int EXIT_CODE_WARN = 4;
  static public final int EXIT_CODE_ERROR = 8;
  static public final int EXIT_CODE_SEVERE = 12;
  static public final int EXIT_CODE_FATAL = 16;

  private Stopwatch stopwatch;
  private String[] args;
  private int exitCode;

  protected Application() {

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
   * @param args
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
   * @param exitCode
   */
  protected void setExitCode(int exitCode) {
    
    this.exitCode = exitCode;
  }

  /**
   * Starts the application.
   * Call <code>start()</code> in the <code>main</code> method.
   * <p>
   * This method writes a start message to the log, 
   * calls <code>work()</code>, writes an end message containing
   * the elapsed time and calls <code>System.exit</code>
   * with the exit code.
   * Any Throwable is catched and logged before the application aborts.
   * In case of abort the exit code is set to <code>16</code> (fatal).
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
   * Does the application's work.
   * This method has to be implemented in a subclass;
   * it is called by <code>start</code>.
   * @throws Exception
   */
  abstract protected void execute() throws Exception;
}
