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
  private CodeAttribute read;
  
  private InputObjectHandlerFactory(Class<?> dataClass, List<PropertyDescription> propertyList) {
  
    this.dataClass = dataClass;
    this.propertyList = propertyList;
  }
  
  public String getClassName() throws Exception {

    if (dataClassName == null) {
      StringBuilder sb = new StringBuilder(200);
      sb.append(getClass().getPackage().getName());
      sb.append(".InputObjectHandler_");
      sb.append(dataClass.getName().replace('.', '_'));
      dataClassName = sb.toString();
    }
    
    return dataClassName;
  }
  
  public GenClass generate() throws Exception {

    GenClass genClass = new GenClass(ACC_PUBLIC, getClassName(), objectType, handlerType);
    
    dataClassType = new Type(dataClass);
    
    genClass.createDefaultConstructor();
    
    GenMethod reader = genClass.createMethod(ACC_PUBLIC, objectType, "read", streamType, classType);    
    reader.addException(exceptionType);    
    read = reader.getCode();
    
    generateRead();
    
    return genClass;
  }
  
  private void generateRead() {
    
    read.newObject(dataClassType);
    read.duplicate(); // data object
    read.invokeSpecial(dataClassType, voidType, "<init>"); // default constructor
    read.loadLocalReference(1); // stream
    
    for (PropertyDescription property : propertyList) {
      generateReadProperty(property);
    }
    
    read.pop(); // stream
    read.returnReference(); // data object
  }

  private void generateReadProperty(PropertyDescription property) {

    BinaryStreamParameter parameter = BinaryStreamParameter.getStreamParameter(property);
    
    read.duplicateDouble();
    
    if (parameter.needsClazz()) {
      read.loadConstant(property.getClazz());
      read.invokeVirtual(streamType, parameter.getType(), parameter.getReaderMethod(), classType); // xxx = stream.readXXX(class)
    } else {
      read.invokeVirtual(streamType, parameter.getType(), parameter.getReaderMethod()); // xxx = stream.readXXX()      
    }
    
    if (property.isNoSetterPresent()) {
      read.pop(parameter.getType().getSize()); // discard stack element
    } else {
      if (parameter.needsClazz() && parameter.getClazz() != property.getClazz()) {
        read.cast(property.getType());
      }
      read.invokeVirtual(dataClassType, voidType, property.getSetterName(), property.getType()); // data.setXXX(xxx)
    }
  }
  
}
