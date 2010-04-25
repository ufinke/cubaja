// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.lang.reflect.Method;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.cafebabe.CodeAttribute;
import de.ufinke.cubaja.cafebabe.GenClass;
import de.ufinke.cubaja.cafebabe.GenMethod;
import de.ufinke.cubaja.cafebabe.Generator;
import de.ufinke.cubaja.cafebabe.Loader;
import de.ufinke.cubaja.cafebabe.Type;
import de.ufinke.cubaja.util.Text;
import de.ufinke.cubaja.util.Util;
import de.ufinke.cubaja.util.WarnMode;

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
    
    SetterEntry(String methodName, ObjectFactoryType parameterType, int position) {
      
      this.name = methodName;
      this.type = parameterType;
      this.position = position;
    }
  }
  
  static private final Text text = new Text(ObjectFactoryGenerator.class);
  static private final Log logger = LogFactory.getLog(ObjectFactoryGenerator.class);
  
  static private final Type objectType = new Type(Object.class);
  static private final Type voidType = new Type(Void.TYPE);
  static private final Type intType = new Type(Integer.TYPE);
  static private final Type objectFactoryType = new Type(ObjectFactory.class);
  static private final Type queryType = new Type(Query.class);
  static private final Type sqlExceptionType = new Type(SQLException.class);

  private Type dataClassType;
  private int singleColumnSqlType;
  private ObjectFactoryType builtin;
  private Map<String, SearchEntry> searchMap;
  private Map<String, SetterEntry> setterMap;
  private Map<Class<?>, ObjectFactory> factoryMap;
  
  ObjectFactoryGenerator(ResultSetMetaData metaData) throws SQLException {
  
    createSearchMap(metaData);
    factoryMap = new HashMap<Class<?>, ObjectFactory>();
  }
  
  ObjectFactory getFactory(Class<?> dataClass, WarnMode warnMode) throws Exception {

    ObjectFactory factory = factoryMap.get(dataClass);
    if (factory != null) {
      return factory;
    }
    
    if (dataClass.isPrimitive()) {
      throw new SQLException(text.get("primitive"));
    }
    
    dataClassType = new Type(dataClass);
    
    builtin = null;
    if (singleColumnSqlType != 0) {
      TypeCombination combination = new TypeCombination(singleColumnSqlType, dataClass);
      builtin = ObjectFactoryType.getType(combination);
    }    
    if (builtin == null) {
      createSetterMap(dataClass);
      checkSetterMap(dataClass, warnMode);
    }
    
    Class<?> factoryClass = Loader.createClass(this, "QueryObjectFactory", dataClass);
    factory = (ObjectFactory) factoryClass.newInstance();
    factoryMap.put(dataClass, factory);
    
    setterMap = null;
    
    return factory;
  }
  
  public GenClass generate(String className) throws Exception {

    GenClass genClass = new GenClass(ACC_PUBLIC | ACC_FINAL, className, objectType, objectFactoryType);
    
    genClass.createDefaultConstructor();
    
    GenMethod method = genClass.createMethod(ACC_PUBLIC, objectType, "createObject", queryType);
    method.addException(sqlExceptionType);
    
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
  
  private void generateBuiltin(CodeAttribute code) {
    
    code.loadLocalReference(1); // query
    code.loadConstant(1); // column #1
    code.invokeVirtual(queryType, dataClassType, builtin.getReaderMethod(), intType);
    code.returnReference();
  }
  
  private void createSearchMap(ResultSetMetaData metaData) throws SQLException {
    
    int size = metaData.getColumnCount();
    
    searchMap = new HashMap<String, SearchEntry>();
    
    for (int i = 1; i <= size; i++) {
      String name = metaData.getColumnName(i).toLowerCase();
      SearchEntry entry = new SearchEntry(name, i, metaData.getColumnType(i));
      searchMap.put(Util.createMethodName(name, "set"), entry);
    }
    
    if (searchMap.size() == 1) {
      singleColumnSqlType = metaData.getColumnType(1);
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
                setterMap.put(methodName, new SetterEntry(methodName, type, position));
                searchEntry.setterFound = true;
              }
            }
          }
        }
      }
    }
  }
  
  private void checkSetterMap(Class<?> clazz, WarnMode warnMode) throws SQLException {
    
    if (warnMode == WarnMode.IGNORE) {
      return;
    }
    
    for (SearchEntry entry : searchMap.values()) {
      if (! entry.setterFound) {
        switch (warnMode) {
          case WARN:
            logger.warn(text.get("noSetter", clazz.getName(), entry.name));
            break;
          case ERROR:
            throw new SQLException(text.get("noSetter", clazz.getName(), entry.name));
        }
      }
    }
  }
}
