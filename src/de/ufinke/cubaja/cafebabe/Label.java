package de.ufinke.cubaja.cafebabe;

class Label {

  private String name;
  private boolean defined;
  private int offset;
  private int stackSize;
  
  Label(String name) {
  
    this.name = name;
  }
  
  void define(int offset, int stackSize) {
    
    this.offset = offset;
    this.stackSize = Math.max(this.stackSize, stackSize);
    defined = true;
  }
  
  String getName() {
    
    return name;
  }
  
  int getOffset() {
    
    return offset;
  }
  
  int getStackSize() {
    
    return stackSize;
  }
  
  boolean isDefined() {
    
    return defined;
  }
}
