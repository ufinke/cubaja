// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import de.ufinke.cubaja.config.Mandatory;

/**
 * <p>
 * Replacement definition.
 * </p><p>
 * A <code>ColConfig</code>
 * may contain any number of replacement definitions.
 * For every definition, 
 * the replacement of the retrieved column content string is 
 * delegated to {@link java.lang.String#replaceAll String.replaceAll}.
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
 * <td style="text-align:left;vertical-align:top"><code>regex</code></td>
 * <td style="text-align:left;vertical-align:top">the regular expression</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * </tr>
 * <tr>
 * <td style="text-align:left;vertical-align:top"><code>replacement</code></td>
 * <td style="text-align:left;vertical-align:top">the replacement string</td>
 * <td style="text-align:center;vertical-align:top">A</td>
 * <td style="text-align:center;vertical-align:top">x</td>
 * <td style="text-align:center;vertical-align:top">x</td>
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
   * @return regex pattern
   */
  public String getRegex() {

    return regex;
  }

  /**
   * Sets the regular expression.
   * @param regex pattern
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
   * @param replacement text which replaces the pattern
   */
  @Mandatory
  public void setReplacement(String replacement) {

    this.replacement = replacement;
  }

}
