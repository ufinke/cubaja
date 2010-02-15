// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.etl.core;

import de.ufinke.cubaja.config.Mandatory;

public class TaskdefConfig {

  private String name;
  private String className;

  public TaskdefConfig() {

  }

  public String getName() {

    return name;
  }

  @Mandatory
  public void setName(String name) {

    this.name = name;
  }

  public String getClassName() {

    return className;
  }

  @Mandatory
  public void setClass(String className) {

    this.className = className;
  }
}
