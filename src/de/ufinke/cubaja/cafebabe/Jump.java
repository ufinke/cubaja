package de.ufinke.cubaja.cafebabe;

class Jump {

  private int size;
  private int opCodeOffset;
  private int offsetFromOpCode;
  private Label label;

  Jump(int size, int opCodeOffset, int offsetFromOpCode, Label label) {

    this.size = size;
    this.opCodeOffset = opCodeOffset;
    this.offsetFromOpCode = offsetFromOpCode;
    this.label = label;
  }

  int getSize() {

    return size;
  }

  int getOpCodeOffset() {

    return opCodeOffset;
  }

  int getResolveOffset() {

    return offsetFromOpCode;
  }

  Label getLabel() {

    return label;
  }
}
