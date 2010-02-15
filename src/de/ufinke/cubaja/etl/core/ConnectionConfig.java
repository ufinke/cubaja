// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.etl.core;

import de.ufinke.cubaja.config.Mandatory;

public class ConnectionConfig {

  private String task;
  private String connector;

  public ConnectionConfig() {

  }

  public String getTask() {

    return task;
  }

  @Mandatory
  public void setTask(String task) {

    this.task = task;
  }

  public String getConnector() {

    return connector;
  }

  public void setConnector(String connector) {

    this.connector = connector;
  }
}
