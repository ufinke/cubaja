// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.sql;

import static java.sql.Types.ARRAY;
import static java.sql.Types.BIGINT;
import static java.sql.Types.BINARY;
import static java.sql.Types.BIT;
import static java.sql.Types.BLOB;
import static java.sql.Types.BOOLEAN;
import static java.sql.Types.CHAR;
import static java.sql.Types.CLOB;
import static java.sql.Types.DATALINK;
import static java.sql.Types.DATE;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.JAVA_OBJECT;
import static java.sql.Types.LONGVARBINARY;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.NUMERIC;
import static java.sql.Types.REAL;
import static java.sql.Types.REF;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.STRUCT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.TINYINT;
import static java.sql.Types.VARBINARY;
import static java.sql.Types.VARCHAR;
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
import java.util.HashMap;
import java.util.Map;
import de.ufinke.cubaja.cafebabe.Type;
import static de.ufinke.cubaja.sql.Types.*;

enum ObjectFactoryType {

  // CONSTANT(sqlType, setterParameterClass, queryReadMethodName, priorityWithinSqlType)
  TINYINT_BYTE_OBJECT       (TINYINT      , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     ,  1),
  TINYINT_BYTE              (TINYINT      , Byte.TYPE           , Type.BYTE       , "readByte"           ,  2),
  TINYINT_SHORT_OBJECT      (TINYINT      , Short.class         , T_SHORT_OBJECT  , "readShortObject"    ,  3),
  TINYINT_SHORT             (TINYINT      , Short.TYPE          , Type.SHORT      , "readShort"          ,  4),
  TINYINT_INT_OBJECT        (TINYINT      , Integer.class       , T_INT_OBJECT    , "readIntObject"      ,  5),
  TINYINT_INT               (TINYINT      , Integer.TYPE        , Type.INT        , "readInt"            ,  6),
  TINYINT_LONG_OBJECT       (TINYINT      , Long.class          , T_LONG_OBJECT   , "readLongObject"     ,  7),
  TINYINT_LONG              (TINYINT      , Long.TYPE           , Type.LONG       , "readLong"           ,  8),
  TINYINT_FLOAT_OBJECT      (TINYINT      , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    , 13),
  TINYINT_FLOAT             (TINYINT      , Float.TYPE          , Type.FLOAT      , "readFloat"          , 14),
  TINYINT_DOUBLE_OBJECT     (TINYINT      , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   , 11),
  TINYINT_DOUBLE            (TINYINT      , Double.TYPE         , Type.DOUBLE     , "readDouble"         , 12),
  TINYINT_BIGINTEGER        (TINYINT      , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     ,  9),
  TINYINT_BIGDECIMAL        (TINYINT      , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     , 10),
  TINYINT_BOOLEAN_OBJECT    (TINYINT      , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  , 15),
  TINYINT_BOOLEAN           (TINYINT      , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        , 16),
  TINYINT_STRING            (TINYINT      , String.class        , Type.STRING     , "readString"         , 17),
  TINYINT_OBJECT            (TINYINT      , Object.class        , Type.OBJECT     , "readObject"         , 99),
  SMALLINT_BYTE_OBJECT      (SMALLINT     , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     ,  9),
  SMALLINT_BYTE             (SMALLINT     , Byte.TYPE           , Type.BYTE       , "readByte"           , 10),
  SMALLINT_SHORT_OBJECT     (SMALLINT     , Short.class         , T_SHORT_OBJECT  , "readShortObject"    ,  1),
  SMALLINT_SHORT            (SMALLINT     , Short.TYPE          , Type.SHORT      , "readShort"          ,  2),
  SMALLINT_INT_OBJECT       (SMALLINT     , Integer.class       , T_INT_OBJECT    , "readIntObject"      ,  3),
  SMALLINT_INT              (SMALLINT     , Integer.TYPE        , Type.INT        , "readInt"            ,  4),
  SMALLINT_LONG_OBJECT      (SMALLINT     , Long.class          , T_LONG_OBJECT   , "readLongObject"     ,  5),
  SMALLINT_LONG             (SMALLINT     , Long.TYPE           , Type.LONG       , "readLong"           ,  6),
  SMALLINT_FLOAT_OBJECT     (SMALLINT     , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    , 13),
  SMALLINT_FLOAT            (SMALLINT     , Float.TYPE          , Type.FLOAT      , "readFloat"          , 14),
  SMALLINT_DOUBLE_OBJECT    (SMALLINT     , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   , 11),
  SMALLINT_DOUBLE           (SMALLINT     , Double.TYPE         , Type.DOUBLE     , "readDouble"         , 12),
  SMALLINT_BIGINTEGER       (SMALLINT     , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     ,  7),
  SMALLINT_BIGDECIMAL       (SMALLINT     , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     ,  8),
  SMALLINT_BOOLEAN_OBJECT   (SMALLINT     , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  , 15),
  SMALLINT_BOOLEAN          (SMALLINT     , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        , 16),
  SMALLINT_STRING           (SMALLINT     , String.class        , Type.STRING     , "readString"         , 17),
  SMALLINT_OBJECT           (SMALLINT     , Object.class        , Type.OBJECT     , "readObject"         , 99),
  INTEGER_BYTE_OBJECT       (INTEGER      , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     ,  9),
  INTEGER_BYTE              (INTEGER      , Byte.TYPE           , Type.BYTE       , "readByte"           , 10),
  INTEGER_SHORT_OBJECT      (INTEGER      , Short.class         , T_SHORT_OBJECT  , "readShortObject"    ,  7),
  INTEGER_SHORT             (INTEGER      , Short.TYPE          , Type.SHORT      , "readShort"          ,  8),
  INTEGER_INT_OBJECT        (INTEGER      , Integer.class       , T_INT_OBJECT    , "readIntObject"      ,  1),
  INTEGER_INT               (INTEGER      , Integer.TYPE        , Type.INT        , "readInt"            ,  2),
  INTEGER_LONG_OBJECT       (INTEGER      , Long.class          , T_LONG_OBJECT   , "readLongObject"     ,  3),
  INTEGER_LONG              (INTEGER      , Long.TYPE           , Type.LONG       , "readLong"           ,  4),
  INTEGER_FLOAT_OBJECT      (INTEGER      , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    , 13),
  INTEGER_FLOAT             (INTEGER      , Float.TYPE          , Type.FLOAT      , "readFloat"          , 14),
  INTEGER_DOUBLE_OBJECT     (INTEGER      , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   , 11),
  INTEGER_DOUBLE            (INTEGER      , Double.TYPE         , Type.DOUBLE     , "readDouble"         , 12),
  INTEGER_BIGINTEGER        (INTEGER      , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     ,  5),
  INTEGER_BIGDECIMAL        (INTEGER      , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     ,  6),
  INTEGER_BOOLEAN_OBJECT    (INTEGER      , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  , 15),
  INTEGER_BOOLEAN           (INTEGER      , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        , 16),
  INTEGER_STRING            (INTEGER      , String.class        , Type.STRING     , "readString"         , 17),
  INTEGER_OBJECT            (INTEGER      , Object.class        , Type.OBJECT     , "readObject"         , 99),
  BIGINT_BYTE_OBJECT        (BIGINT       , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     ,  9),
  BIGINT_BYTE               (BIGINT       , Byte.TYPE           , Type.BYTE       , "readByte"           , 10),
  BIGINT_SHORT_OBJECT       (BIGINT       , Short.class         , T_SHORT_OBJECT  , "readShortObject"    ,  7),
  BIGINT_SHORT              (BIGINT       , Short.TYPE          , Type.SHORT      , "readShort"          ,  8),
  BIGINT_INT_OBJECT         (BIGINT       , Integer.class       , T_INT_OBJECT    , "readIntObject"      ,  5),
  BIGINT_INT                (BIGINT       , Integer.TYPE        , Type.INT        , "readInt"            ,  6),
  BIGINT_LONG_OBJECT        (BIGINT       , Long.class          , T_LONG_OBJECT   , "readLongObject"     ,  1),
  BIGINT_LONG               (BIGINT       , Long.TYPE           , Type.LONG       , "readLong"           ,  2),
  BIGINT_FLOAT_OBJECT       (BIGINT       , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    , 13),
  BIGINT_FLOAT              (BIGINT       , Float.TYPE          , Type.FLOAT      , "readFloat"          , 14),
  BIGINT_DOUBLE_OBJECT      (BIGINT       , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   , 11),
  BIGINT_DOUBLE             (BIGINT       , Double.TYPE         , Type.DOUBLE     , "readDouble"         , 12),
  BIGINT_BIGINTEGER         (BIGINT       , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     ,  3),
  BIGINT_BIGDECIMAL         (BIGINT       , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     ,  4),
  BIGINT_BOOLEAN_OBJECT     (BIGINT       , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  , 15),
  BIGINT_BOOLEAN            (BIGINT       , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        , 16),
  BIGINT_STRING             (BIGINT       , String.class        , Type.STRING     , "readString"         , 17),
  BIGINT_OBJECT             (BIGINT       , Object.class        , Type.OBJECT     , "readObject"         , 99),
  REAL_BYTE_OBJECT          (REAL         , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     , 13),
  REAL_BYTE                 (REAL         , Byte.TYPE           , Type.BYTE       , "readByte"           , 14),
  REAL_SHORT_OBJECT         (REAL         , Short.class         , T_SHORT_OBJECT  , "readShortObject"    , 11),
  REAL_SHORT                (REAL         , Short.TYPE          , Type.SHORT      , "readShort"          , 12),
  REAL_INT_OBJECT           (REAL         , Integer.class       , T_INT_OBJECT    , "readIntObject"      ,  9),
  REAL_INT                  (REAL         , Integer.TYPE        , Type.INT        , "readInt"            , 10),
  REAL_LONG_OBJECT          (REAL         , Long.class          , T_LONG_OBJECT   , "readLongObject"     ,  7),
  REAL_LONG                 (REAL         , Long.TYPE           , Type.LONG       , "readLong"           ,  8),
  REAL_FLOAT_OBJECT         (REAL         , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    ,  1),
  REAL_FLOAT                (REAL         , Float.TYPE          , Type.FLOAT      , "readFloat"          ,  2),
  REAL_DOUBLE_OBJECT        (REAL         , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   ,  3),
  REAL_DOUBLE               (REAL         , Double.TYPE         , Type.DOUBLE     , "readDouble"         ,  4),
  REAL_BIGINTEGER           (REAL         , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     ,  6),
  REAL_BIGDECIMAL           (REAL         , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     ,  5),
  REAL_BOOLEAN_OBJECT       (REAL         , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  , 15),
  REAL_BOOLEAN              (REAL         , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        , 16),
  REAL_STRING               (REAL         , String.class        , Type.STRING     , "readString"         , 17),
  REAL_OBJECT               (REAL         , Object.class        , Type.OBJECT     , "readObject"         , 99),
  FLOAT_BYTE_OBJECT         (FLOAT        , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     , 13),
  FLOAT_BYTE                (FLOAT        , Byte.TYPE           , Type.BYTE       , "readByte"           , 14),
  FLOAT_SHORT_OBJECT        (FLOAT        , Short.class         , T_SHORT_OBJECT  , "readShortObject"    , 11),
  FLOAT_SHORT               (FLOAT        , Short.TYPE          , Type.SHORT      , "readShort"          , 12),
  FLOAT_INT_OBJECT          (FLOAT        , Integer.class       , T_INT_OBJECT    , "readIntObject"      ,  9),
  FLOAT_INT                 (FLOAT        , Integer.TYPE        , Type.INT        , "readInt"            , 10),
  FLOAT_LONG_OBJECT         (FLOAT        , Long.class          , T_LONG_OBJECT   , "readLongObject"     ,  7),
  FLOAT_LONG                (FLOAT        , Long.TYPE           , Type.LONG       , "readLong"           ,  8),
  FLOAT_FLOAT_OBJECT        (FLOAT        , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    ,  3),
  FLOAT_FLOAT               (FLOAT        , Float.TYPE          , Type.FLOAT      , "readFloat"          ,  4),
  FLOAT_DOUBLE_OBJECT       (FLOAT        , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   ,  1),
  FLOAT_DOUBLE              (FLOAT        , Double.TYPE         , Type.DOUBLE     , "readDouble"         ,  2),
  FLOAT_BIGINTEGER          (FLOAT        , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     ,  6),
  FLOAT_BIGDECIMAL          (FLOAT        , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     ,  5),
  FLOAT_BOOLEAN_OBJECT      (FLOAT        , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  , 15),
  FLOAT_BOOLEAN             (FLOAT        , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        , 16),
  FLOAT_STRING              (FLOAT        , String.class        , Type.STRING     , "readString"         , 17),
  FLOAT_OBJECT              (FLOAT        , Object.class        , Type.OBJECT     , "readObject"         , 99),
  DOUBLE_BYTE_OBJECT        (DOUBLE       , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     , 13),
  DOUBLE_BYTE               (DOUBLE       , Byte.TYPE           , Type.BYTE       , "readByte"           , 14),
  DOUBLE_SHORT_OBJECT       (DOUBLE       , Short.class         , T_SHORT_OBJECT  , "readShortObject"    , 11),
  DOUBLE_SHORT              (DOUBLE       , Short.TYPE          , Type.SHORT      , "readShort"          , 12),
  DOUBLE_INT_OBJECT         (DOUBLE       , Integer.class       , T_INT_OBJECT    , "readIntObject"      ,  9),
  DOUBLE_INT                (DOUBLE       , Integer.TYPE        , Type.INT        , "readInt"            , 10),
  DOUBLE_LONG_OBJECT        (DOUBLE       , Long.class          , T_LONG_OBJECT   , "readLongObject"     ,  7),
  DOUBLE_LONG               (DOUBLE       , Long.TYPE           , Type.LONG       , "readLong"           ,  8),
  DOUBLE_FLOAT_OBJECT       (DOUBLE       , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    ,  3),
  DOUBLE_FLOAT              (DOUBLE       , Float.TYPE          , Type.FLOAT      , "readFloat"          ,  4),
  DOUBLE_DOUBLE_OBJECT      (DOUBLE       , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   ,  1),
  DOUBLE_DOUBLE             (DOUBLE       , Double.TYPE         , Type.DOUBLE     , "readDouble"         ,  2),
  DOUBLE_BIGINTEGER         (DOUBLE       , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     ,  6),
  DOUBLE_BIGDECIMAL         (DOUBLE       , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     ,  5),
  DOUBLE_BOOLEAN_OBJECT     (DOUBLE       , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  , 15),
  DOUBLE_BOOLEAN            (DOUBLE       , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        , 16),
  DOUBLE_STRING             (DOUBLE       , String.class        , Type.STRING     , "readString"         , 17),
  DOUBLE_OBJECT             (DOUBLE       , Object.class        , Type.OBJECT     , "readObject"         , 99),
  DECIMAL_BYTE_OBJECT       (DECIMAL      , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     , 13),
  DECIMAL_BYTE              (DECIMAL      , Byte.TYPE           , Type.BYTE       , "readByte"           , 14),
  DECIMAL_SHORT_OBJECT      (DECIMAL      , Short.class         , T_SHORT_OBJECT  , "readShortObject"    , 11),
  DECIMAL_SHORT             (DECIMAL      , Short.TYPE          , Type.SHORT      , "readShort"          , 12),
  DECIMAL_INT_OBJECT        (DECIMAL      , Integer.class       , T_INT_OBJECT    , "readIntObject"      ,  9),
  DECIMAL_INT               (DECIMAL      , Integer.TYPE        , Type.INT        , "readInt"            , 10),
  DECIMAL_LONG_OBJECT       (DECIMAL      , Long.class          , T_LONG_OBJECT   , "readLongObject"     ,  7),
  DECIMAL_LONG              (DECIMAL      , Long.TYPE           , Type.LONG       , "readLong"           ,  8),
  DECIMAL_FLOAT_OBJECT      (DECIMAL      , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    ,  4),
  DECIMAL_FLOAT             (DECIMAL      , Float.TYPE          , Type.FLOAT      , "readFloat"          ,  5),
  DECIMAL_DOUBLE_OBJECT     (DECIMAL      , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   ,  2),
  DECIMAL_DOUBLE            (DECIMAL      , Double.TYPE         , Type.DOUBLE     , "readDouble"         ,  3),
  DECIMAL_BIGINTEGER        (DECIMAL      , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     ,  6),
  DECIMAL_BIGDECIMAL        (DECIMAL      , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     ,  1),
  DECIMAL_BOOLEAN_OBJECT    (DECIMAL      , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  , 15),
  DECIMAL_BOOLEAN           (DECIMAL      , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        , 16),
  DECIMAL_STRING            (DECIMAL      , String.class        , Type.STRING     , "readString"         , 17),
  DECIMAL_OBJECT            (DECIMAL      , Object.class        , Type.OBJECT     , "readObject"         , 99),
  NUMERIC_BYTE_OBJECT       (NUMERIC      , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     , 13),
  NUMERIC_BYTE              (NUMERIC      , Byte.TYPE           , Type.BYTE       , "readByte"           , 14),
  NUMERIC_SHORT_OBJECT      (NUMERIC      , Short.class         , T_SHORT_OBJECT  , "readShortObject"    , 11),
  NUMERIC_SHORT             (NUMERIC      , Short.TYPE          , Type.SHORT      , "readShort"          , 12),
  NUMERIC_INT_OBJECT        (NUMERIC      , Integer.class       , T_INT_OBJECT    , "readIntObject"      ,  9),
  NUMERIC_INT               (NUMERIC      , Integer.TYPE        , Type.INT        , "readInt"            , 10),
  NUMERIC_LONG_OBJECT       (NUMERIC      , Long.class          , T_LONG_OBJECT   , "readLongObject"     ,  7),
  NUMERIC_LONG              (NUMERIC      , Long.TYPE           , Type.LONG       , "readLong"           ,  8),
  NUMERIC_FLOAT_OBJECT      (NUMERIC      , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    ,  4),
  NUMERIC_FLOAT             (NUMERIC      , Float.TYPE          , Type.FLOAT      , "readFloat"          ,  5),
  NUMERIC_DOUBLE_OBJECT     (NUMERIC      , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   ,  2),
  NUMERIC_DOUBLE            (NUMERIC      , Double.TYPE         , Type.DOUBLE     , "readDouble"         ,  3),
  NUMERIC_BIGINTEGER        (NUMERIC      , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     ,  6),
  NUMERIC_BIGDECIMAL        (NUMERIC      , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     ,  1),
  NUMERIC_BOOLEAN_OBJECT    (NUMERIC      , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  , 15),
  NUMERIC_BOOLEAN           (NUMERIC      , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        , 16),
  NUMERIC_STRING            (NUMERIC      , String.class        , Type.STRING     , "readString"         , 17),
  NUMERIC_OBJECT            (NUMERIC      , Object.class        , Type.OBJECT     , "readObject"         , 99),
  BIT_BYTE_OBJECT           (BIT          , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     ,  3),
  BIT_BYTE                  (BIT          , Byte.TYPE           , Type.BYTE       , "readByte"           ,  4),
  BIT_SHORT_OBJECT          (BIT          , Short.class         , T_SHORT_OBJECT  , "readShortObject"    ,  5),
  BIT_SHORT                 (BIT          , Short.TYPE          , Type.SHORT      , "readShort"          ,  6),
  BIT_INT_OBJECT            (BIT          , Integer.class       , T_INT_OBJECT    , "readIntObject"      ,  7),
  BIT_INT                   (BIT          , Integer.TYPE        , Type.INT        , "readInt"            ,  8),
  BIT_LONG_OBJECT           (BIT          , Long.class          , T_LONG_OBJECT   , "readLongObject"     ,  9),
  BIT_LONG                  (BIT          , Long.TYPE           , Type.LONG       , "readLong"           , 10),
  BIT_FLOAT_OBJECT          (BIT          , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    , 11),
  BIT_FLOAT                 (BIT          , Float.TYPE          , Type.FLOAT      , "readFloat"          , 12),
  BIT_DOUBLE_OBJECT         (BIT          , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   , 13),
  BIT_DOUBLE                (BIT          , Double.TYPE         , Type.DOUBLE     , "readDouble"         , 14),
  BIT_BIGINTEGER            (BIT          , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     , 15),
  BIT_BIGDECIMAL            (BIT          , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     , 16),
  BIT_BOOLEAN_OBJECT        (BIT          , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  ,  1),
  BIT_BOOLEAN               (BIT          , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        ,  2),
  BIT_STRING                (BIT          , String.class        , Type.STRING     , "readString"         , 17),
  BIT_OBJECT                (BIT          , Object.class        , Type.OBJECT     , "readObject"         , 99),
  BOOLEAN_BYTE_OBJECT       (BOOLEAN      , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     ,  3),
  BOOLEAN_BYTE              (BOOLEAN      , Byte.TYPE           , Type.BYTE       , "readByte"           ,  4),
  BOOLEAN_SHORT_OBJECT      (BOOLEAN      , Short.class         , T_SHORT_OBJECT  , "readShortObject"    ,  5),
  BOOLEAN_SHORT             (BOOLEAN      , Short.TYPE          , Type.SHORT      , "readShort"          ,  6),
  BOOLEAN_INT_OBJECT        (BOOLEAN      , Integer.class       , T_INT_OBJECT    , "readIntObject"      ,  7),
  BOOLEAN_INT               (BOOLEAN      , Integer.TYPE        , Type.INT        , "readInt"            ,  8),
  BOOLEAN_LONG_OBJECT       (BOOLEAN      , Long.class          , T_LONG_OBJECT   , "readLongObject"     ,  9),
  BOOLEAN_LONG              (BOOLEAN      , Long.TYPE           , Type.LONG       , "readLong"           , 10),
  BOOLEAN_FLOAT_OBJECT      (BOOLEAN      , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    , 11),
  BOOLEAN_FLOAT             (BOOLEAN      , Float.TYPE          , Type.FLOAT      , "readFloat"          , 12),
  BOOLEAN_DOUBLE_OBJECT     (BOOLEAN      , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   , 13),
  BOOLEAN_DOUBLE            (BOOLEAN      , Double.TYPE         , Type.DOUBLE     , "readDouble"         , 14),
  BOOLEAN_BOOLEAN_OBJECT    (BOOLEAN      , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  ,  1),
  BOOLEAN_BOOLEAN           (BOOLEAN      , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        ,  2),
  BOOLEAN_STRING            (BOOLEAN      , String.class        , Type.STRING     , "readString"         , 17),
  BOOLEAN_OBJECT            (BOOLEAN      , Object.class        , Type.OBJECT     , "readObject"         , 99),
  CHAR_BYTE_OBJECT          (CHAR         , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     , 18),
  CHAR_BYTE                 (CHAR         , Byte.TYPE           , Type.BYTE       , "readByte"           , 19),
  CHAR_SHORT_OBJECT         (CHAR         , Short.class         , T_SHORT_OBJECT  , "readShortObject"    , 16),
  CHAR_SHORT                (CHAR         , Short.TYPE          , Type.SHORT      , "readShort"          , 17),
  CHAR_INT_OBJECT           (CHAR         , Integer.class       , T_INT_OBJECT    , "readIntObject"      , 14),
  CHAR_INT                  (CHAR         , Integer.TYPE        , Type.INT        , "readInt"            , 15),
  CHAR_LONG_OBJECT          (CHAR         , Long.class          , T_LONG_OBJECT   , "readLongObject"     , 12),
  CHAR_LONG                 (CHAR         , Long.TYPE           , Type.LONG       , "readLong"           , 13),
  CHAR_FLOAT_OBJECT         (CHAR         , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    , 10),
  CHAR_FLOAT                (CHAR         , Float.TYPE          , Type.FLOAT      , "readFloat"          , 11),
  CHAR_DOUBLE_OBJECT        (CHAR         , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   ,  8),
  CHAR_DOUBLE               (CHAR         , Double.TYPE         , Type.DOUBLE     , "readDouble"         ,  9),
  CHAR_BIGINTEGER           (CHAR         , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     ,  7),
  CHAR_BIGDECIMAL           (CHAR         , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     ,  6),
  CHAR_BOOLEAN_OBJECT       (CHAR         , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  , 15),
  CHAR_BOOLEAN              (CHAR         , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        , 16),
  CHAR_STRING               (CHAR         , String.class        , Type.STRING     , "readString"         ,  1),
  CHAR_CHAR_OBJECT          (CHAR         , Character.class     , T_CHAR_OBJECT   , "readCharObject"     ,  2),
  CHAR_CHAR                 (CHAR         , Character.TYPE      , Type.CHAR       , "readChar"           ,  3),
  CHAR_DATE                 (CHAR         , java.sql.Date.class , T_SQL_DATE      , "readSqlDate"        , 20),
  CHAR_TIME                 (CHAR         , Time.class          , T_TIME          , "readSqlTime"        , 21),
  CHAR_TIMESTAMP            (CHAR         , Timestamp.class     , T_TIMESTAMP     , "readSqlTimestamp"   , 22),
  CHAR_UTIL_DATE            (CHAR         , java.util.Date.class, T_UTIL_DATE     , "readTimestamp"      , 23),
  CHAR_INPUTSTREAM          (CHAR         , InputStream.class   , T_INPUT_STREAM  , "readAsciiStream"    ,  5),
  CHAR_READER               (CHAR         , Reader.class        , T_READER        , "readCharacterStream",  4),
  CHAR_OBJECT               (CHAR         , Object.class        , Type.OBJECT     , "readObject"         , 99),
  VARCHAR_BYTE_OBJECT       (VARCHAR      , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     , 18),
  VARCHAR_BYTE              (VARCHAR      , Byte.TYPE           , Type.BYTE       , "readByte"           , 19),
  VARCHAR_SHORT_OBJECT      (VARCHAR      , Short.class         , T_SHORT_OBJECT  , "readShortObject"    , 16),
  VARCHAR_SHORT             (VARCHAR      , Short.TYPE          , Type.SHORT      , "readShort"          , 17),
  VARCHAR_INT_OBJECT        (VARCHAR      , Integer.class       , T_INT_OBJECT    , "readIntObject"      , 14),
  VARCHAR_INT               (VARCHAR      , Integer.TYPE        , Type.INT        , "readInt"            , 15),
  VARCHAR_LONG_OBJECT       (VARCHAR      , Long.class          , T_LONG_OBJECT   , "readLongObject"     , 12),
  VARCHAR_LONG              (VARCHAR      , Long.TYPE           , Type.LONG       , "readLong"           , 13),
  VARCHAR_FLOAT_OBJECT      (VARCHAR      , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    , 10),
  VARCHAR_FLOAT             (VARCHAR      , Float.TYPE          , Type.FLOAT      , "readFloat"          , 11),
  VARCHAR_DOUBLE_OBJECT     (VARCHAR      , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   ,  8),
  VARCHAR_DOUBLE            (VARCHAR      , Double.TYPE         , Type.DOUBLE     , "readDouble"         ,  9),
  VARCHAR_BIGINTEGER        (VARCHAR      , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     ,  7),
  VARCHAR_BIGDECIMAL        (VARCHAR      , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     ,  6),
  VARCHAR_BOOLEAN_OBJECT    (VARCHAR      , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  , 15),
  VARCHAR_BOOLEAN           (VARCHAR      , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        , 16),
  VARCHAR_STRING            (VARCHAR      , String.class        , Type.STRING     , "readString"         ,  1),
  VARCHAR_CHAR_OBJECT       (VARCHAR      , Character.class     , T_CHAR_OBJECT   , "readCharObject"     ,  2),
  VARCHAR_CHAR              (VARCHAR      , Character.TYPE      , Type.CHAR       , "readChar"           ,  3),
  VARCHAR_DATE              (VARCHAR      , java.sql.Date.class , T_SQL_DATE      , "readSqlDate"        , 20),
  VARCHAR_TIME              (VARCHAR      , Time.class          , T_TIME          , "readSqlTime"        , 21),
  VARCHAR_TIMESTAMP         (VARCHAR      , Timestamp.class     , T_TIMESTAMP     , "readSqlTimestamp"   , 22),
  VARCHAR_UTIL_DATE         (VARCHAR      , java.util.Date.class, T_UTIL_DATE     , "readTimestamp"      , 23),
  VARCHAR_INPUTSTREAM       (VARCHAR      , InputStream.class   , T_INPUT_STREAM  , "readAsciiStream"    ,  5),
  VARCHAR_READER            (VARCHAR      , Reader.class        , T_READER        , "readCharacterStream",  4),
  VARCHAR_OBJECT            (VARCHAR      , Object.class        , Type.OBJECT     , "readObject"         , 99),
  LONGVARCHAR_BYTE_OBJECT   (LONGVARCHAR  , Byte.class          , T_BYTE_OBJECT   , "readByteObject"     , 18),
  LONGVARCHAR_BYTE          (LONGVARCHAR  , Byte.TYPE           , Type.BYTE       , "readByte"           , 19),
  LONGVARCHAR_SHORT_OBJECT  (LONGVARCHAR  , Short.class         , T_SHORT_OBJECT  , "readShortObject"    , 16),
  LONGVARCHAR_SHORT         (LONGVARCHAR  , Short.TYPE          , Type.SHORT      , "readShort"          , 17),
  LONGVARCHAR_INT_OBJECT    (LONGVARCHAR  , Integer.class       , T_INT_OBJECT    , "readIntObject"      , 14),
  LONGVARCHAR_INT           (LONGVARCHAR  , Integer.TYPE        , Type.INT        , "readInt"            , 15),
  LONGVARCHAR_LONG_OBJECT   (LONGVARCHAR  , Long.class          , T_LONG_OBJECT   , "readLongObject"     , 12),
  LONGVARCHAR_LONG          (LONGVARCHAR  , Long.TYPE           , Type.LONG       , "readLong"           , 13),
  LONGVARCHAR_FLOAT_OBJECT  (LONGVARCHAR  , Float.class         , T_FLOAT_OBJECT  , "readFloatObject"    , 10),
  LONGVARCHAR_FLOAT         (LONGVARCHAR  , Float.TYPE          , Type.FLOAT      , "readFloat"          , 11),
  LONGVARCHAR_DOUBLE_OBJECT (LONGVARCHAR  , Double.class        , T_DOUBLE_OBJECT , "readDoubleObject"   ,  8),
  LONGVARCHAR_DOUBLE        (LONGVARCHAR  , Double.TYPE         , Type.DOUBLE     , "readDouble"         ,  9),
  LONGVARCHAR_BIGINTEGER    (LONGVARCHAR  , BigInteger.class    , T_BIG_INTEGER   , "readBigInteger"     ,  7),
  LONGVARCHAR_BIGDECIMAL    (LONGVARCHAR  , BigDecimal.class    , T_BIG_DECIMAL   , "readBigDecimal"     ,  6),
  LONGVARCHAR_BOOLEAN_OBJECT(LONGVARCHAR  , Boolean.class       , T_BOOLEAN_OBJECT, "readBooleanObject"  , 15),
  LONGVARCHAR_BOOLEAN       (LONGVARCHAR  , Boolean.TYPE        , Type.BOOLEAN    , "readBoolean"        , 16),
  LONGVARCHAR_STRING        (LONGVARCHAR  , String.class        , Type.STRING     , "readString"         ,  3),
  LONGVARCHAR_CHAR_OBJECT   (LONGVARCHAR  , Character.class     , T_CHAR_OBJECT   , "readCharObject"     ,  4),
  LONGVARCHAR_CHAR          (LONGVARCHAR  , Character.TYPE      , Type.CHAR       , "readChar"           ,  5),
  LONGVARCHAR_DATE          (LONGVARCHAR  , java.sql.Date.class , T_SQL_DATE      , "readSqlDate"        , 20),
  LONGVARCHAR_TIME          (LONGVARCHAR  , Time.class          , T_TIME          , "readSqlTime"        , 21),
  LONGVARCHAR_TIMESTAMP     (LONGVARCHAR  , Timestamp.class     , T_TIMESTAMP     , "readSqlTimestamp"   , 22),
  LONGVARCHAR_UTIL_DATE     (LONGVARCHAR  , java.util.Date.class, T_UTIL_DATE     , "readTimestamp"      , 23),
  LONGVARCHAR_INPUTSTREAM   (LONGVARCHAR  , InputStream.class   , T_INPUT_STREAM  , "readAsciiStream"    ,  2),
  LONGVARCHAR_READER        (LONGVARCHAR  , Reader.class        , T_READER        , "readCharacterStream",  1),
  LONGVARCHAR_OBJECT        (LONGVARCHAR  , Object.class        , Type.OBJECT     , "readObject"         , 99),
  BINARY_BYTES              (BINARY       , byte[].class        , T_BYTE_ARRAY    , "readBytes"          ,  1),
  BINARY_INPUTSTREAM        (BINARY       , InputStream.class   , T_INPUT_STREAM  , "readBinaryStream"   ,  2),
  BINARY_READER             (BINARY       , Reader.class        , T_READER        , "readCharacterStream",  3),
  BINARY_STRING             (BINARY       , String.class        , Type.STRING     , "readString"         ,  4),
  BINARY_CHAR_OBJECT        (BINARY       , Character.class     , T_CHAR_OBJECT   , "readCharObject"     ,  5),
  BINARY_CHAR               (BINARY       , Character.TYPE      , Type.CHAR       , "readChar"           ,  6),
  BINARY_OBJECT             (BINARY       , Object.class        , Type.OBJECT     , "readObject"         , 99),
  VARBINARY_BYTES           (VARBINARY    , byte[].class        , T_BYTE_ARRAY    , "readBytes"          ,  1),
  VARBINARY_INPUTSTREAM     (VARBINARY    , InputStream.class   , T_INPUT_STREAM  , "readBinaryStream"   ,  2),
  VARBINARY_READER          (VARBINARY    , Reader.class        , T_READER        , "readCharacterStream",  3),
  VARBINARY_STRING          (VARBINARY    , String.class        , Type.STRING     , "readString"         ,  4),
  VARBINARY_CHAR_OBJECT     (VARBINARY    , Character.class     , T_CHAR_OBJECT   , "readCharObject"     ,  5),
  VARBINARY_CHAR            (VARBINARY    , Character.TYPE      , Type.CHAR       , "readChar"           ,  6),
  VARBINARY_OBJECT          (VARBINARY    , Object.class        , Type.OBJECT     , "readObject"         , 99),
  LONGVARBINARY_BYTES       (LONGVARBINARY, byte[].class        , T_BYTE_ARRAY    , "readBytes"          ,  2),
  LONGVARBINARY_INPUTSTREAM (LONGVARBINARY, InputStream.class   , T_INPUT_STREAM  , "readBinaryStream"   ,  1),
  LONGVARBINARY_READER      (LONGVARBINARY, Reader.class        , T_READER        , "readCharacterStream",  3),
  LONGVARBINARY_STRING      (LONGVARBINARY, String.class        , Type.STRING     , "readString"         ,  4),
  LONGVARBINARY_CHAR_OBJECT (LONGVARBINARY, Character.class     , T_CHAR_OBJECT   , "readCharObject"     ,  5),
  LONGVARBINARY_CHAR        (LONGVARBINARY, Character.TYPE      , Type.CHAR       , "readChar"           ,  6),
  LONGVARBINARY_OBJECT      (LONGVARBINARY, Object.class        , Type.OBJECT     , "readObject"         , 99),
  DATE_TIMESTAMP            (DATE         , Timestamp.class     , T_TIMESTAMP     , "readSqlTimestamp"   ,  2),
  DATE_DATE                 (DATE         , java.sql.Date.class , T_SQL_DATE      , "readSqlDate"        ,  1),
  DATE_UTIL_DATE            (DATE         , java.util.Date.class, T_UTIL_DATE     , "readDate"           ,  3),
  DATE_STRING               (DATE         , String.class        , Type.STRING     , "readString"         ,  4),
  DATE_OBJECT               (DATE         , Object.class        , Type.OBJECT     , "readObject"         , 99),
  TIME_TIMESTAMP            (TIME         , Timestamp.class     , T_TIMESTAMP     , "readSqlTimestamp"   ,  2),
  TIME_TIME                 (TIME         , Time.class          , T_TIME          , "readSqlTime"        ,  1),
  TIME_UTIL_DATE            (TIME         , java.util.Date.class, T_UTIL_DATE     , "readTime"           ,  3),
  TIME_STRING               (TIME         , String.class        , Type.STRING     , "readString"         ,  4),
  TIME_OBJECT               (TIME         , Object.class        , Type.OBJECT     , "readObject"         , 99),
  TIMESTAMP_TIMESTAMP       (TIMESTAMP    , Timestamp.class     , T_TIMESTAMP     , "readSqlTimestamp"   ,  1),
  TIMESTAMP_DATE            (TIMESTAMP    , java.sql.Date.class , T_SQL_DATE      , "readSqlDate"        ,  3),
  TIMESTAMP_TIME            (TIMESTAMP    , Time.class          , T_TIME          , "readSqlTime"        ,  4),
  TIMESTAMP_UTIL_DATE       (TIMESTAMP    , java.util.Date.class, T_UTIL_DATE     , "readTimestamp"      ,  2),
  TIMESTAMP_STRING          (TIMESTAMP    , String.class        , Type.STRING     , "readString"         ,  5),
  TIMESTAMP_OBJECT          (TIMESTAMP    , Object.class        , Type.OBJECT     , "readObject"         , 99),
  CLOB_CLOB                 (CLOB         , Clob.class          , T_CLOB          , "readClob"           ,  1),
  CLOB_OBJECT               (CLOB         , Object.class        , Type.OBJECT     , "readObject"         , 99),
  BLOB_BLOB                 (BLOB         , Blob.class          , T_BLOB          , "readBlob"           ,  1),
  BLOB_OBJECT               (BLOB         , Object.class        , Type.OBJECT     , "readObject"         , 99),
  ARRAY_ARRAY               (ARRAY        , Array.class         , T_ARRAY         , "readArray"          ,  1),
  ARRAY_OBJECT              (ARRAY        , Object.class        , Type.OBJECT     , "readObject"         , 99),
  REF_REF                   (REF          , Ref.class           , T_REF           , "readRef"            ,  1),
  REF_OBJECT                (REF          , Object.class        , Type.OBJECT     , "readObject"         , 99),
  DATALINK_URL              (DATALINK     , URL.class           , T_URL           , "readURL"            ,  1),
  DATALINK_STRING           (DATALINK     , String.class        , Type.STRING     , "readString"         ,  2),
  DATALINK_OBJECT           (DATALINK     , Object.class        , Type.OBJECT     , "readObject"         , 99),
  STRUCT_OBJECT             (STRUCT       , Object.class        , Type.OBJECT     , "readObject"         ,  1),
  JAVAOBJECT_OBJECT         (JAVA_OBJECT  , Object.class        , Type.OBJECT     , "readObject"         ,  1);

  private TypeCombination combination;
  private Type type;
  private String readerMethod;
  private int priority;
  
  private ObjectFactoryType(int sqlType, Class<?> setterClass, Type type, String method, int priority) {
    
    combination = new TypeCombination(sqlType, setterClass);
    this.type = type;
    readerMethod = method;
    this.priority = priority;
  }
  
  TypeCombination getCombination() {
    
    return combination;
  }
  
  Type getType() {
    
    return type;
  }
  
  String getReaderMethod() {
    
    return readerMethod;
  }
  
  int getPriority() {
    
    return priority;
  }
  
// --- parameter finder -------------------------------------------------------
  
  static private final Map<TypeCombination, ObjectFactoryType> parameterMap;
  
  static {
    
    parameterMap = new HashMap<TypeCombination, ObjectFactoryType>(512);
    
    for (ObjectFactoryType type : ObjectFactoryType.values()) {
      parameterMap.put(type.getCombination(), type);
    }
  }
  
  static ObjectFactoryType getType(TypeCombination combination) {
    
    return parameterMap.get(combination);
  }
}
