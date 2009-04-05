package de.ufinke.cubaja.io.test.example1;

import java.util.*;

public class Data {

  private int integer;
  private String string;
  private Date date;
  private TestStreamable streamable;
  private SubData subData;

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

  public TestStreamable getStreamable() {
  
    return streamable;
  }
  
  public void setStreamable(TestStreamable streamable) {
  
    this.streamable = streamable;
  }
  
  public SubData getSubData() {
    
    return subData;
  }
  
  public void setSubData(SubData subData) {
    
    this.subData = subData;
  }
}
