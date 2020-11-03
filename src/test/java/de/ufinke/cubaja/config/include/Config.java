package de.ufinke.cubaja.config.include;

import java.util.*;

public class Config {

  private List<FooConfig> fooList;

  public Config() {

    fooList = new ArrayList<FooConfig>();
  }

  public List<FooConfig> getFooList() {

    return fooList;
  }

  public void addFoo(FooConfig foo) {

    fooList.add(foo);
  }
}
