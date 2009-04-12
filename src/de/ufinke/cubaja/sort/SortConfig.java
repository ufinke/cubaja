// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

public class SortConfig {

  private long totalRecordCount;
  private long inMemoryRecordCount;
  private SortAlgorithm<?> algorithm;

  public SortConfig() {

  }

  public long getTotalRecordCount() {

    if (totalRecordCount <= 0) {
      totalRecordCount = 100000000;
    }
    return totalRecordCount;
  }

  public void setTotalRecordCount(long totalRecordCount) {

    this.totalRecordCount = totalRecordCount;
  }

  public long getInMemoryRecordCount() {

    if (inMemoryRecordCount <= 0) {
      inMemoryRecordCount = 2000000;
    }
    return inMemoryRecordCount;
  }

  public void setInMemoryRecordCount(long inMemoryRecordCount) {

    this.inMemoryRecordCount = inMemoryRecordCount;
  }

  @SuppressWarnings("unchecked")
  public SortAlgorithm<?> getAlgorithm() {

    if (algorithm == null) {
      algorithm = new Quicksort();
    }
    return algorithm;
  }

  public void setAlgorithm(SortAlgorithm<?> algorithm) {

    this.algorithm = algorithm;
  }
}
