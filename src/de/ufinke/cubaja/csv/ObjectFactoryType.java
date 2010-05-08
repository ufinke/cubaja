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

  // CONSTANT(clazz, type, isPrimitive, needsClass, methodName, priority)
  BOOLEAN       (Boolean.TYPE    , Type.BOOLEAN              , true , false, "readBoolean"      , 19),
  BYTE          (Byte.TYPE       , Type.BYTE                 , true , false, "readByte"         , 18),
  SHORT         (Short.TYPE      , Type.SHORT                , true , false, "readShort"        , 17),
  CHAR          (Character.TYPE  , Type.CHAR                 , true , false, "readChar"         , 16),
  INT           (Integer.TYPE    , Type.INT                  , true , false, "readInt"          , 15),
  LONG          (Long.TYPE       , Type.LONG                 , true , false, "readLong"         , 14),
  FLOAT         (Float.TYPE      , Type.FLOAT                , true , false, "readFloat"        , 13),
  DOUBLE        (Double.TYPE     , Type.DOUBLE               , true , false, "readDouble"       , 12),
  BOOLEAN_OBJECT(Boolean.class   , new Type(Boolean.class)   , false, false, "readBooleanObject", 11),
  BYTE_OBJECT   (Byte.class      , new Type(Byte.class)      , false, false, "readByteObject"   , 10),
  SHORT_OBJECT  (Short.class     , new Type(Short.class)     , false, false, "readShortObject"  ,  9),
  CHAR_OBJECT   (Character.class , new Type(Character.class) , false, false, "readCharObject"   ,  8),
  INT_OBJECT    (Integer.class   , new Type(Integer.class)   , false, false, "readIntObject"    ,  7),
  LONG_OBJECT   (Long.class      , new Type(Long.class)      , false, false, "readLongObject"   ,  6),
  FLOAT_OBJECT  (Float.class     , new Type(Float.class)     , false, false, "readFloatObject"  ,  5),
  DOUBLE_OBJECT (Double.class    , new Type(Double.class)    , false, false, "readDoubleObject" ,  4),
  STRING        (String.class    , Type.STRING               , false, false, "readString"       ,  1),
  DATE          (Date.class      , new Type(Date.class)      , false, false, "readDate"         , 20),
  BIG_INTEGER   (BigInteger.class, new Type(BigInteger.class), false, false, "readBigInteger"   ,  3),
  BIG_DECIMAL   (BigDecimal.class, new Type(BigDecimal.class), false, false, "readBigDecimal"   ,  2),
  ENUM          (Enum.class      , new Type(Enum.class)      , false, true , "readEnum"         , 21);
  
  private Class<?> clazz;
  private Type type;
  private boolean primitive;
  private boolean needsClass;
  private String method;
  private int priority;
  
  private ObjectFactoryType(Class<?> clazz, Type type, boolean primitive, boolean needsClass, String method, int priority) {
    
    this.clazz = clazz;
    this.type = type;
    this.primitive = primitive;
    this.needsClass = needsClass;
    this.method = method;
    this.priority = priority;
  }
  
  Class<?> getClazz() {
    
    return clazz;
  }
  
  Type getType() {
    
    return type;
  }
  
  String getReaderMethod() {
    
    return method;
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
  
  static private final Map<Class<?>, ObjectFactoryType> parameterMap;
  
  static {
    
    parameterMap = new HashMap<Class<?>, ObjectFactoryType>(32);
    
    for (ObjectFactoryType type : ObjectFactoryType.values()) {
      parameterMap.put(type.getClazz(), type);
    }
  }
  
  static ObjectFactoryType getType(Class<?> parameterClazz) {
    
    if (parameterClazz.isEnum()) {
      return ENUM;
    }
    
    return parameterMap.get(parameterClazz);
  }
  
}
