// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import de.ufinke.cubaja.config.Mandatory;

/**
 * Replacement definition.
 * <p>
 * A <code>ColConfig</code>
 * may contain any number of replacement definitions.
 * For every definition, 
 * the retrieved column content string is 
 * replaced using method <code>replaceAll</code> of class <code>String</code>.
 * @author Uwe Finke
 */
public class ReplaceConfig {

  private String regex;
  private String replacement;

  /**
   * Constructor.
   */
  public ReplaceConfig() {

  }

  /**
   * Returns the regular expression.
   * @return regex
   */
  public String getRegex() {

    return regex;
  }

  /**
   * Sets the regular expression.
   * @param regex
   */
  @Mandatory
  public void setRegex(String regex) {

    this.regex = regex;
  }

  /**
   * Returns the replacement string.
   * @return replacement
   */
  public String getReplacement() {

    return replacement;
  }

  /**
   * Sets the replacement string.
   * @param replacement
   */
  @Mandatory
  public void setReplacement(String replacement) {

    this.replacement = replacement;
  }

}
