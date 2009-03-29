package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;

interface Generatable {

  public void generate(DataOutputStream out) throws IOException;
}
