// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
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

class ObjectFactoryGenerator implements Generator {

  static private class SetterEntry {
  
    String name;
    ObjectFactoryType type;
    int position;
    
    SetterEntry(String methodName, ObjectFactoryType parameterType, int position) {
      
      this.name = methodName;
      this.type = parameterType;
      this.position = position;
    }
  }
  
  static private final Type objectType = new Type(Object.class);
  static private final Type clazzType = new Type(Class.class);
  static private final Type voidType = new Type(Void.TYPE);
  static private final Type intType = new Type(Integer.TYPE);
  static private final Type objectFactoryType = new Type(ObjectFactory.class);
  static private final Type csvReaderType = new Type(CsvReader.class);
  static private final Type csvExceptionType = new Type(CsvException.class);

  private Type dataClassType;
  private Map<String, Integer> searchMap;
  private Map<String, SetterEntry> setterMap;
  private Map<Class<?>, ObjectFactory> factoryMap;
  private Class<?> lastClass;
  private ObjectFactory lastFactory;
  
  ObjectFactoryGenerator(Map<String, Integer> nameMap) {
  
    createSearchMap(nameMap);
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
    createSetterMap(dataClass);
    
    Class<?> factoryClass = Loader.createClass(this, "CsvReaderObjectFactory", dataClass);
    lastFactory = (ObjectFactory) factoryClass.newInstance();
    factoryMap.put(dataClass, lastFactory);
    
    setterMap = null;
    
    return lastFactory;
  }
  
  public GenClass generate(String className) throws Exception {

    GenClass genClass = new GenClass(ACC_PUBLIC | ACC_FINAL, className, objectType, objectFactoryType);
    
    genClass.createDefaultConstructor();
    
    GenMethod method = genClass.createMethod(ACC_PUBLIC, objectType, "createObject", csvReaderType);
    method.addException(csvExceptionType);
    generateCode(method.getCode());    
    
    return genClass;
  }
  
  private void generateCode(CodeAttribute code) {
    
    code.newObject(dataClassType);
    code.duplicate(); // required for setter or return
    code.invokeSpecial(dataClassType, voidType, "<init>");
    
    for (SetterEntry setter : setterMap.values()) {
      
      ObjectFactoryType type = setter.type;
      Type parmType = type.getType();
      
      code.duplicate(); // data object with setter method
      
      code.loadLocalReference(1); // CsvReader
      code.loadConstant(setter.position);
      if (type.needsClass()) {
        code.loadConstant(parmType);
        code.invokeVirtual(csvReaderType, parmType, type.getReaderMethod(), intType, clazzType);
      } else {
        code.invokeVirtual(csvReaderType, parmType, type.getReaderMethod(), intType);
      }
      
      code.invokeVirtual(dataClassType, voidType, setter.name, parmType); // operates on duplicated data object
    }
    
    code.returnReference(); // returns duplicated data object
  }  
  
  private void createSearchMap(Map<String, Integer> nameMap) {
    
    searchMap = new HashMap<String, Integer>(nameMap.size() << 1);
    
    for (Map.Entry<String, Integer> entry : nameMap.entrySet()) {
      searchMap.put(Util.createMethodName(entry.getKey(), "set"), entry.getValue());
    }
  }
  
  private void createSetterMap(Class<?> clazz) {
    
    setterMap = new HashMap<String, SetterEntry>();
    
    for (Method method : clazz.getMethods()) {
      
      if (method.getReturnType() == Void.TYPE) {
        
        String methodName = method.getName();
        Integer position = searchMap.get(methodName);
        
        if (position != null && method.getReturnType() == Void.TYPE) {
          
          Class<?>[] parameterTypes = method.getParameterTypes();
          
          if (parameterTypes.length == 1) {
            
            ObjectFactoryType type = ObjectFactoryType.getType(parameterTypes[0]);
            
            if (type != null) {
              
              SetterEntry entry = setterMap.get(methodName);
              
              if (entry == null || type.getPriority() < entry.type.getPriority()) {
                setterMap.put(methodName, new SetterEntry(methodName, type, position));
              }
            }
          }
        }
      }
    }
  }
}
