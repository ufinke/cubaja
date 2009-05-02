// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

public interface ObjectFactory {

  public Object createObject(CsvReader reader) throws Exception;
}
