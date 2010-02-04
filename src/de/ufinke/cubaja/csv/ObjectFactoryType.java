// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import de.ufinke.cubaja.cafebabe.Type;

enum ObjectFactoryType {

  // CONSTANT(clazz, isPrimitive, needsClass, methodName, priority)
  BOOLEAN       (Boolean.TYPE    , true , false, "Boolean"      , 19),
  BYTE          (Byte.TYPE       , true , false, "Byte"         , 18),
  SHORT         (Short.TYPE      , true , false, "Short"        , 17),
  CHAR          (Character.TYPE  , true , false, "Char"         , 16),
  INT           (Integer.TYPE    , true , false, "Int"          , 15),
  LONG          (Long.TYPE       , true , false, "Long"         , 14),
  FLOAT         (Float.TYPE      , true , false, "Float"        , 13),
  DOUBLE        (Double.TYPE     , true , false, "Double"       , 12),
  BOOLEAN_OBJECT(Boolean.class   , false, false, "BooleanObject", 11),
  BYTE_OBJECT   (Byte.class      , false, false, "ByteObject"   , 10),
  SHORT_OBJECT  (Short.class     , false, false, "ShortObject"  ,  9),
  CHAR_OBJECT   (Character.class , false, false, "CharObject"   ,  8),
  INT_OBJECT    (Integer.class   , false, false, "IntObject"    ,  7),
  LONG_OBJECT   (Long.class      , false, false, "LongObject"   ,  6),
  FLOAT_OBJECT  (Float.class     , false, false, "FloatObject"  ,  5),
  DOUBLE_OBJECT (Double.class    , false, false, "DoubleObject" ,  4),
  STRING        (String.class    , false, false, "String"       ,  1),
  DATE          (Date.class      , false, false, "Date"         , 20),
  BIG_INTEGER   (BigInteger.class, false, false, "BigInteger"   ,  3),
  BIG_DECIMAL   (BigDecimal.class, false, false, "BigDecimal"   ,  2),
  ENUM          (Enum.class      , false, true , "Enum"         , 21);
  
  private Class<?> clazz;
  private Type type;
  private boolean primitive;
  private boolean needsClass;
  private String readerMethod;
  private int priority;
  
  private ObjectFactoryType(Class<?> clazz, boolean primitive, boolean needsClass, String method, int priority) {
    
    this.clazz = clazz;
    type = new Type(clazz);
    this.primitive = primitive;
    this.needsClass = needsClass;
    readerMethod = "read" + method;
    this.priority = priority;
  }
  
  Class<?> getClazz() {
    
    return clazz;
  }
  
  Type getType() {
    
    return type;
  }
  
  String getReaderMethod() {
    
    return readerMethod;
  }
  
  boolean isPrimitive() {
    
    return primitive;
  }
  
  boolean needsClass() {
    
    return needsClass;
  }
  
  int getPriority() {
    
    return priority;
  }
  
// --- parameter finder -------------------------------------------------------
  
  static private final Map<Class<?>, ObjectFactoryType> parameterMap = createParameterMap();
  
  static private Map<Class<?>, ObjectFactoryType> createParameterMap() {
    
    Map<Class<?>, ObjectFactoryType> map = new HashMap<Class<?>, ObjectFactoryType>(32);
    
    for (ObjectFactoryType type : ObjectFactoryType.values()) {
      map.put(type.getClazz(), type);
    }
    
    return map;
  }
  
  static ObjectFactoryType getType(Class<?> parameterClazz) {
    
    if (parameterClazz.isEnum()) {
      return ENUM;
    }
    
    return parameterMap.get(parameterClazz);
  }
  
}
