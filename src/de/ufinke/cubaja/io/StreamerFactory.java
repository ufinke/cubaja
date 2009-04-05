// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import de.ufinke.cubaja.cafebabe.*;
import java.util.*;
import static de.ufinke.cubaja.cafebabe.AccessFlags.*;

public class StreamerFactory implements Generator {

  {
    loader = new GenClassLoader();
  }
  
  static private GenClassLoader loader;
  
  static private Type objectType = new Type(Object.class);
  static private Type voidType = new Type(Void.TYPE);
  static private Type streamerType = new Type(Streamer.class);
  static private Type exceptionType = new Type(Exception.class);
  static private Type inputStreamType = new Type(BinaryInputStream.class);
  static private Type outputStreamType = new Type(BinaryOutputStream.class);
  
  @SuppressWarnings("unchecked")
  static public <D> Streamer<D> createStreamer(Class<D> clazz) throws Exception {
  
    return (Streamer<D>) loader.createInstance(new StreamerFactory(clazz));
  }
  
  private Class<?> clazz;
  private String className;
  private Type clazzType;
  
  private StreamerFactory(Class<?> clazz) {
  
    this.clazz = clazz;
  }

  public String getClassName() throws Exception {

    if (className == null) {
      StringBuilder sb = new StringBuilder(200);
      sb.append(getClass().getPackage().getName());
      sb.append(".BinaryStreamer_");
      sb.append(clazz.getName().replace('.', '_'));
      className = sb.toString();
    }
    
    return className;
  }
  
  public GenClass generate() throws Exception {

    clazzType = new Type(clazz);
    
    GenClass genClass = new GenClass(ACC_PUBLIC, getClassName(), streamerType);
    
    genClass.createDefaultConstructor();
    GenMethod writer = genClass.createMethod(ACC_PUBLIC, voidType, "write", clazzType);
    writer.createGenericBridge(voidType, objectType);
    GenMethod reader = genClass.createMethod(ACC_PUBLIC, clazzType, "read");
    reader.createGenericBridge(objectType);
    
    generateWriter(writer);
    generateReader(reader);
    
    return genClass;
  }
  
  private void generateWriter(GenMethod method) {
    
    method.addException(exceptionType);
    
    CodeAttribute code = method.getCode();
    
    code.loadLocalReference(0);
    code.getField(streamerType, outputStreamType, "out");
    code.branchIfNull("nullStream");
    ...
    
    code.defineLabel("nullStream");
    ...
  }
  
  private void generateReader(GenMethod method) {
    
    method.addException(exceptionType);
  }

}
