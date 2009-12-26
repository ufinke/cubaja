// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import static de.ufinke.cubaja.cafebabe.AccessFlags.ACC_FINAL;
import static de.ufinke.cubaja.cafebabe.AccessFlags.ACC_PUBLIC;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import de.ufinke.cubaja.cafebabe.CodeAttribute;
import de.ufinke.cubaja.cafebabe.GenClass;
import de.ufinke.cubaja.cafebabe.GenMethod;
import de.ufinke.cubaja.cafebabe.Generator;
import de.ufinke.cubaja.cafebabe.Loader;
import de.ufinke.cubaja.cafebabe.Type;
import de.ufinke.cubaja.util.Util;

class ObjectWriterGenerator implements Generator {

  static private class GetterEntry {
  
    String name;
    ObjectFactoryType type;
    int position;
    
    GetterEntry(String methodName, ObjectFactoryType type, int position) {
      
      this.name = methodName;
      this.type = type;
      this.position = position;
    }
  }
  
  static private final Type objectType = new Type(Object.class);
  static private final Type voidType = new Type(Void.TYPE);
  static private final Type intType = new Type(Integer.TYPE);
  static private final Type objectWriterType = new Type(ObjectWriter.class);
  static private final Type csvWriterType = new Type(CsvWriter.class);
  static private final Type exceptionType = new Type(Exception.class);

  private Type dataClassType;
  private Map<String, Integer> searchMap;
  private Map<String, GetterEntry> getterMap;
  private Map<Class<?>, ObjectWriter> writerMap;
  private Class<?> lastClass;
  private ObjectWriter lastWriter;
  
  ObjectWriterGenerator(Map<String, Integer> nameMap) {
  
    createSearchMap(nameMap);
    writerMap = new HashMap<Class<?>, ObjectWriter>();
  }
  
  ObjectWriter getWriter(Class<?> dataClass) throws Exception {
    
    if (lastClass == dataClass) {
      return lastWriter;
    }
    
    lastClass = dataClass;
    
    lastWriter = writerMap.get(lastClass);
    if (lastWriter != null) {
      return lastWriter;
    }
    
    dataClassType = new Type(dataClass);
    createGetterMap(dataClass);
    
    Class<?> writerClass = Loader.createClass(this, "CsvWriterObjectWriter", dataClass);
    lastWriter = (ObjectWriter) writerClass.newInstance();
    writerMap.put(dataClass, lastWriter);
    
    getterMap = null;
    
    return lastWriter;
  }
  
  public GenClass generate(String className) throws Exception {

    GenClass genClass = new GenClass(ACC_PUBLIC | ACC_FINAL, className, objectType, objectWriterType);
    
    genClass.createDefaultConstructor();
    
    GenMethod method = genClass.createMethod(ACC_PUBLIC, voidType, "writeObject", csvWriterType, objectType);
    method.addException(exceptionType);
    generateCode(method.getCode());    
    
    return genClass;
  }
  
  private void generateCode(CodeAttribute code) {
    
    code.loadLocalReference(2); // dataObject
    code.cast(dataClassType);
    code.storeLocalReference(3);
    
    for (GetterEntry getter : getterMap.values()) {
      Type parmType = getter.type.getType();
      code.loadLocalReference(1); // writer
      code.loadConstant(getter.position);      
      code.loadLocalReference(3); // data object
      code.invokeVirtual(dataClassType, parmType, getter.name); // get
      code.invokeVirtual(csvWriterType, voidType, "write", intType, parmType);
    }
    
    code.returnVoid();
  }  
  
  private void createSearchMap(Map<String, Integer> nameMap) {
    
    searchMap = new HashMap<String, Integer>(nameMap.size() << 1);
    
    for (Map.Entry<String, Integer> entry : nameMap.entrySet()) {
      searchMap.put(Util.createMethodName(entry.getKey(), "get"), entry.getValue());
    }
  }
  
  private void createGetterMap(Class<?> clazz) {
    
    getterMap = new HashMap<String, GetterEntry>();
    
    for (Method method : clazz.getMethods()) {
      
      if (method.getParameterTypes().length == 0) {
        
        String methodName = method.getName();
        Integer position = searchMap.get(methodName);
        
        if (position != null) {
          
          Class<?> returnType = method.getReturnType();
          ObjectFactoryType type = ObjectFactoryType.getType(returnType);
          
          if (type != null) {
            getterMap.put(methodName, new GetterEntry(methodName, type, position));
          }
        }
      }
    }
  }
}
