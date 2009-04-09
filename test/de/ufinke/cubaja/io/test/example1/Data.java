package de.ufinke.cubaja.io.test.example1;

import java.util.*;

public class Data {

  private int integer;
  private String string;
  private Date date;
  private SubData subData;
  private TestEnum testEnum;

  public Data() {

  }

  public int getInteger() {

    return integer;
  }

  public void setInteger(int integer) {

    this.integer = integer;
  }

  public String getString() {

    return string;
  }

  public void setString(String string) {

    this.string = string;
  }

  public Date getDate() {

    return date;
  }

  public void setDate(Date date) {

    this.date = date;
  }

  public SubData getSubData() {

    return subData;
  }

  public void setSubData(SubData subData) {

    this.subData = subData;
  }

  public TestEnum getTestEnum() {

    return testEnum;
  }

  public void setTestEnum(TestEnum testEnum) {

    this.testEnum = testEnum;
  }
}
