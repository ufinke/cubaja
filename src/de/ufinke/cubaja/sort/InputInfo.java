// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

class InputInfo {

  private Object[] array;
  private int arraySize;
  private SortAlgorithm<?> algorithm;

  public InputInfo() {

  }

  public Object[] getArray() {

    return array;
  }

  public void setArray(Object[] array) {

    this.array = array;
  }

  public int getArraySize() {

    return arraySize;
  }

  public void setArraySize(int arraySize) {

    this.arraySize = arraySize;
  }

  public SortAlgorithm<?> getAlgorithm() {

    return algorithm;
  }

  public void setAlgorithm(SortAlgorithm<?> algorithm) {

    this.algorithm = algorithm;
  }
}
