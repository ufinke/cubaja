// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

class Jump {

  private int size;
  private int opCodeOffset;
  private int jumpOffset;
  private Label label;

  Jump(int size, int opCodeOffset, int jumpOffset, Label label) {

    this.size = size;
    this.opCodeOffset = opCodeOffset;
    this.jumpOffset = jumpOffset;
    this.label = label;
  }

  int getSize() {

    return size;
  }

  int getOpCodeOffset() {

    return opCodeOffset;
  }

  int getJumpOffset() {

    return jumpOffset;
  }

  Label getLabel() {

    return label;
  }
}
