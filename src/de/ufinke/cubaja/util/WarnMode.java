// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

/**
 * Action which should be performed when there might be something wrong.
 * @author Uwe Finke
 */
public enum WarnMode {

  /**
   * Ignore if there might be something wrong.
   */
  IGNORE,
  /**
   * Warn if there might be something wrong. The warning may be written to a log.
   */
  WARN,
  /**
   * Throw an exception if there might be something wrong.
   */
  ERROR
}
