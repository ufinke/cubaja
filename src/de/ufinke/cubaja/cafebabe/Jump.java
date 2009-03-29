package de.ufinke.cubaja.cafebabe;

class Jump {

  private int size;
  private int opCodeOffset;
  private int offset;
  private Label label;

  Jump(int size, int opCodeOffset, int offset, Label label) {

    this.size = size;
    this.opCodeOffset = opCodeOffset;
    this.offset = offset;
    this.label = label;
  }

  int getSize() {

    return size;
  }

  int getOpCodeOffset() {

    return opCodeOffset;
  }

  int getOffset() {

    return offset;
  }

  Label getLabel() {

    return label;
  }
}
