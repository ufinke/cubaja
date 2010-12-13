// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

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
   * @param text
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
   * @param text
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
      normalize();
    }
    return normalizedText;
  }
  
  private void normalize() {
    
    if (text == null) {
      return;
    }
    
    StringBuilder sb = new StringBuilder(text.length());
    boolean whitespacePending = false;
    
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (Character.isWhitespace(c)) {
        whitespacePending = true;
      } else {
        if (whitespacePending) {
          sb.append(' ');
          whitespacePending = false;
        }
        sb.append(c);
      }
    }
    
    normalizedText = sb.toString();
  }

}
