// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import static de.ufinke.cubaja.cafebabe.AccessFlags.ACC_FINAL;
import static de.ufinke.cubaja.cafebabe.AccessFlags.ACC_PUBLIC;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import de.ufinke.cubaja.cafebabe.CodeAttribute;
import de.ufinke.cubaja.cafebabe.GenClass;
import de.ufinke.cubaja.cafebabe.GenMethod;
import de.ufinke.cubaja.cafebabe.Generator;
import de.ufinke.cubaja.cafebabe.Loader;
import de.ufinke.cubaja.cafebabe.Type;
import de.ufinke.cubaja.util.Util;

class ObjectFactoryManager implements Generator {

  static private class SetterEntry {
  
    String name;
    ObjectFactoryType type;
    int position;
    Class<?> clazz;
    
    SetterEntry(String methodName, ObjectFactoryType parameterType, int position, Class<?> parameterClass) {
      
      this.name = methodName;
      this.type = parameterType;
      this.position = position;
      this.clazz = parameterClass;
    }
  }
  
  static private final Map<Class<?>, ObjectFactory> factoryMap = new ConcurrentHashMap<Class<?>, ObjectFactory>();
  
  static ObjectFactory getFactory(Class<?> clazz, Map<String, Integer> nameMap) throws Exception {
    
    ObjectFactory factory = factoryMap.get(clazz);
    if (factory == null) {
      String className = Loader.createClassName(ObjectFactoryManager.class, "ObjectFactory", clazz);
      factory = (ObjectFactory) Loader.createInstance(className, new ObjectFactoryManager(clazz, nameMap));
      factoryMap.put(clazz, factory);
    }
    return factory;
  }

  static private final Type objectType = new Type(Object.class);
  static private final Type voidType = new Type(Void.TYPE);
  static private final Type intType = new Type(Integer.TYPE);
  static private final Type classType = new Type(Class.class);
  static private final Type objectFactoryType = new Type(ObjectFactory.class);
  static private final Type csvReaderType = new Type(CsvReader.class);
  static private final Type csvExceptionType = new Type(CsvException.class);

  private Type dataClassType;
  private Map<String, Integer> searchMap;
  private Map<String, SetterEntry> setterMap;
  
  private ObjectFactoryManager(Class<?> dataClass, Map<String, Integer> nameMap) {
  
    dataClassType = new Type(dataClass);
    searchMap = createSearchMap(nameMap);
    setterMap = createSetterMap(dataClass);
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
      
      code.duplicate();
      
      code.loadLocalReference(1);
      code.loadConstant(setter.position);
      if (type.needsClazz()) {
        parmType = new Type(setter.clazz);
        code.loadConstant(setter.clazz);
        code.invokeVirtual(csvReaderType, type.getType(), type.getReaderMethod(), intType, classType);
        code.cast(parmType);
      } else {
        code.invokeVirtual(csvReaderType, parmType, type.getReaderMethod(), intType);
      }
      
      code.invokeVirtual(dataClassType, voidType, setter.name, parmType); // operates on duplicated data object
    }
    
    code.returnReference(); // returns duplicated data object
  }  
  
  private Map<String, Integer> createSearchMap(Map<String, Integer> nameMap) {
    
    Map<String, Integer> sm = new HashMap<String, Integer>(nameMap.size() << 1);
    
    for (Map.Entry<String, Integer> entry : nameMap.entrySet()) {
      sm.put(Util.createMethodName(entry.getKey(), "set"), entry.getValue());
    }
    
    return sm;
  }
  
  private Map<String, SetterEntry> createSetterMap(Class<?> clazz) {
    
    Map<String, SetterEntry> sm = new HashMap<String, SetterEntry>();
    
    for (Method method : clazz.getMethods()) {
      
      if (method.getReturnType() == Void.TYPE) {
        
        String methodName = method.getName();
        Integer position = searchMap.get(methodName);
        
        if (position != null && method.getReturnType() == Void.TYPE) {
          
          Class<?>[] parameterTypes = method.getParameterTypes();
          
          if (parameterTypes.length == 1) {
            
            ObjectFactoryType type = ObjectFactoryType.getType(parameterTypes[0]);
            
            if (type != null) {
              
              SetterEntry entry = sm.get(methodName);
              
              if (entry == null || type.getPriority() < entry.type.getPriority()) {
                sm.put(methodName, new SetterEntry(methodName, type, position, parameterTypes[0]));
              }
            }
          }
        }
      }
    }
    
    return sm;
  }
}
