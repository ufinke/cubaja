// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Ref;
import java.sql.Time;
import java.sql.Timestamp;
import de.ufinke.cubaja.cafebabe.Type;

class Types {

  static public Type T_BYTE_ARRAY = new Type(byte[].class);
  static public Type T_BYTE_OBJECT = new Type(Byte.class);
  static public Type T_SHORT_OBJECT = new Type(Short.class);
  static public Type T_CHAR_OBJECT = new Type(Character.class);
  static public Type T_INT_OBJECT = new Type(Integer.class);
  static public Type T_LONG_OBJECT = new Type(Long.class);
  static public Type T_FLOAT_OBJECT = new Type(Float.class);
  static public Type T_DOUBLE_OBJECT = new Type(Double.class);
  static public Type T_BOOLEAN_OBJECT = new Type(Boolean.class);
  static public Type T_BIG_DECIMAL = new Type(BigDecimal.class);
  static public Type T_BIG_INTEGER = new Type(BigInteger.class);
  static public Type T_UTIL_DATE = new Type(java.util.Date.class);
  static public Type T_SQL_DATE = new Type(java.sql.Date.class);
  static public Type T_TIME = new Type(Time.class);
  static public Type T_TIMESTAMP = new Type(Timestamp.class);
  static public Type T_READER = new Type(Reader.class);
  static public Type T_INPUT_STREAM = new Type(InputStream.class);
  static public Type T_BLOB = new Type(Blob.class);
  static public Type T_CLOB = new Type(Clob.class);
  static public Type T_ARRAY = new Type(Array.class);
  static public Type T_REF = new Type(Ref.class);
  static public Type T_URL = new Type(URL.class);
}
