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
