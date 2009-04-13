// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sort;

class Info<D> {

  private Action action;
  private SortArray<D> array;

  public Info(Action action) {

    this.action = action;
  }

  public SortArray<D> getArray() {

    return array;
  }

  public void setArray(SortArray<D> array) {

    this.array = array;
  }

  public Action getAction() {

    return action;
  }
}
