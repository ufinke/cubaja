package de.ufinke.cubaja.config.test.simple;

import de.ufinke.cubaja.config.*;

public class SubElement extends ConfigNode {

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
  
  public String getValue() {
    
    return charData();
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
