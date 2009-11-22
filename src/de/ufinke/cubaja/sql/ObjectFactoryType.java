// Copyright (c) 2009, Uwe Finke. All rights reserved.
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

enum ObjectFactoryType {

  // CONSTANT(sqlType, setterParameterClass, isPrimitive, queryReadMethodName, priorityWithinSqlType)
  TINYINT_BYTE_OBJECT       (TINYINT      , Byte.class          ,  "ByteObject"     ,  1),
  TINYINT_BYTE              (TINYINT      , Byte.TYPE           ,  "Byte"           ,  2),
  TINYINT_SHORT_OBJECT      (TINYINT      , Short.class         ,  "ShortObject"    ,  3),
  TINYINT_SHORT             (TINYINT      , Short.TYPE          ,  "Short"          ,  4),
  TINYINT_INT_OBJECT        (TINYINT      , Integer.class       ,  "IntObject"      ,  5),
  TINYINT_INT               (TINYINT      , Integer.TYPE        ,  "Int"            ,  6),
  TINYINT_LONG_OBJECT       (TINYINT      , Long.class          ,  "LongObject"     ,  7),
  TINYINT_LONG              (TINYINT      , Long.TYPE           ,  "Long"           ,  8),
  TINYINT_FLOAT_OBJECT      (TINYINT      , Float.class         ,  "FloatObject"    , 13),
  TINYINT_FLOAT             (TINYINT      , Float.TYPE          ,  "Float"          , 14),
  TINYINT_DOUBLE_OBJECT     (TINYINT      , Double.class        ,  "DoubleObject"   , 11),
  TINYINT_DOUBLE            (TINYINT      , Double.TYPE         ,  "Double"         , 12),
  TINYINT_BIGINTEGER        (TINYINT      , BigInteger.class    ,  "BigInteger"     ,  9),
  TINYINT_BIGDECIMAL        (TINYINT      , BigDecimal.class    ,  "BigDecimal"     , 10),
  TINYINT_BOOLEAN_OBJECT    (TINYINT      , Boolean.class       ,  "BooleanObject"  , 15),
  TINYINT_BOOLEAN           (TINYINT      , Boolean.TYPE        ,  "Boolean"        , 16),
  TINYINT_STRING            (TINYINT      , String.class        ,  "String"         , 17),
  TINYINT_OBJECT            (TINYINT      , Object.class        ,  "Object"         , 99),
  SMALLINT_BYTE_OBJECT      (SMALLINT     , Byte.class          ,  "ByteObject"     ,  9),
  SMALLINT_BYTE             (SMALLINT     , Byte.TYPE           ,  "Byte"           , 10),
  SMALLINT_SHORT_OBJECT     (SMALLINT     , Short.class         ,  "ShortObject"    ,  1),
  SMALLINT_SHORT            (SMALLINT     , Short.TYPE          ,  "Short"          ,  2),
  SMALLINT_INT_OBJECT       (SMALLINT     , Integer.class       ,  "IntObject"      ,  3),
  SMALLINT_INT              (SMALLINT     , Integer.TYPE        ,  "Int"            ,  4),
  SMALLINT_LONG_OBJECT      (SMALLINT     , Long.class          ,  "LongObject"     ,  5),
  SMALLINT_LONG             (SMALLINT     , Long.TYPE           ,  "Long"           ,  6),
  SMALLINT_FLOAT_OBJECT     (SMALLINT     , Float.class         ,  "FloatObject"    , 13),
  SMALLINT_FLOAT            (SMALLINT     , Float.TYPE          ,  "Float"          , 14),
  SMALLINT_DOUBLE_OBJECT    (SMALLINT     , Double.class        ,  "DoubleObject"   , 11),
  SMALLINT_DOUBLE           (SMALLINT     , Double.TYPE         ,  "Double"         , 12),
  SMALLINT_BIGINTEGER       (SMALLINT     , BigInteger.class    ,  "BigInteger"     ,  7),
  SMALLINT_BIGDECIMAL       (SMALLINT     , BigDecimal.class    ,  "BigDecimal"     ,  8),
  SMALLINT_BOOLEAN_OBJECT   (SMALLINT     , Boolean.class       ,  "BooleanObject"  , 15),
  SMALLINT_BOOLEAN          (SMALLINT     , Boolean.TYPE        ,  "Boolean"        , 16),
  SMALLINT_STRING           (SMALLINT     , String.class        ,  "String"         , 17),
  SMALLINT_OBJECT           (SMALLINT     , Object.class        ,  "Object"         , 99),
  INTEGER_BYTE_OBJECT       (INTEGER      , Byte.class          ,  "ByteObject"     ,  9),
  INTEGER_BYTE              (INTEGER      , Byte.TYPE           ,  "Byte"           , 10),
  INTEGER_SHORT_OBJECT      (INTEGER      , Short.class         ,  "ShortObject"    ,  7),
  INTEGER_SHORT             (INTEGER      , Short.TYPE          ,  "Short"          ,  8),
  INTEGER_INT_OBJECT        (INTEGER      , Integer.class       ,  "IntObject"      ,  1),
  INTEGER_INT               (INTEGER      , Integer.TYPE        ,  "Int"            ,  2),
  INTEGER_LONG_OBJECT       (INTEGER      , Long.class          ,  "LongObject"     ,  3),
  INTEGER_LONG              (INTEGER      , Long.TYPE           ,  "Long"           ,  4),
  INTEGER_FLOAT_OBJECT      (INTEGER      , Float.class         ,  "FloatObject"    , 13),
  INTEGER_FLOAT             (INTEGER      , Float.TYPE          ,  "Float"          , 14),
  INTEGER_DOUBLE_OBJECT     (INTEGER      , Double.class        ,  "DoubleObject"   , 11),
  INTEGER_DOUBLE            (INTEGER      , Double.TYPE         ,  "Double"         , 12),
  INTEGER_BIGINTEGER        (INTEGER      , BigInteger.class    ,  "BigInteger"     ,  5),
  INTEGER_BIGDECIMAL        (INTEGER      , BigDecimal.class    ,  "BigDecimal"     ,  6),
  INTEGER_BOOLEAN_OBJECT    (INTEGER      , Boolean.class       ,  "BooleanObject"  , 15),
  INTEGER_BOOLEAN           (INTEGER      , Boolean.TYPE        ,  "Boolean"        , 16),
  INTEGER_STRING            (INTEGER      , String.class        ,  "String"         , 17),
  INTEGER_OBJECT            (INTEGER      , Object.class        ,  "Object"         , 99),
  BIGINT_BYTE_OBJECT        (BIGINT       , Byte.class          ,  "ByteObject"     ,  9),
  BIGINT_BYTE               (BIGINT       , Byte.TYPE           ,  "Byte"           , 10),
  BIGINT_SHORT_OBJECT       (BIGINT       , Short.class         ,  "ShortObject"    ,  7),
  BIGINT_SHORT              (BIGINT       , Short.TYPE          ,  "Short"          ,  8),
  BIGINT_INT_OBJECT         (BIGINT       , Integer.class       ,  "IntObject"      ,  5),
  BIGINT_INT                (BIGINT       , Integer.TYPE        ,  "Int"            ,  6),
  BIGINT_LONG_OBJECT        (BIGINT       , Long.class          ,  "LongObject"     ,  1),
  BIGINT_LONG               (BIGINT       , Long.TYPE           ,  "Long"           ,  2),
  BIGINT_FLOAT_OBJECT       (BIGINT       , Float.class         ,  "FloatObject"    , 13),
  BIGINT_FLOAT              (BIGINT       , Float.TYPE          ,  "Float"          , 14),
  BIGINT_DOUBLE_OBJECT      (BIGINT       , Double.class        ,  "DoubleObject"   , 11),
  BIGINT_DOUBLE             (BIGINT       , Double.TYPE         ,  "Double"         , 12),
  BIGINT_BIGINTEGER         (BIGINT       , BigInteger.class    ,  "BigInteger"     ,  3),
  BIGINT_BIGDECIMAL         (BIGINT       , BigDecimal.class    ,  "BigDecimal"     ,  4),
  BIGINT_BOOLEAN_OBJECT     (BIGINT       , Boolean.class       ,  "BooleanObject"  , 15),
  BIGINT_BOOLEAN            (BIGINT       , Boolean.TYPE        ,  "Boolean"        , 16),
  BIGINT_STRING             (BIGINT       , String.class        ,  "String"         , 17),
  BIGINT_OBJECT             (BIGINT       , Object.class        ,  "Object"         , 99),
  REAL_BYTE_OBJECT          (REAL         , Byte.class          ,  "ByteObject"     , 13),
  REAL_BYTE                 (REAL         , Byte.TYPE           ,  "Byte"           , 14),
  REAL_SHORT_OBJECT         (REAL         , Short.class         ,  "ShortObject"    , 11),
  REAL_SHORT                (REAL         , Short.TYPE          ,  "Short"          , 12),
  REAL_INT_OBJECT           (REAL         , Integer.class       ,  "IntObject"      ,  9),
  REAL_INT                  (REAL         , Integer.TYPE        ,  "Int"            , 10),
  REAL_LONG_OBJECT          (REAL         , Long.class          ,  "LongObject"     ,  7),
  REAL_LONG                 (REAL         , Long.TYPE           ,  "Long"           ,  8),
  REAL_FLOAT_OBJECT         (REAL         , Float.class         ,  "FloatObject"    ,  1),
  REAL_FLOAT                (REAL         , Float.TYPE          ,  "Float"          ,  2),
  REAL_DOUBLE_OBJECT        (REAL         , Double.class        ,  "DoubleObject"   ,  3),
  REAL_DOUBLE               (REAL         , Double.TYPE         ,  "Double"         ,  4),
  REAL_BIGINTEGER           (REAL         , BigInteger.class    ,  "BigInteger"     ,  6),
  REAL_BIGDECIMAL           (REAL         , BigDecimal.class    ,  "BigDecimal"     ,  5),
  REAL_BOOLEAN_OBJECT       (REAL         , Boolean.class       ,  "BooleanObject"  , 15),
  REAL_BOOLEAN              (REAL         , Boolean.TYPE        ,  "Boolean"        , 16),
  REAL_STRING               (REAL         , String.class        ,  "String"         , 17),
  REAL_OBJECT               (REAL         , Object.class        ,  "Object"         , 99),
  FLOAT_BYTE_OBJECT         (FLOAT        , Byte.class          ,  "ByteObject"     , 13),
  FLOAT_BYTE                (FLOAT        , Byte.TYPE           ,  "Byte"           , 14),
  FLOAT_SHORT_OBJECT        (FLOAT        , Short.class         ,  "ShortObject"    , 11),
  FLOAT_SHORT               (FLOAT        , Short.TYPE          ,  "Short"          , 12),
  FLOAT_INT_OBJECT          (FLOAT        , Integer.class       ,  "IntObject"      ,  9),
  FLOAT_INT                 (FLOAT        , Integer.TYPE        ,  "Int"            , 10),
  FLOAT_LONG_OBJECT         (FLOAT        , Long.class          ,  "LongObject"     ,  7),
  FLOAT_LONG                (FLOAT        , Long.TYPE           ,  "Long"           ,  8),
  FLOAT_FLOAT_OBJECT        (FLOAT        , Float.class         ,  "FloatObject"    ,  3),
  FLOAT_FLOAT               (FLOAT        , Float.TYPE          ,  "Float"          ,  4),
  FLOAT_DOUBLE_OBJECT       (FLOAT        , Double.class        ,  "DoubleObject"   ,  1),
  FLOAT_DOUBLE              (FLOAT        , Double.TYPE         ,  "Double"         ,  2),
  FLOAT_BIGINTEGER          (FLOAT        , BigInteger.class    ,  "BigInteger"     ,  6),
  FLOAT_BIGDECIMAL          (FLOAT        , BigDecimal.class    ,  "BigDecimal"     ,  5),
  FLOAT_BOOLEAN_OBJECT      (FLOAT        , Boolean.class       ,  "BooleanObject"  , 15),
  FLOAT_BOOLEAN             (FLOAT        , Boolean.TYPE        ,  "Boolean"        , 16),
  FLOAT_STRING              (FLOAT        , String.class        ,  "String"         , 17),
  FLOAT_OBJECT              (FLOAT        , Object.class        ,  "Object"         , 99),
  DOUBLE_BYTE_OBJECT        (DOUBLE       , Byte.class          ,  "ByteObject"     , 13),
  DOUBLE_BYTE               (DOUBLE       , Byte.TYPE           ,  "Byte"           , 14),
  DOUBLE_SHORT_OBJECT       (DOUBLE       , Short.class         ,  "ShortObject"    , 11),
  DOUBLE_SHORT              (DOUBLE       , Short.TYPE          ,  "Short"          , 12),
  DOUBLE_INT_OBJECT         (DOUBLE       , Integer.class       ,  "IntObject"      ,  9),
  DOUBLE_INT                (DOUBLE       , Integer.TYPE        ,  "Int"            , 10),
  DOUBLE_LONG_OBJECT        (DOUBLE       , Long.class          ,  "LongObject"     ,  7),
  DOUBLE_LONG               (DOUBLE       , Long.TYPE           ,  "Long"           ,  8),
  DOUBLE_FLOAT_OBJECT       (DOUBLE       , Float.class         ,  "FloatObject"    ,  3),
  DOUBLE_FLOAT              (DOUBLE       , Float.TYPE          ,  "Float"          ,  4),
  DOUBLE_DOUBLE_OBJECT      (DOUBLE       , Double.class        ,  "DoubleObject"   ,  1),
  DOUBLE_DOUBLE             (DOUBLE       , Double.TYPE         ,  "Double"         ,  2),
  DOUBLE_BIGINTEGER         (DOUBLE       , BigInteger.class    ,  "BigInteger"     ,  6),
  DOUBLE_BIGDECIMAL         (DOUBLE       , BigDecimal.class    ,  "BigDecimal"     ,  5),
  DOUBLE_BOOLEAN_OBJECT     (DOUBLE       , Boolean.class       ,  "BooleanObject"  , 15),
  DOUBLE_BOOLEAN            (DOUBLE       , Boolean.TYPE        ,  "Boolean"        , 16),
  DOUBLE_STRING             (DOUBLE       , String.class        ,  "String"         , 17),
  DOUBLE_OBJECT             (DOUBLE       , Object.class        ,  "Object"         , 99),
  DECIMAL_BYTE_OBJECT       (DECIMAL      , Byte.class          ,  "ByteObject"     , 13),
  DECIMAL_BYTE              (DECIMAL      , Byte.TYPE           ,  "Byte"           , 14),
  DECIMAL_SHORT_OBJECT      (DECIMAL      , Short.class         ,  "ShortObject"    , 11),
  DECIMAL_SHORT             (DECIMAL      , Short.TYPE          ,  "Short"          , 12),
  DECIMAL_INT_OBJECT        (DECIMAL      , Integer.class       ,  "IntObject"      ,  9),
  DECIMAL_INT               (DECIMAL      , Integer.TYPE        ,  "Int"            , 10),
  DECIMAL_LONG_OBJECT       (DECIMAL      , Long.class          ,  "LongObject"     ,  7),
  DECIMAL_LONG              (DECIMAL      , Long.TYPE           ,  "Long"           ,  8),
  DECIMAL_FLOAT_OBJECT      (DECIMAL      , Float.class         ,  "FloatObject"    ,  4),
  DECIMAL_FLOAT             (DECIMAL      , Float.TYPE          ,  "Float"          ,  5),
  DECIMAL_DOUBLE_OBJECT     (DECIMAL      , Double.class        ,  "DoubleObject"   ,  2),
  DECIMAL_DOUBLE            (DECIMAL      , Double.TYPE         ,  "Double"         ,  3),
  DECIMAL_BIGINTEGER        (DECIMAL      , BigInteger.class    ,  "BigInteger"     ,  6),
  DECIMAL_BIGDECIMAL        (DECIMAL      , BigDecimal.class    ,  "BigDecimal"     ,  1),
  DECIMAL_BOOLEAN_OBJECT    (DECIMAL      , Boolean.class       ,  "BooleanObject"  , 15),
  DECIMAL_BOOLEAN           (DECIMAL      , Boolean.TYPE        ,  "Boolean"        , 16),
  DECIMAL_STRING            (DECIMAL      , String.class        ,  "String"         , 17),
  DECIMAL_OBJECT            (DECIMAL      , Object.class        ,  "Object"         , 99),
  NUMERIC_BYTE_OBJECT       (NUMERIC      , Byte.class          ,  "ByteObject"     , 13),
  NUMERIC_BYTE              (NUMERIC      , Byte.TYPE           ,  "Byte"           , 14),
  NUMERIC_SHORT_OBJECT      (NUMERIC      , Short.class         ,  "ShortObject"    , 11),
  NUMERIC_SHORT             (NUMERIC      , Short.TYPE          ,  "Short"          , 12),
  NUMERIC_INT_OBJECT        (NUMERIC      , Integer.class       ,  "IntObject"      ,  9),
  NUMERIC_INT               (NUMERIC      , Integer.TYPE        ,  "Int"            , 10),
  NUMERIC_LONG_OBJECT       (NUMERIC      , Long.class          ,  "LongObject"     ,  7),
  NUMERIC_LONG              (NUMERIC      , Long.TYPE           ,  "Long"           ,  8),
  NUMERIC_FLOAT_OBJECT      (NUMERIC      , Float.class         ,  "FloatObject"    ,  4),
  NUMERIC_FLOAT             (NUMERIC      , Float.TYPE          ,  "Float"          ,  5),
  NUMERIC_DOUBLE_OBJECT     (NUMERIC      , Double.class        ,  "DoubleObject"   ,  2),
  NUMERIC_DOUBLE            (NUMERIC      , Double.TYPE         ,  "Double"         ,  3),
  NUMERIC_BIGINTEGER        (NUMERIC      , BigInteger.class    ,  "BigInteger"     ,  6),
  NUMERIC_BIGDECIMAL        (NUMERIC      , BigDecimal.class    ,  "BigDecimal"     ,  1),
  NUMERIC_BOOLEAN_OBJECT    (NUMERIC      , Boolean.class       ,  "BooleanObject"  , 15),
  NUMERIC_BOOLEAN           (NUMERIC      , Boolean.TYPE        ,  "Boolean"        , 16),
  NUMERIC_STRING            (NUMERIC      , String.class        ,  "String"         , 17),
  NUMERIC_OBJECT            (NUMERIC      , Object.class        ,  "Object"         , 99),
  BIT_BYTE_OBJECT           (BIT          , Byte.class          ,  "ByteObject"     ,  3),
  BIT_BYTE                  (BIT          , Byte.TYPE           ,  "Byte"           ,  4),
  BIT_SHORT_OBJECT          (BIT          , Short.class         ,  "ShortObject"    ,  5),
  BIT_SHORT                 (BIT          , Short.TYPE          ,  "Short"          ,  6),
  BIT_INT_OBJECT            (BIT          , Integer.class       ,  "IntObject"      ,  7),
  BIT_INT                   (BIT          , Integer.TYPE        ,  "Int"            ,  8),
  BIT_LONG_OBJECT           (BIT          , Long.class          ,  "LongObject"     ,  9),
  BIT_LONG                  (BIT          , Long.TYPE           ,  "Long"           , 10),
  BIT_FLOAT_OBJECT          (BIT          , Float.class         ,  "FloatObject"    , 11),
  BIT_FLOAT                 (BIT          , Float.TYPE          ,  "Float"          , 12),
  BIT_DOUBLE_OBJECT         (BIT          , Double.class        ,  "DoubleObject"   , 13),
  BIT_DOUBLE                (BIT          , Double.TYPE         ,  "Double"         , 14),
  BIT_BIGINTEGER            (BIT          , BigInteger.class    ,  "BigInteger"     , 15),
  BIT_BIGDECIMAL            (BIT          , BigDecimal.class    ,  "BigDecimal"     , 16),
  BIT_BOOLEAN_OBJECT        (BIT          , Boolean.class       ,  "BooleanObject"  ,  1),
  BIT_BOOLEAN               (BIT          , Boolean.TYPE        ,  "Boolean"        ,  2),
  BIT_STRING                (BIT          , String.class        ,  "String"         , 17),
  BIT_OBJECT                (BIT          , Object.class        ,  "Object"         , 99),
  BOOLEAN_BYTE_OBJECT       (BOOLEAN      , Byte.class          ,  "ByteObject"     ,  3),
  BOOLEAN_BYTE              (BOOLEAN      , Byte.TYPE           ,  "Byte"           ,  4),
  BOOLEAN_SHORT_OBJECT      (BOOLEAN      , Short.class         ,  "ShortObject"    ,  5),
  BOOLEAN_SHORT             (BOOLEAN      , Short.TYPE          ,  "Short"          ,  6),
  BOOLEAN_INT_OBJECT        (BOOLEAN      , Integer.class       ,  "IntObject"      ,  7),
  BOOLEAN_INT               (BOOLEAN      , Integer.TYPE        ,  "Int"            ,  8),
  BOOLEAN_LONG_OBJECT       (BOOLEAN      , Long.class          ,  "LongObject"     ,  9),
  BOOLEAN_LONG              (BOOLEAN      , Long.TYPE           ,  "Long"           , 10),
  BOOLEAN_FLOAT_OBJECT      (BOOLEAN      , Float.class         ,  "FloatObject"    , 11),
  BOOLEAN_FLOAT             (BOOLEAN      , Float.TYPE          ,  "Float"          , 12),
  BOOLEAN_DOUBLE_OBJECT     (BOOLEAN      , Double.class        ,  "DoubleObject"   , 13),
  BOOLEAN_DOUBLE            (BOOLEAN      , Double.TYPE         ,  "Double"         , 14),
  BOOLEAN_BOOLEAN_OBJECT    (BOOLEAN      , Boolean.class       ,  "BooleanObject"  ,  1),
  BOOLEAN_BOOLEAN           (BOOLEAN      , Boolean.TYPE        ,  "Boolean"        ,  2),
  BOOLEAN_STRING            (BOOLEAN      , String.class        ,  "String"         , 17),
  CHAR_BYTE_OBJECT          (CHAR         , Byte.class          ,  "ByteObject"     , 18),
  CHAR_BYTE                 (CHAR         , Byte.TYPE           ,  "Byte"           , 19),
  CHAR_SHORT_OBJECT         (CHAR         , Short.class         ,  "ShortObject"    , 16),
  CHAR_SHORT                (CHAR         , Short.TYPE          ,  "Short"          , 17),
  CHAR_INT_OBJECT           (CHAR         , Integer.class       ,  "IntObject"      , 14),
  CHAR_INT                  (CHAR         , Integer.TYPE        ,  "Int"            , 15),
  CHAR_LONG_OBJECT          (CHAR         , Long.class          ,  "LongObject"     , 12),
  CHAR_LONG                 (CHAR         , Long.TYPE           ,  "Long"           , 13),
  CHAR_FLOAT_OBJECT         (CHAR         , Float.class         ,  "FloatObject"    , 10),
  CHAR_FLOAT                (CHAR         , Float.TYPE          ,  "Float"          , 11),
  CHAR_DOUBLE_OBJECT        (CHAR         , Double.class        ,  "DoubleObject"   ,  8),
  CHAR_DOUBLE               (CHAR         , Double.TYPE         ,  "Double"         ,  9),
  CHAR_BIGINTEGER           (CHAR         , BigInteger.class    ,  "BigInteger"     ,  7),
  CHAR_BIGDECIMAL           (CHAR         , BigDecimal.class    ,  "BigDecimal"     ,  6),
  CHAR_BOOLEAN_OBJECT       (CHAR         , Boolean.class       ,  "BooleanObject"  , 15),
  CHAR_BOOLEAN              (CHAR         , Boolean.TYPE        ,  "Boolean"        , 16),
  CHAR_STRING               (CHAR         , String.class        ,  "String"         ,  1),
  CHAR_CHAR_OBJECT          (CHAR         , Character.class     ,  "CharObject"     ,  2),
  CHAR_CHAR                 (CHAR         , Character.TYPE      ,  "Char"           ,  3),
  CHAR_DATE                 (CHAR         , java.sql.Date.class ,  "SqlDate"        , 20),
  CHAR_TIME                 (CHAR         , Time.class          ,  "SqlTime"        , 21),
  CHAR_TIMESTAMP            (CHAR         , Timestamp.class     ,  "SqlTimestamp"   , 22),
  CHAR_UTIL_DATE            (CHAR         , java.util.Date.class,  "Timestamp"      , 23),
  CHAR_INPUTSTREAM          (CHAR         , InputStream.class   ,  "AsciiStream"    ,  5),
  CHAR_READER               (CHAR         , Reader.class        ,  "CharacterStream",  4),
  CHAR_OBJECT               (CHAR         , Object.class        ,  "Object"         , 99),
  VARCHAR_BYTE_OBJECT       (VARCHAR      , Byte.class          ,  "ByteObject"     , 18),
  VARCHAR_BYTE              (VARCHAR      , Byte.TYPE           ,  "Byte"           , 19),
  VARCHAR_SHORT_OBJECT      (VARCHAR      , Short.class         ,  "ShortObject"    , 16),
  VARCHAR_SHORT             (VARCHAR      , Short.TYPE          ,  "Short"          , 17),
  VARCHAR_INT_OBJECT        (VARCHAR      , Integer.class       ,  "IntObject"      , 14),
  VARCHAR_INT               (VARCHAR      , Integer.TYPE        ,  "Int"            , 15),
  VARCHAR_LONG_OBJECT       (VARCHAR      , Long.class          ,  "LongObject"     , 12),
  VARCHAR_LONG              (VARCHAR      , Long.TYPE           ,  "Long"           , 13),
  VARCHAR_FLOAT_OBJECT      (VARCHAR      , Float.class         ,  "FloatObject"    , 10),
  VARCHAR_FLOAT             (VARCHAR      , Float.TYPE          ,  "Float"          , 11),
  VARCHAR_DOUBLE_OBJECT     (VARCHAR      , Double.class        ,  "DoubleObject"   ,  8),
  VARCHAR_DOUBLE            (VARCHAR      , Double.TYPE         ,  "Double"         ,  9),
  VARCHAR_BIGINTEGER        (VARCHAR      , BigInteger.class    ,  "BigInteger"     ,  7),
  VARCHAR_BIGDECIMAL        (VARCHAR      , BigDecimal.class    ,  "BigDecimal"     ,  6),
  VARCHAR_BOOLEAN_OBJECT    (VARCHAR      , Boolean.class       ,  "BooleanObject"  , 15),
  VARCHAR_BOOLEAN           (VARCHAR      , Boolean.TYPE        ,  "Boolean"        , 16),
  VARCHAR_STRING            (VARCHAR      , String.class        ,  "String"         ,  1),
  VARCHAR_CHAR_OBJECT       (VARCHAR      , Character.class     ,  "CharObject"     ,  2),
  VARCHAR_CHAR              (VARCHAR      , Character.TYPE      ,  "Char"           ,  3),
  VARCHAR_DATE              (VARCHAR      , java.sql.Date.class ,  "SqlDate"        , 20),
  VARCHAR_TIME              (VARCHAR      , Time.class          ,  "SqlTime"        , 21),
  VARCHAR_TIMESTAMP         (VARCHAR      , Timestamp.class     ,  "SqlTimestamp"   , 22),
  VARCHAR_UTIL_DATE         (VARCHAR      , java.util.Date.class,  "Timestamp"      , 23),
  VARCHAR_INPUTSTREAM       (VARCHAR      , InputStream.class   ,  "AsciiStream"    ,  5),
  VARCHAR_READER            (VARCHAR      , Reader.class        ,  "CharacterStream",  4),
  VARCHAR_OBJECT            (VARCHAR      , Object.class        ,  "Object"         , 99),
  LONGVARCHAR_BYTE_OBJECT   (LONGVARCHAR  , Byte.class          ,  "ByteObject"     , 18),
  LONGVARCHAR_BYTE          (LONGVARCHAR  , Byte.TYPE           ,  "Byte"           , 19),
  LONGVARCHAR_SHORT_OBJECT  (LONGVARCHAR  , Short.class         ,  "ShortObject"    , 16),
  LONGVARCHAR_SHORT         (LONGVARCHAR  , Short.TYPE          ,  "Short"          , 17),
  LONGVARCHAR_INT_OBJECT    (LONGVARCHAR  , Integer.class       ,  "IntObject"      , 14),
  LONGVARCHAR_INT           (LONGVARCHAR  , Integer.TYPE        ,  "Int"            , 15),
  LONGVARCHAR_LONG_OBJECT   (LONGVARCHAR  , Long.class          ,  "LongObject"     , 12),
  LONGVARCHAR_LONG          (LONGVARCHAR  , Long.TYPE           ,  "Long"           , 13),
  LONGVARCHAR_FLOAT_OBJECT  (LONGVARCHAR  , Float.class         ,  "FloatObject"    , 10),
  LONGVARCHAR_FLOAT         (LONGVARCHAR  , Float.TYPE          ,  "Float"          , 11),
  LONGVARCHAR_DOUBLE_OBJECT (LONGVARCHAR  , Double.class        ,  "DoubleObject"   ,  8),
  LONGVARCHAR_DOUBLE        (LONGVARCHAR  , Double.TYPE         ,  "Double"         ,  9),
  LONGVARCHAR_BIGINTEGER    (LONGVARCHAR  , BigInteger.class    ,  "BigInteger"     ,  7),
  LONGVARCHAR_BIGDECIMAL    (LONGVARCHAR  , BigDecimal.class    ,  "BigDecimal"     ,  6),
  LONGVARCHAR_BOOLEAN_OBJECT(LONGVARCHAR  , Boolean.class       ,  "BooleanObject"  , 15),
  LONGVARCHAR_BOOLEAN       (LONGVARCHAR  , Boolean.TYPE        ,  "Boolean"        , 16),
  LONGVARCHAR_STRING        (LONGVARCHAR  , String.class        ,  "String"         ,  3),
  LONGVARCHAR_CHAR_OBJECT   (LONGVARCHAR  , Character.class     ,  "CharObject"     ,  4),
  LONGVARCHAR_CHAR          (LONGVARCHAR  , Character.TYPE      ,  "Char"           ,  5),
  LONGVARCHAR_DATE          (LONGVARCHAR  , java.sql.Date.class ,  "SqlDate"        , 20),
  LONGVARCHAR_TIME          (LONGVARCHAR  , Time.class          ,  "SqlTime"        , 21),
  LONGVARCHAR_TIMESTAMP     (LONGVARCHAR  , Timestamp.class     ,  "SqlTimestamp"   , 22),
  LONGVARCHAR_UTIL_DATE     (LONGVARCHAR  , java.util.Date.class,  "Timestamp"      , 23),
  LONGVARCHAR_INPUTSTREAM   (LONGVARCHAR  , InputStream.class   ,  "AsciiStream"    ,  2),
  LONGVARCHAR_READER        (LONGVARCHAR  , Reader.class        ,  "CharacterStream",  1),
  LONGVARCHAR_OBJECT        (LONGVARCHAR  , Object.class        ,  "Object"         , 99),
  BINARY_BYTES              (BINARY       , byte[].class        ,  "Bytes"          ,  1),
  BINARY_INPUTSTREAM        (BINARY       , InputStream.class   ,  "BinaryStream"   ,  2),
  BINARY_READER             (BINARY       , Reader.class        ,  "CharacterStream",  3),
  BINARY_STRING             (BINARY       , String.class        ,  "String"         ,  4),
  BINARY_OBJECT             (BINARY       , Object.class        ,  "Object"         , 99),
  VARBINARY_BYTES           (VARBINARY    , byte[].class        ,  "Bytes"          ,  1),
  VARBINARY_INPUTSTREAM     (VARBINARY    , InputStream.class   ,  "BinaryStream"   ,  2),
  VARBINARY_READER          (VARBINARY    , Reader.class        ,  "CharacterStream",  3),
  VARBINARY_STRING          (VARBINARY    , String.class        ,  "String"         ,  4),
  VARBINARY_OBJECT          (VARBINARY    , Object.class        ,  "Object"         , 99),
  LONGVARBINARY_BYTES       (LONGVARBINARY, byte[].class        ,  "Bytes"          ,  2),
  LONGVARBINARY_INPUTSTREAM (LONGVARBINARY, InputStream.class   ,  "BinaryStream"   ,  1),
  LONGVARBINARY_READER      (LONGVARBINARY, Reader.class        ,  "CharacterStream",  3),
  LONGVARBINARY_STRING      (LONGVARBINARY, String.class        ,  "String"         ,  4),
  LONGVARBINARY_OBJECT      (LONGVARBINARY, Object.class        ,  "Object"         , 99),
  DATE_TIMESTAMP            (DATE         , Timestamp.class     ,  "SqlTimestamp"   ,  2),
  DATE_DATE                 (DATE         , java.sql.Date.class ,  "SqlDate"        ,  1),
  DATE_UTIL_DATE            (DATE         , java.util.Date.class,  "Date"           ,  3),
  DATE_STRING               (DATE         , String.class        ,  "String"         ,  4),
  DATE_OBJECT               (DATE         , Object.class        ,  "Object"         , 99),
  TIME_TIMESTAMP            (TIME         , Timestamp.class     ,  "SqlTimestamp"   ,  2),
  TIME_TIME                 (TIME         , Time.class          ,  "SqlTime"        ,  1),
  TIME_UTIL_DATE            (TIME         , java.util.Date.class,  "Time"           ,  3),
  TIME_STRING               (TIME         , String.class        ,  "String"         ,  4),
  TIME_OBJECT               (TIME         , Object.class        ,  "Object"         , 99),
  TIMESTAMP_TIMESTAMP       (TIMESTAMP    , Timestamp.class     ,  "SqlTimestamp"   ,  1),
  TIMESTAMP_DATE            (TIMESTAMP    , java.sql.Date.class ,  "SqlDate"        ,  3),
  TIMESTAMP_TIME            (TIMESTAMP    , Time.class          ,  "SqlTime"        ,  4),
  TIMESTAMP_UTIL_DATE       (TIMESTAMP    , java.util.Date.class,  "Timestamp"      ,  2),
  TIMESTAMP_STRING          (TIMESTAMP    , String.class        ,  "String"         ,  5),
  TIMESTAMP_OBJECT          (TIMESTAMP    , Object.class        ,  "Object"         , 99),
  CLOB_CLOB                 (CLOB         , Clob.class          ,  "Clob"           ,  1),
  CLOB_OBJECT               (CLOB         , Object.class        ,  "Object"         , 99),
  BLOB_BLOB                 (BLOB         , Blob.class          ,  "Blob"           ,  1),
  BLOB_OBJECT               (BLOB         , Object.class        ,  "Object"         , 99),
  ARRAY_ARRAY               (ARRAY        , Array.class         ,  "Array"          ,  1),
  ARRAY_OBJECT              (ARRAY        , Object.class        ,  "Object"         , 99),
  REF_REF                   (REF          , Ref.class           ,  "Ref"            ,  1),
  REF_OBJECT                (REF          , Object.class        ,  "Object"         , 99),
  DATALINK_URL              (DATALINK     , URL.class           ,  "URL"            ,  1),
  DATALINK_STRING           (DATALINK     , String.class        ,  "String"         ,  2),
  DATALINK_OBJECT           (DATALINK     , Object.class        ,  "Object"         , 99),
  STRUCT_OBJECT             (STRUCT       , Object.class        ,  "Object"         ,  1),
  JAVAOBJECT_OBJECT         (JAVA_OBJECT  , Object.class        ,  "Object"         ,  1);

  private TypeCombination combination;
  private Type type;
  private String readerMethod;
  private int priority;
  
  private ObjectFactoryType(int sqlType, Class<?> setterClass, String method, int priority) {
    
    combination = new TypeCombination(sqlType, setterClass);
    type = new Type(setterClass);
    readerMethod = "read" + method;
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
  
  static private final Map<TypeCombination, ObjectFactoryType> parameterMap = createParameterMap();
  
  static private Map<TypeCombination, ObjectFactoryType> createParameterMap() {
    
    Map<TypeCombination, ObjectFactoryType> map = new HashMap<TypeCombination, ObjectFactoryType>(512);
    
    for (ObjectFactoryType type : ObjectFactoryType.values()) {
      map.put(type.getCombination(), type);
    }
    
    return map;
  }
  
  static ObjectFactoryType getType(TypeCombination combination) {
    
    return parameterMap.get(combination);
  }
}
