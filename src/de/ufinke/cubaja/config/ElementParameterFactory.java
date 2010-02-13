// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.annotation.Annotation;


class ElementParameterFactory implements ParameterFactory {

  private Object parameter;
  
  public ElementParameterFactory(Object parameter) {
  
    this.parameter = parameter;
  }
  
  public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {

    return parameter;
  }

  public boolean isNode() {

    return true;
  }

}
