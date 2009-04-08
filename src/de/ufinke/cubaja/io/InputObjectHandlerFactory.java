// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import static de.ufinke.cubaja.cafebabe.AccessFlags.ACC_PUBLIC;
import java.util.List;
import de.ufinke.cubaja.cafebabe.CodeAttribute;
import de.ufinke.cubaja.cafebabe.GenClass;
import de.ufinke.cubaja.cafebabe.GenClassLoader;
import de.ufinke.cubaja.cafebabe.GenMethod;
import de.ufinke.cubaja.cafebabe.Generator;
import de.ufinke.cubaja.cafebabe.Type;

class InputObjectHandlerFactory implements Generator {

  static private final GenClassLoader loader = new GenClassLoader();
  
  static private final Type objectType = new Type(Object.class);
  static private final Type classType = new Type(Class.class);
  static private final Type voidType = new Type(Void.TYPE);
  static private final Type exceptionType = new Type(Exception.class);
  static private final Type streamType = new Type(BinaryInputStream.class);
  static private final Type handlerType = new Type(InputObjectHandler.class);
  
  static InputObjectHandler getHandler(Class<?> dataClass, List<PropertyDescription> propertyList) throws Exception {
    
    return (InputObjectHandler) loader.createInstance(new InputObjectHandlerFactory(dataClass, propertyList));
  }
  
  private List<PropertyDescription> propertyList;
  private Class<?> dataClass;
  private String dataClassName;
  private Type dataClassType;
  private Type genClassType;
  private CodeAttribute code;
  
  private InputObjectHandlerFactory(Class<?> dataClass, List<PropertyDescription> propertyList) {
  
    this.dataClass = dataClass;
    this.propertyList = propertyList;
  }
  
  public String getClassName() throws Exception {

    if (dataClassName == null) {
      StringBuilder sb = new StringBuilder(200);
      sb.append(getClass().getPackage().getName());
      sb.append("InputObjectHandler_");
      sb.append(dataClass.getName().replace('.', '_'));
      dataClassName = sb.toString();
    }
    
    return dataClassName;
  }
  
  public GenClass generate() throws Exception {

    GenClass genClass = new GenClass(ACC_PUBLIC, getClassName(), objectType, handlerType);
    
    genClassType = new Type(genClass);
    dataClassType = new Type(dataClass);
    
    genClass.createDefaultConstructor();
    
    GenMethod read = genClass.createMethod(ACC_PUBLIC, voidType, "read", streamType, classType);    
    read.addException(exceptionType);    
    code = read.getCode();
    
    return genClass;
  }
}
