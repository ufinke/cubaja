// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.csv;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import de.ufinke.cubaja.cafebabe.Type;

enum ObjectFactoryType {

  // CONSTANT(clazz, isPrimitive, methodName, priority)
  BOOLEAN       (Boolean.TYPE    , true , "Boolean"      , 19),
  BYTE          (Byte.TYPE       , true , "Byte"         , 18),
  SHORT         (Short.TYPE      , true , "Short"        , 17),
  CHAR          (Character.TYPE  , true , "Char"         , 16),
  INT           (Integer.TYPE    , true , "Int"          , 15),
  LONG          (Long.TYPE       , true , "Long"         , 14),
  FLOAT         (Float.TYPE      , true , "Float"        , 13),
  DOUBLE        (Double.TYPE     , true , "Double"       , 12),
  BOOLEAN_OBJECT(Boolean.class   , false, "BooleanObject", 11),
  BYTE_OBJECT   (Byte.class      , false, "ByteObject"   , 10),
  SHORT_OBJECT  (Short.class     , false, "ShortObject"  ,  9),
  CHAR_OBJECT   (Character.class , false, "CharObject"   ,  8),
  INT_OBJECT    (Integer.class   , false, "IntObject"    ,  7),
  LONG_OBJECT   (Long.class      , false, "LongObject"   ,  6),
  FLOAT_OBJECT  (Float.class     , false, "FloatObject"  ,  5),
  DOUBLE_OBJECT (Double.class    , false, "DoubleObject" ,  4),
  STRING        (String.class    , false, "String"       ,  1),
  DATE          (Date.class      , false, "Date"         , 20),
  BIG_INTEGER   (BigInteger.class, false, "BigInteger"   ,  3),
  BIG_DECIMAL   (BigDecimal.class, false, "BigDecimal"   ,  2);
  
  private Class<?> clazz;
  private Type type;
  private boolean primitive;
  private String readerMethod;
  private int priority;
  
  private ObjectFactoryType(Class<?> clazz, boolean primitive, String method, int priority) {
    
    this.clazz = clazz;
    type = new Type(clazz);
    this.primitive = primitive;
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
  
  int getPriority() {
    
    return priority;
  }
  
// --- parameter finder -------------------------------------------------------
  
  static private final Map<Class<?>, ObjectFactoryType> parameterMap = createParameterMap();
  
  static private Map<Class<?>, ObjectFactoryType> createParameterMap() {
    
    Map<Class<?>, ObjectFactoryType> map = new HashMap<Class<?>, ObjectFactoryType>();
    
    for (ObjectFactoryType type : ObjectFactoryType.values()) {
      map.put(type.getClazz(), type);
    }
    
    return map;
  }
  
  static ObjectFactoryType getType(Class<?> parameterClazz) {
    
    return parameterMap.get(parameterClazz);
  }
  
}
