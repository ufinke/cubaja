// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import de.ufinke.cubaja.config.Mandatory;

public class ReplaceConfig {

  private String regex;
  private String replacement;

  public ReplaceConfig() {

  }

  public String getRegex() {

    return regex;
  }

  @Mandatory
  public void setRegex(String regex) {

    this.regex = regex;
  }

  public String getReplacement() {

    return replacement;
  }

  @Mandatory
  public void setReplacement(String replacement) {

    this.replacement = replacement;
  }

}
