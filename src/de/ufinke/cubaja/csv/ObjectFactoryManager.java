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
    
    SetterEntry(String methodName, ObjectFactoryType parameterType, int position) {
      
      this.name = methodName;
      this.type = parameterType;
      this.position = position;
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
  static private final Type objectFactoryType = new Type(ObjectFactory.class);
  static private final Type csvReaderType = new Type(CsvReader.class);
  static private final Type exceptionType = new Type(Exception.class);

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
    method.addException(exceptionType);
    generateCode(method.getCode());    
    
    return genClass;
  }
  
  private void generateCode(CodeAttribute code) {
    
    code.newObject(dataClassType);
    code.duplicate();
    code.invokeSpecial(dataClassType, voidType, "<init>");
    
    for (SetterEntry setter : setterMap.values()) {
      code.duplicate();
      code.loadLocalReference(1);
      code.loadConstant(setter.position);
      code.invokeVirtual(csvReaderType, setter.type.getType(), setter.type.getReaderMethod(), intType);
      code.invokeVirtual(dataClassType, voidType, setter.name, setter.type.getType());
    }
    
    code.returnReference();
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
                sm.put(methodName, new SetterEntry(methodName, type, position));
              }
            }
          }
        }
      }
    }
    
    return sm;
  }
}
