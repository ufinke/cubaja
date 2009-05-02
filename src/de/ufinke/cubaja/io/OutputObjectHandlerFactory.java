// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import static de.ufinke.cubaja.cafebabe.AccessFlags.ACC_PUBLIC;
import java.util.List;
import de.ufinke.cubaja.cafebabe.CodeAttribute;
import de.ufinke.cubaja.cafebabe.GenClass;
import de.ufinke.cubaja.cafebabe.GenMethod;
import de.ufinke.cubaja.cafebabe.Generator;
import de.ufinke.cubaja.cafebabe.Loader;
import de.ufinke.cubaja.cafebabe.Type;

class OutputObjectHandlerFactory implements Generator {

  static private final Type objectType = new Type(Object.class);
  static private final Type voidType = new Type(Void.TYPE);
  static private final Type exceptionType = new Type(Exception.class);
  static private final Type streamType = new Type(BinaryOutputStream.class);
  static private final Type handlerType = new Type(OutputObjectHandler.class);
  
  static OutputObjectHandler getHandler(Class<?> dataClass, List<PropertyDescription> propertyList) throws Exception {
    
    String className = Loader.createClassName(OutputObjectHandlerFactory.class, "OutputObjectHandler", dataClass);
    return (OutputObjectHandler) Loader.createInstance(className, new OutputObjectHandlerFactory(dataClass, propertyList));
  }
  
  private List<PropertyDescription> propertyList;
  private Class<?> dataClass;
  private Type dataClassType;
  private CodeAttribute write;
  
  private OutputObjectHandlerFactory(Class<?> dataClass, List<PropertyDescription> propertyList) {
  
    this.dataClass = dataClass;
    this.propertyList = propertyList;
  }
  
  public GenClass generate(String className) throws Exception {

    dataClassType = new Type(dataClass);
    
    GenClass genClass = new GenClass(ACC_PUBLIC, className, objectType, handlerType);
    
    genClass.createDefaultConstructor();
    
    GenMethod writer = genClass.createMethod(ACC_PUBLIC, voidType, "write", streamType, objectType);    
    writer.addException(exceptionType);    
    write = writer.getCode();
    
    generateWrite();
    
    return genClass;
  }
  
  private void generateWrite() {
    
    write.loadLocalReference(1); // stream
    write.loadLocalReference(2); // object
    write.cast(dataClassType);

    BinaryStreamParameter parameter = BinaryStreamParameter.getStreamParameter(dataClass);
    
    if (parameter == BinaryStreamParameter.OBJECT) {      
      for (PropertyDescription property : propertyList) {
        generateWriteProperty(property);
      }
    } else { // builtin type   
      write.invokeVirtual(streamType, voidType, parameter.getWriterMethod(), parameter.getType()); // stream.writeXXX(xxx)
    }    
    
    write.returnVoid();
  }
  
  private void generateWriteProperty(PropertyDescription property) {
    
    BinaryStreamParameter parameter = BinaryStreamParameter.getStreamParameter(property.getClazz());
    
    write.duplicateDouble(); // 2 addresses on stack are needed on every loop
    write.invokeVirtual(dataClassType, property.getType(), property.getGetterName()); // xxx = data.getXXX()    
    write.invokeVirtual(streamType, voidType, parameter.getWriterMethod(), parameter.getType()); // stream.writeXXX(xxx)
  }
  
}
