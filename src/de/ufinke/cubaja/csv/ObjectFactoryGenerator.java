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
      
      Type readerType = setter.readerType.getType();
      String readerMethod = setter.readerType.getReaderMethod();
      
      code.duplicate(); // data object with setter method
      
      code.loadLocalReference(1); // CsvReader
      code.loadConstant(setter.position);
      if (setter.readerType.needsClass()) {
        code.loadConstant(setter.dataType);
        code.invokeVirtual(csvReaderType, readerType, readerMethod, intType, clazzType);
        code.cast(setter.dataType);
        code.invokeVirtual(dataClassType, voidType, setter.name, setter.dataType);
      } else {
        code.invokeVirtual(csvReaderType, readerType, readerMethod, intType);
        code.invokeVirtual(dataClassType, voidType, setter.name, setter.dataType);
      }
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
