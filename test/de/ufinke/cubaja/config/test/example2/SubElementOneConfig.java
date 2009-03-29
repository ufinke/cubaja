package de.ufinke.cubaja.config.test.example2;

import de.ufinke.cubaja.config.*;

public class SubElementOneConfig extends ConfigNode {

  private int number;
  
  public SubElementOneConfig() {
    
  }
  
  public void setNumber(int number) {
    
    this.number = number;
  }
  
  public int getNumber() {
    
    return number;
  }
}
