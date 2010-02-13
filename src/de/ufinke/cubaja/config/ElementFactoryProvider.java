// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.util.Map;

public interface ElementFactoryProvider {

  public ElementFactory getFactory(String tagName, Map<String, String> attributes) throws ConfigException;
}
