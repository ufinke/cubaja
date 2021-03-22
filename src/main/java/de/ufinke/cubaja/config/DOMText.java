// Copyright (c) 2010 - 2013, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import de.ufinke.cubaja.util.Util;

/**
 * Represents text content.
 * @author Uwe Finke
 */
public class DOMText extends DOMContent {

  private String text;
  private String normalizedText;

  /**
   * Constructor.
   */
  public DOMText() {

    super(DOMType.TEXT);
  }
  
  /**
   * Constructor with text.
   * @param text text string
   */
  public DOMText(String text) {
    
    super(DOMType.TEXT);
    this.text = text;
  }

  /**
   * Returns the text.
   * @return text
   */
  public String getText() {

    return text;
  }

  /**
   * Sets the text.
   * @param text text string
   */
  public void setText(String text) {

    this.text = text;
  }
  
  /**
   * Returns normalized text.
   * The normalized text is trimmed and all
   * consecutive whitespace characters are replaced by a single space character.
   * @return text
   */
  public String getNormalizedText() {
    
    if (normalizedText == null) {
      normalizedText = Util.normalize(text);
    }
    return normalizedText;
  }
  
  /**
   * Returns the text.
   * Same as getText().
   */
  public String toString() {
    
    return text;
  }
}
