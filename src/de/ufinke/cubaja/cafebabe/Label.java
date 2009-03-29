package de.ufinke.cubaja.cafebabe;

class Label {

  private int offset;
  private int stackSize;
  
  Label() {
  
    offset = -1;
  }
  
  void setOffset(int offset) {
    
    this.offset = offset;
  }
  
  int getOffset() {
    
    return offset;
  }
  
  void setStackSize(int stackSize) {
    
    this.stackSize = Math.max(this.stackSize, stackSize);
  }
  
  int getStackSize() {
    
    return stackSize;
  }
}
