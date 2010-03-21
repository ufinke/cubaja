// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import de.ufinke.cubaja.config.Mandatory;

/**
 * Replacement definition.
 * <p>
 * A <tt>ColConfig</tt>
 * may contain any number of replacement definitions.
 * For every definition, 
 * the retrieved column content string is 
 * replaced using method <tt>replaceAll</tt> of class <tt>String</tt>.
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
 *     <td align="left" valign="top"><tt>regex</tt></td>
 *     <td align="left" valign="top">the regular expression</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top">x</td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 *   <tr bgcolor="#eeeeff">
 *     <td align="left" valign="top"><tt>replacement</tt></td>
 *     <td align="left" valign="top">the replacement string</td>
 *     <td align="center" valign="top">A</td>
 *     <td align="center" valign="top">x</td>
 *     <td align="center" valign="top">x</td>
 *     </tr>
 * </table>
 * <tt>A/E</tt>: attribute or subelement
 * <br/>
 * <tt>M</tt>: mandatory
 * <br/>
 * <tt>U</tt>: unique
 * </blockquote>
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
