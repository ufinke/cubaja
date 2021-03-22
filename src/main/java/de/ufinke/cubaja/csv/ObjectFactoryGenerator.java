// Copyright (c) 2009 - 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

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

class ObjectFactoryGenerator implements Generator {

  static private class SetterEntry {
  
    String name;
    ObjectFactoryType readerType;
    Type dataType;
    int position;
    
    SetterEntry(String methodName, ObjectFactoryType readerType, Class<?> dataType, int position) {
      
      this.name = methodName;
      this.readerType = readerType;
      this.dataType = new Type(dataType);
      this.position = position;
    }
  }
  
  static private final Type objectFactoryType = new Type(ObjectFactory.class);
  static private final Type csvReaderType = new Type(CsvReader.class);
  static private final Type csvExceptionType = new Type(CsvException.class);

  private Type dataClassType;
  private ObjectFactoryType builtin;
  private Map<String, Integer> searchMap;
  private Map<String, SetterEntry> setterMap;
  private Map<Class<?>, ObjectFactory> factoryMap;
  private Class<?> lastClass;
  private ObjectFactory lastFactory;
  
  ObjectFactoryGenerator(Map<String, ColConfig> colMap) {
  
    createSearchMap(colMap);
    factoryMap = new HashMap<Class<?>, ObjectFactory>();
  }
  
  ObjectFactory getFactory(Class<?> dataClass) throws Exception {
    
    if (lastClass == dataClass) {
      return lastFactory;
    }
    
    lastClass = dataClass;
    
    lastFactory = factoryMap.get(lastClass);
    if (lastFactory != null) {
      return lastFactory;
    }
    
    dataClassType = new Type(dataClass);
    
    builtin = ObjectFactoryType.getBuiltin(dataClass);    
    if (builtin == null) {
      createSetterMap(dataClass);
    }
    
    Class<?> contextClass = (builtin == null) ? dataClass : getClass();
    Class<?> factoryClass = Loader.createClass(contextClass, this, "CsvReaderObjectFactory", dataClass);
    lastFactory = (ObjectFactory) factoryClass.newInstance();
    factoryMap.put(dataClass, lastFactory);
    
    setterMap = null;
    
    return lastFactory;
  }
  
  public GenClass generate(String className) throws Exception {

    GenClass genClass = new GenClass(ACC_PUBLIC | ACC_FINAL, className, Type.OBJECT, objectFactoryType);
    
    genClass.createDefaultConstructor();
    
    GenMethod method = genClass.createMethod(ACC_PUBLIC, Type.OBJECT, "createObject", csvReaderType);
    method.addException(csvExceptionType);
    
    if (builtin == null) {
      generateCode(method.getCode());    
    } else {
      generateBuiltin(method.getCode());
    }
    
    return genClass;
  }
  
  private void generateCode(CodeAttribute code) {
    
    code.newObject(dataClassType);
    code.duplicate(); // required for setter or return
    code.invokeSpecial(dataClassType, Type.VOID, "<init>");
    
    for (SetterEntry setter : setterMap.values()) {
      
      Type readerType = setter.readerType.getType();
      String readerMethod = setter.readerType.getReaderMethod();
      
      code.duplicate(); // data object with setter method
      
      code.loadLocalReference(1); // CsvReader
      code.loadConstant(setter.position);
      if (setter.readerType.needsClass()) {
        code.loadConstant(setter.dataType);
        code.invokeVirtual(csvReaderType, readerType, readerMethod, Type.INT, Type.CLASS);
        code.cast(setter.dataType);
        code.invokeVirtual(dataClassType, Type.VOID, setter.name, setter.dataType);
      } else {
        code.invokeVirtual(csvReaderType, readerType, readerMethod, Type.INT);
        code.invokeVirtual(dataClassType, Type.VOID, setter.name, setter.dataType);
      }
    }
    
    code.returnReference(); // returns duplicated data object
  }  
  
  private void generateBuiltin(CodeAttribute code) {
    
    code.loadLocalReference(1); // query
    code.loadConstant(1); // column #1
    code.invokeVirtual(csvReaderType, dataClassType, builtin.getReaderMethod(), Type.INT);
    code.returnReference();
  }
  
  private void createSearchMap(Map<String, ColConfig> colMap) {
    
    searchMap = new HashMap<String, Integer>(colMap.size() << 1);
    
    for (Map.Entry<String, ColConfig> entry : colMap.entrySet()) {
      searchMap.put(Util.createMethodName(entry.getKey(), "set"), entry.getValue().getPosition());
    }
  }
  
  private void createSetterMap(Class<?> clazz) {
    
    setterMap = new HashMap<String, SetterEntry>();
    
    for (Method method : clazz.getMethods()) {
      
      if (method.getReturnType() == Void.TYPE) {
        
        String methodName = method.getName();
        Integer position = searchMap.get(methodName);
        
        if (position != null) {
          
          Class<?>[] parameterTypes = method.getParameterTypes();
          
          if (parameterTypes.length == 1) {
            
            Class<?> parmClazz = parameterTypes[0];
            ObjectFactoryType type = ObjectFactoryType.getType(parmClazz);
            
            if (type != null) {
              
              SetterEntry entry = setterMap.get(methodName);
              
              if (entry == null || type.getPriority() < entry.readerType.getPriority()) {
                setterMap.put(methodName, new SetterEntry(methodName, type, parmClazz, position));
              }
            }
          }
        }
      }
    }
  }
}
