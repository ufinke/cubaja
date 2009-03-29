package de.ufinke.cubaja.config.test.simple;

import de.ufinke.cubaja.config.*;

public class SubElement {

  private String value;
  private int number;
  private double decimal;
  private SomeEnum[] enums;
  private int[] intArray;
  
  public SubElement() {
    
  }
  
  @Mandatory
  public void setNumber(int value) {
    
    number = value;
  }
  
  public int getNumber() {
    
    return number;
  }
  
  @CharData
  public void setValue(String value) {
    
    this.value = value;
  }
  
  public String getValue() {
    
    return value;
  }
  
  public void setEnums(SomeEnum[] enums) {
    
    this.enums = enums;
  }
  
  public SomeEnum[] getEnums() {
    
    return enums;
  }
  
  public void setDecimal(double decimal) {
    
    this.decimal = decimal;
  }
  
  public double getDecimal() {
    
    return decimal;
  }
  
  public int[] getIntArray() {
  
    return intArray;
  }

  public void setIntArray(int[] intArray) {
  
    this.intArray = intArray;
  }
  
}
