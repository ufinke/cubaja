package de.ufinke.cubaja.io.test.example1;

import de.ufinke.cubaja.io.*;
import java.util.*;

public class TestStreamable extends ArrayList<String> implements Streamable {

  public TestStreamable() {

  }

  public void read(BinaryInputStream stream) throws Exception {

    int length = stream.readShort();
    for (int i = 0; i < length; i++) {
      add(stream.readString());
    }
  }

  public void write(BinaryOutputStream stream) throws Exception {

    stream.writeShort(size());
    for (String s : this) {
      stream.writeString(s);
    }
  }

}
