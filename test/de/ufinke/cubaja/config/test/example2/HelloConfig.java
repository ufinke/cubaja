package de.ufinke.cubaja.config.test.example2;

import de.ufinke.cubaja.config.*;

public class HelloConfig extends ConfigNode {

  private String hello;

  public HelloConfig() {

  }

  public String getHello() {

    return hello;
  }

  public void setHello(String hello) {

    this.hello = hello;
  }
}
