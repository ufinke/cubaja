// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

class MemoryManager {

  static private final int MIN_INPUT_ARRAY_CAPACITY = 100;
  
  private SortConfig config;
  private int inputArrayCapacity;
  
  MemoryManager(SortConfig config) {
  
    this.config = config;
  }
  
  int getInputArrayCapacity() {
    
    if (inputArrayCapacity == 0) {
      double parallelArrays = 3 + config.getAlgorithm().memoryFactor();
      inputArrayCapacity = (int) (config.getInMemoryRecordCount() / parallelArrays);
      inputArrayCapacity = Math.max(inputArrayCapacity, MIN_INPUT_ARRAY_CAPACITY);
    }
    
    return inputArrayCapacity;
  }
}
