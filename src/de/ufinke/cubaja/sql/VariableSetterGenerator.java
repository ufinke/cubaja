// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import static de.ufinke.cubaja.cafebabe.AccessFlags.ACC_FINAL;
import static de.ufinke.cubaja.cafebabe.AccessFlags.ACC_PUBLIC;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.cafebabe.CodeAttribute;
import de.ufinke.cubaja.cafebabe.GenClass;
import de.ufinke.cubaja.cafebabe.GenMethod;
import de.ufinke.cubaja.cafebabe.Generator;
import de.ufinke.cubaja.cafebabe.Loader;
import de.ufinke.cubaja.cafebabe.Type;
import de.ufinke.cubaja.util.Util;

class VariableSetterGenerator implements Generator {

  static private class ListEntry {
  
    String getterMethod;
    int position;
    VariableSetterType type;
    
    ListEntry(String name, int position, VariableSetterType type) {
      
      this.getterMethod = name;
      this.position = position;
      this.type = type;
    }
  }
  
  static private final Type objectType = new Type(Object.class);
  static private final Type voidType = new Type(Void.TYPE);
  static private final Type intType = new Type(Integer.TYPE);
  static private final Type exceptionType = new Type(Exception.class);
  static private final Type variableSetterType = new Type(VariableSetter.class);
  static private final Type preparedSqlType = new Type(PreparedSql.class);
  
  private Map<Class<?>, VariableSetter> setterMap;
  private Map<String, Integer> variableMap;
  private List<ListEntry> list;
  private Type dataClassType;
  
  VariableSetterGenerator(List<String> variableList) {

    createVariableMap(variableList);
    setterMap = new HashMap<Class<?>, VariableSetter>();
  }

  VariableSetter getSetter(Class<?> dataClass) throws Exception {
    
    VariableSetter setter = setterMap.get(dataClass);
    if (setter != null) {
      return setter;
    }
    
    dataClassType = new Type(dataClass);
    createList(dataClass);
    
    Class<?> setterClass = Loader.createClass(this, "PreparedSqlVariableSetter", dataClass);
    setter = (VariableSetter) setterClass.newInstance();
    setterMap.put(dataClass, setter);
    
    return setter;
  }
  
  private void createVariableMap(List<String> variableList) {
    
    variableMap = new HashMap<String, Integer>();
    
    for (int i = 1; i < variableList.size(); i++) {
      String methodName = Util.createMethodName(variableList.get(i), "get");
      variableMap.put(methodName, i);
    }
  }
  
  private void createList (Class<?> dataClass) {
    
    list = new ArrayList<ListEntry>();
    
    for (Method method : dataClass.getMethods()) {
      if (method.getParameterTypes().length == 0) {
        String name = method.getName();
        Class<?> returnType = method.getReturnType();
        Integer position = variableMap.get(name);
        if (position == null && returnType == Boolean.TYPE && name.startsWith("is")) {
          position = variableMap.get("get" + name.substring(2));
        }
        if (position != null) {
          VariableSetterType type = VariableSetterType.getType(returnType);
          if (type != null) {
            list.add(new ListEntry(name, position, type));
          }
        }
      }
    }
  }
  
  public GenClass generate(String className) throws Exception {

    GenClass genClass = new GenClass(ACC_PUBLIC | ACC_FINAL, className, objectType, variableSetterType);
    
    genClass.createDefaultConstructor();
    
    GenMethod method = genClass.createMethod(ACC_PUBLIC, voidType, "setVariables", preparedSqlType, objectType);
    method.addException(exceptionType);
    generateCode(method.getCode());
    
    return genClass;
  }
  
  private void generateCode(CodeAttribute code) {
    
    code.loadLocalReference(2); // dataObject
    code.cast(dataClassType);
    code.storeLocalReference(3);
    
    for (ListEntry entry : list) {
      VariableSetterType type = entry.type;
      code.loadLocalReference(1); // preparedSql
      code.loadConstant(entry.position);
      code.loadLocalReference(3);
      code.invokeVirtual(dataClassType, type.getGenType(), entry.getterMethod); // get
      code.invokeVirtual(preparedSqlType, voidType, type.getSetterMethod(), intType, type.getGenType()); // set
    }
    
    code.returnVoid();
  }
  
}
