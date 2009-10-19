// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import static de.ufinke.cubaja.cafebabe.AccessFlags.ACC_FINAL;
import static de.ufinke.cubaja.cafebabe.AccessFlags.ACC_PUBLIC;
import java.lang.reflect.Method;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import de.ufinke.cubaja.cafebabe.CodeAttribute;
import de.ufinke.cubaja.cafebabe.GenClass;
import de.ufinke.cubaja.cafebabe.GenMethod;
import de.ufinke.cubaja.cafebabe.Generator;
import de.ufinke.cubaja.cafebabe.Loader;
import de.ufinke.cubaja.cafebabe.Type;
import de.ufinke.cubaja.util.*;
import org.apache.commons.logging.*;

class ObjectFactoryGenerator implements Generator {

  static private class SearchEntry {
  
    String name;
    int position;
    int sqlType;
    boolean setterFound;
    
    SearchEntry(String name, int position, int sqlType) {
      
      this.name = name;
      this.position = position;
      this.sqlType = sqlType;
      setterFound = false;
    }
  }
  
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
  
  static private final Text text = new Text(ObjectFactoryGenerator.class);
  
  static private final Type objectType = new Type(Object.class);
  static private final Type voidType = new Type(Void.TYPE);
  static private final Type intType = new Type(Integer.TYPE);
  static private final Type objectFactoryType = new Type(ObjectFactory.class);
  static private final Type queryType = new Type(Query.class);
  static private final Type sqlExceptionType = new Type(SQLException.class);

  private Type dataClassType;
  private Map<String, SearchEntry> searchMap;
  private Map<String, SetterEntry> setterMap;
  private Map<Class<?>, ObjectFactory> factoryMap;
  private Class<?> lastClass;
  private ObjectFactory lastFactory;
  private DatabaseConfig config;
  private Log logger;
  
  ObjectFactoryGenerator(ResultSetMetaData metaData, DatabaseConfig config) throws SQLException {
  
    this.config = config;
    createSearchMap(metaData);
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
    
    Class<?> factoryClass = Loader.createClass(this, "QueryObjectFactory", dataClass);
    lastFactory = (ObjectFactory) factoryClass.newInstance();
    factoryMap.put(dataClass, lastFactory);
    
    setterMap = null;
    
    return lastFactory;
  }
  
  public GenClass generate(String className) throws Exception {

    GenClass genClass = new GenClass(ACC_PUBLIC | ACC_FINAL, className, objectType, objectFactoryType);
    
    genClass.createDefaultConstructor();
    
    GenMethod method = genClass.createMethod(ACC_PUBLIC, objectType, "createObject", queryType);
    method.addException(sqlExceptionType);
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
      code.invokeVirtual(queryType, parmType, type.getReaderMethod(), intType);
      
      code.invokeVirtual(dataClassType, voidType, setter.name, parmType); // operates on duplicated data object
    }
    
    code.returnReference(); // returns duplicated data object
  }  
  
  private void createSearchMap(ResultSetMetaData metaData) throws SQLException {
    
    int size = metaData.getColumnCount();
    
    searchMap = new HashMap<String, SearchEntry>(size << 1);
    
    for (int i = 1; i <= size; i++) {
      String name = metaData.getColumnName(i).toLowerCase();
      SearchEntry entry = new SearchEntry(name, i, metaData.getColumnType(i));
      searchMap.put(Util.createMethodName(name, "set"), entry);
    }
  }
  
  private void createSetterMap(Class<?> clazz) {
    
    setterMap = new HashMap<String, SetterEntry>();
    
    for (Method method : clazz.getMethods()) {
      
      if (method.getReturnType() == Void.TYPE) {
        
        String methodName = method.getName();
        SearchEntry searchEntry = searchMap.get(methodName);
        
        if (searchEntry != null && method.getReturnType() == Void.TYPE) {
          
          int position = searchEntry.position;
          Class<?>[] parameterTypes = method.getParameterTypes();
          
          if (parameterTypes.length == 1) {
            
            TypeCombination combination = new TypeCombination(searchEntry.sqlType, parameterTypes[0]);
            ObjectFactoryType type = ObjectFactoryType.getType(combination);
            
            if (type != null) {
              
              SetterEntry entry = setterMap.get(methodName);
              
              if (entry == null || type.getPriority() < entry.type.getPriority()) {
                setterMap.put(methodName, new SetterEntry(methodName, type, position, parameterTypes[0]));
              }
            }
          }
        }
      }
    }
    
    if (config.isLog()) {
      for (SearchEntry entry : searchMap.values()) {
        if (! entry.setterFound) {
          getLogger().debug(text.get("noSetter", clazz.getName(), entry.name));
        }
      }
    }
  }
  
  private Log getLogger() {
    
    if (logger == null) {
      logger = LogFactory.getLog(ObjectFactoryGenerator.class);
    }
    return logger;
  }
}
