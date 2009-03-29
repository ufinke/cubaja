package de.ufinke.cubaja.config.test.example1;

import de.ufinke.cubaja.config.*;

public class FileConfig {

  private String key;
  private String name;

  public FileConfig() {

  }

  public String getKey() {

    return key;
  }

  @Mandatory
  public void setKey(String key) {

    this.key = key;
  }

  public String getName() {

    return name;
  }

  @Mandatory
  public void setName(String name) {

    this.name = name;
  }
}
