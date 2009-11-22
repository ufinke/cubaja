// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import de.ufinke.cubaja.cafebabe.Type;

enum VariableSetterType {

  ARRAY          (Array.class         , "Array"),
  BIG_DECIMAL    (BigDecimal.class    , "BigDecimal"),
  BIG_INTEGER    (BigInteger.class    , "BigInteger"),
  BLOB           (Blob.class          , "Blob"),
  BOOLEAN        (Boolean.TYPE        , "Boolean"),
  BOOLEAN_OBJECT (Boolean.class       , "Boolean"),
  BYTE           (Byte.TYPE           , "Byte"),
  BYTE_OBJECT    (Byte.class          , "Byte"),
  CHAR           (Character.TYPE      , "Char"),
  CHAR_OBJECT    (Character.class     , "Char"),
  CLOB           (Clob.class          , "Clob"),
  DATE           (java.sql.Date.class , "Date"),
  DOUBLE         (Double.TYPE         , "Double"),
  DOUBLE_OBJECT  (Double.class        , "Double"),
  FLOAT          (Float.TYPE          , "Float"),
  FLOAT_OBJECT   (Float.class         , "Float"),
  INT            (Integer.TYPE        , "Int"),
  INT_OBJECT     (Integer.class       , "Int"),
  LONG           (Long.TYPE           , "Long"),
  LONG_OBJECT    (Long.class          , "Long"),
  OBJECT         (Object.class        , "Object"),
  REF            (Ref.class           , "Ref"),
  SHORT          (Short.TYPE          , "Short"),
  SHORT_OBJECT   (Short.class         , "Short"),
  STRING         (String.class        , "String"),
  TIME           (Time.class          , "Time"),
  TIMESTAMP      (Timestamp.class     , "Timestamp"),
  UTIL_DATE      (java.util.Date.class, "Timestamp"),
  URL            (URL.class           , "URL");
  
  private Class<?> setterType;
  private String setterMethod;
  private Type genType;
  
  private VariableSetterType(Class<?> setterType, String setterMethod) {
  
    this.setterType = setterType;
    this.setterMethod = "set" + setterMethod;
    genType = new Type(setterType);
  }
  
  public Class<?> getSetterType() {
    
    return setterType;
  }
  
  public String getSetterMethod() {
    
    return setterMethod;
  }
  
  public Type getGenType() {
    
    return genType;
  }
  
//--- parameter finder -------------------------------------------------------
  
  static private final Map<Class<?>, VariableSetterType> map = createMap();
  
  static Map<Class<?>, VariableSetterType> createMap() {
    
    Map<Class<?>, VariableSetterType> map = new HashMap<Class<?>, VariableSetterType>(64);
    
    for (VariableSetterType type : VariableSetterType.values()) {
      map.put(type.getSetterType(), type);
    }
    
    return map;
  }
  
  static VariableSetterType getType(Class<?> parm) {
    
    return map.get(parm);
  }
}
