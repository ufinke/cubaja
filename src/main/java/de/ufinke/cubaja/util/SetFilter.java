package de.ufinke.cubaja.util;

import java.util.List;

public interface SetFilter<I, O> {

  public List<O> filter(I input) throws Exception;
}
