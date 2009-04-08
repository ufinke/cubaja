// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import de.ufinke.cubaja.cafebabe.Type;

enum BinaryStreamParameter {

  BOOLEAN(Boolean.TYPE, true, false, "Boolean"),
  BYTE(Byte.TYPE, true, false, "Byte"),
  SHORT(Short.TYPE, true, false, "Short"),
  CHAR(Character.TYPE, true, false, "Char"),
  INT(Integer.TYPE, true, false, "Int"),
  LONG(Long.TYPE, true, false, "Long"),
  FLOAT(Float.TYPE, true, false, "Float"),
  DOUBLE(Double.TYPE, true, false, "Double"),
  BOOLEAN_OBJECT(Boolean.class, false, false, "BooleanObject"),
  BYTE_OBJECT(Byte.class, false, false, "ByteObject"),
  SHORT_OBJECT(Short.class, false, false, "ShortObject"),
  CHAR_OBJECT(Character.class, false, false, "CharObject"),
  INT_OBJECT(Integer.class, false, false, "IntObject"),
  LONG_OBJECT(Long.class, false, false, "LongObject"),
  FLOAT_OBJECT(Float.class, false, false, "FloatObject"),
  DOUBLE_OBJECT(Double.class, false, false, "DoubleObject"),
  STRING(String.class, false, false, "String"),
  DATE(Date.class, false, false, "Date"),
  BIG_INTEGER(BigInteger.class, false, false, "BigInteger"),
  BIG_DECIMAL(BigDecimal.class, false, false, "BigDecimal"),
  BYTE_ARRAY(byte[].class, false, false, "ByteArray"),
  ENUM(Enum.class, false, true, "Enum"),
  OBJECT(Object.class, false, true, "Object");
  
  private Class<?> clazz;
  private Type type;
  private boolean primitive;
  private boolean needsClazz;
  private String writerMethod;
  private String readerMethod;
  
  private BinaryStreamParameter(Class<?> clazz, boolean primitive, boolean needsClazz, String method) {
    
    this.clazz = clazz;
    type = new Type(clazz);
    this.primitive = primitive;
    this.needsClazz = needsClazz;
    writerMethod = "write" + method;
    readerMethod = "read" + method;
  }
  
  Class<?> getClazz() {
    
    return clazz;
  }
  
  Type getType() {
    
    return type;
  }
  
  String getWriterMethod() {
    
    return writerMethod;
  }
  
  String getReaderMethod() {
    
    return readerMethod;
  }
  
  boolean isPrimitive() {
    
    return primitive;
  }
  
  boolean needsClazz() {
    
    return needsClazz;
  }
}
