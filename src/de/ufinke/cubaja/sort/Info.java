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
