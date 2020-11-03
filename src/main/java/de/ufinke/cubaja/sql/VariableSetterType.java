// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import static de.ufinke.cubaja.sql.Types.T_ARRAY;
import static de.ufinke.cubaja.sql.Types.T_BIG_DECIMAL;
import static de.ufinke.cubaja.sql.Types.T_BIG_INTEGER;
import static de.ufinke.cubaja.sql.Types.T_BLOB;
import static de.ufinke.cubaja.sql.Types.T_BOOLEAN_OBJECT;
import static de.ufinke.cubaja.sql.Types.T_BYTE_OBJECT;
import static de.ufinke.cubaja.sql.Types.T_CHAR_OBJECT;
import static de.ufinke.cubaja.sql.Types.T_CLOB;
import static de.ufinke.cubaja.sql.Types.T_DOUBLE_OBJECT;
import static de.ufinke.cubaja.sql.Types.T_FLOAT_OBJECT;
import static de.ufinke.cubaja.sql.Types.T_INT_OBJECT;
import static de.ufinke.cubaja.sql.Types.T_LONG_OBJECT;
import static de.ufinke.cubaja.sql.Types.T_REF;
import static de.ufinke.cubaja.sql.Types.T_SHORT_OBJECT;
import static de.ufinke.cubaja.sql.Types.T_SQL_DATE;
import static de.ufinke.cubaja.sql.Types.T_TIME;
import static de.ufinke.cubaja.sql.Types.T_TIMESTAMP;
import static de.ufinke.cubaja.sql.Types.T_URL;
import static de.ufinke.cubaja.sql.Types.T_UTIL_DATE;
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

  ARRAY          (Array.class         , T_ARRAY         , "setArray"),
  BIG_DECIMAL    (BigDecimal.class    , T_BIG_DECIMAL   , "setBigDecimal"),
  BIG_INTEGER    (BigInteger.class    , T_BIG_INTEGER   , "setBigInteger"),
  BLOB           (Blob.class          , T_BLOB          , "setBlob"),
  BOOLEAN        (Boolean.TYPE        , Type.BOOLEAN    , "setBoolean"),
  BOOLEAN_OBJECT (Boolean.class       , T_BOOLEAN_OBJECT, "setBoolean"),
  BYTE           (Byte.TYPE           , Type.BYTE       , "setByte"),
  BYTE_OBJECT    (Byte.class          , T_BYTE_OBJECT   , "setByte"),
  CHAR           (Character.TYPE      , Type.CHAR       , "setChar"),
  CHAR_OBJECT    (Character.class     , T_CHAR_OBJECT   , "setChar"),
  CLOB           (Clob.class          , T_CLOB          , "setClob"),
  DATE           (java.sql.Date.class , T_SQL_DATE      , "setDate"),
  DOUBLE         (Double.TYPE         , Type.DOUBLE     , "setDouble"),
  DOUBLE_OBJECT  (Double.class        , T_DOUBLE_OBJECT , "setDouble"),
  FLOAT          (Float.TYPE          , Type.FLOAT      , "setFloat"),
  FLOAT_OBJECT   (Float.class         , T_FLOAT_OBJECT  , "setFloat"),
  INT            (Integer.TYPE        , Type.INT        , "setInt"),
  INT_OBJECT     (Integer.class       , T_INT_OBJECT    , "setInt"),
  LONG           (Long.TYPE           , Type.LONG       , "setLong"),
  LONG_OBJECT    (Long.class          , T_LONG_OBJECT   , "setLong"),
  OBJECT         (Object.class        , Type.OBJECT     , "setObject"),
  REF            (Ref.class           , T_REF           , "setRef"),
  SHORT          (Short.TYPE          , Type.SHORT      , "setShort"),
  SHORT_OBJECT   (Short.class         , T_SHORT_OBJECT  , "setShort"),
  STRING         (String.class        , Type.STRING     , "setString"),
  TIME           (Time.class          , T_TIME          , "setTime"),
  TIMESTAMP      (Timestamp.class     , T_TIMESTAMP     , "setTimestamp"),
  UTIL_DATE      (java.util.Date.class, T_UTIL_DATE     , "setTimestamp"),
  URL            (URL.class           , T_URL           , "setURL");
  
  private Class<?> clazz;
  private String method;
  private Type type;
  
  private VariableSetterType(Class<?> clazz, Type type, String method) {
  
    this.clazz = clazz;
    this.type = type;
    this.method = method;
  }
  
  public String getSetterMethod() {
    
    return method;
  }
  
  public Type getGenType() {
    
    return type;
  }
  
//--- parameter finder -------------------------------------------------------
  
  static private final Map<Class<?>, VariableSetterType> map;
  
  static {
    
    map = new HashMap<Class<?>, VariableSetterType>(64);
    
    for (VariableSetterType type : VariableSetterType.values()) {
      map.put(type.clazz, type);
    }
  }
  
  static VariableSetterType getType(Class<?> parm) {
    
    return map.get(parm);
  }
}
