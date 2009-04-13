package de.ufinke.cubaja.sort;

class SortArray<D> {

  private D[] array;
  private int size;
  
  SortArray(D[] array) {
    
    this.array = array;
  }
  
  boolean isFull() {
    
    return size == array.length;
  }
  
  void add(D element) {
    
    array[size++] = element;
  }
  
  int getSize() {
    
    return size;
  }
  
  D[] getArray() {
    
    return array;
  }
}
