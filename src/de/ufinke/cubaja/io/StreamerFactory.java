// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.io;

import static de.ufinke.cubaja.cafebabe.AccessFlags.ACC_PUBLIC;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.cafebabe.CodeAttribute;
import de.ufinke.cubaja.cafebabe.GenClass;
import de.ufinke.cubaja.cafebabe.GenClassLoader;
import de.ufinke.cubaja.cafebabe.GenMethod;
import de.ufinke.cubaja.cafebabe.Generator;
import de.ufinke.cubaja.cafebabe.Type;

public class StreamerFactory implements Generator {

// parameter kind -------------------------------------------------------------
  
  static enum Kind {
  
    PRIMITIVE,
    BUILTIN,
    ENUM,
    STREAMABLE,
    UNKNOWN
  }

// parameter types and attributes ---------------------------------------------
  
  static enum Parm {
    
    BOOLEAN(Boolean.TYPE, Kind.PRIMITIVE, "Boolean"),
    BYTE(Byte.TYPE, Kind.PRIMITIVE, "Byte"),
    SHORT(Short.TYPE, Kind.PRIMITIVE, "Short"),
    CHAR(Character.TYPE, Kind.PRIMITIVE, "Char"),
    INT(Integer.TYPE, Kind.PRIMITIVE, "Int"),
    LONG(Long.TYPE, Kind.PRIMITIVE, "Long"),
    FLOAT(Float.TYPE, Kind.PRIMITIVE, "Float"),
    DOUBLE(Double.TYPE, Kind.PRIMITIVE, "Double"),
    BOOLEAN_OBJECT(Boolean.class, Kind.BUILTIN, "BooleanObject"),
    BYTE_OBJECT(Byte.class, Kind.BUILTIN, "ByteObject"),
    SHORT_OBJECT(Short.class, Kind.BUILTIN, "ShortObject"),
    CHAR_OBJECT(Character.class, Kind.BUILTIN, "CharObject"),
    INT_OBJECT(Integer.class, Kind.BUILTIN, "IntObject"),
    LONG_OBJECT(Long.class, Kind.BUILTIN, "LongObject"),
    FLOAT_OBJECT(Float.class, Kind.BUILTIN, "FloatObject"),
    DOUBLE_OBJECT(Double.class, Kind.BUILTIN, "DoubleObject"),
    STRING(String.class, Kind.BUILTIN, "String"),
    DATE(Date.class, Kind.BUILTIN, "Date"),
    BIG_INTEGER(BigInteger.class, Kind.BUILTIN, "BigInteger"),
    BIG_DECIMAL(BigDecimal.class, Kind.BUILTIN, "BigDecimal"),
    BYTE_ARRAY(byte[].class, Kind.BUILTIN, "ByteArray"),
    ENUM(Enum.class, Kind.ENUM, "Enum"),
    STREAMABLE(Streamable.class, Kind.STREAMABLE, "Streamable"),
    OBJECT(Object.class, Kind.UNKNOWN, "");
    
    private Class<?> clazz;
    private Type type;
    private Kind kind;
    private String writerMethod;
    private String readerMethod;
    
    private Parm(Class<?> clazz, Kind kind, String method) {
      
      this.clazz = clazz;
      type = new Type(clazz);
      this.kind = kind;
      writerMethod = "write" + method;
      readerMethod = "read" + method;
    }
    
    Class<?> getClazz() {
      
      return clazz;
    }
    
    Type getType() {
      
      return type;
    }
    
    Kind getKind() {
      
      return kind;
    }
    
    String getWriterMethod() {
      
      return writerMethod;
    }
    
    String getReaderMethod() {
      
      return readerMethod;
    }
    
    boolean isPredefinedObjectType() {
      
      return kind == Kind.BUILTIN || kind == Kind.STREAMABLE || kind == Kind.ENUM;
    }
    
    boolean needsClassParameter() {
      
      return kind == Kind.STREAMABLE || kind == Kind.ENUM;
    }
  }
  
  static private final Map<Class<?>, Parm> parmMap = createParmMap();
  
  static private Map<Class<?>, Parm> createParmMap() {
    
    Map<Class<?>, Parm> map = new HashMap<Class<?>, Parm>();
    
    for (Parm parm : Parm.values()) {
      map.put(parm.getClazz(), parm);
    }
    
    return map;
  }

// properties (getter / setter pairs) of classes ------------------------------
  
  static class Property {
    
    private Class<?> setterClazz;
    private Class<?> getterClazz;
    private String setterMethod;
    private String getterMethod;
    
    Property() {
      
    }

    Class<?> getClazz() {
    
      return setterClazz;
    }
    
    void setSetterClazz(Class<?> clazz) {
    
      setterClazz = clazz;
    }
    
    void setGetterClazz(Class<?> clazz) {
      
      getterClazz = clazz;
    }
    
    String getSetterMethod() {
    
      return setterMethod;
    }
    
    void setSetterMethod(String setterMethod) {
    
      this.setterMethod = setterMethod;
    }

    String getGetterMethod() {
    
      return getterMethod;
    }

    void setGetterMethod(String getterMethod) {
    
      this.getterMethod = getterMethod;
    }
    
    boolean isValid() {
      
      return setterMethod != null && getterMethod != null && setterClazz == getterClazz;
    }
  }
  
  static private final Map<Class<?>, List<Property>> propertyMap = new HashMap<Class<?>, List<Property>>();
  
  static private List<Property> getPropertyList(Class<?> clazz) {
    
    List<Property> list = propertyMap.get(clazz);
    
    if (list == null) {
      list = createPropertyList(clazz);
    }
    
    return list;
  }
  
  static private synchronized List<Property> createPropertyList(Class<?> clazz) {
    
    List<Property> list = propertyMap.get(clazz);
    if (list != null) {
      return list;
    }
    
    Map<String, Property> map = new HashMap<String, Property>();
    
    for (Method method : clazz.getMethods()) {
      
      String methodName = method.getName();
      String name = null;
      Class<?> setterClass = null;
      Class<?> getterClass = null;
      String setterMethod = null;
      String getterMethod = null;
      
      if (methodName.startsWith("get") && methodName.length() > 3 && method.getParameterTypes().length == 0 && method.getReturnType() != Void.TYPE) {
        name = methodName.substring(3);
        getterClass = method.getReturnType();
        getterMethod = methodName;
      } else if (methodName.startsWith("set") && methodName.length() > 3 && method.getParameterTypes().length == 1 && method.getReturnType() == Void.TYPE) {
        name = methodName.substring(3);
        setterClass = method.getParameterTypes()[0];
        setterMethod = methodName;
      }
      
      if (name != null) {
        Property property = map.get(name);
        if (property == null) {
          property = new Property();
          map.put(name, property);
        }
        property.setSetterClazz(setterClass);
        property.setGetterClazz(getterClass);
        property.setSetterMethod(setterMethod);
        property.setGetterMethod(getterMethod);
      }
    }
    
    list = new ArrayList<Property>();
    for (Property property : map.values()) {
      if (property.isValid()) {
        list.add(property);
      }
    }
    
    propertyMap.put(clazz, list);
    
    return list;
  }
  
// constants ------------------------------------------------------------------
  
  static private final GenClassLoader loader = new GenClassLoader();
  
  static private final Type objectType = new Type(Object.class);
  static private final Type classType = new Type(Class.class);
  static private final Type voidType = new Type(Void.TYPE);
  static private final Type streamerType = new Type(Streamer.class);
  static private final Type exceptionType = new Type(Exception.class);
  static private final Type inputStreamType = new Type(BinaryInputStream.class);
  static private final Type outputStreamType = new Type(BinaryOutputStream.class);
  
// StreamerFactory logic ------------------------------------------------------
  
  @SuppressWarnings("unchecked")
  static public <D> Streamer<D> createStreamer(Class<D> clazz) throws Exception {
  
    return (Streamer<D>) loader.createInstance(new StreamerFactory(clazz));
  }
  
  private Class<?> clazz;
  private String className;
  private Type dataClassType;
  private Type genClassType;
  private CodeAttribute readerCode;
  private CodeAttribute writerCode;
  private int lastWriterLocal;
  private int lastReaderLocal;
  
  private StreamerFactory(Class<?> clazz) {
  
    this.clazz = clazz;
  }

  public String getClassName() throws Exception {

    if (className == null) {
      StringBuilder sb = new StringBuilder(200);
      sb.append(getClass().getPackage().getName());
      sb.append(".Streamer_");
      sb.append(clazz.getName().replace('.', '_'));
      className = sb.toString();
    }
    
    return className;
  }
  
  public GenClass generate() throws Exception {

    GenClass genClass = new GenClass(ACC_PUBLIC, getClassName(), streamerType);

    genClassType = new Type(genClass);
    dataClassType = new Type(clazz);
    
    genClass.createDefaultConstructor();
    
    GenMethod writer = genClass.createMethod(ACC_PUBLIC, voidType, "write", objectType);
    GenMethod reader = genClass.createMethod(ACC_PUBLIC, objectType, "read");
    
    writer.addException(exceptionType);
    reader.addException(exceptionType);
    
    writerCode = writer.getCode();
    readerCode = reader.getCode();

    lastWriterLocal = 1;
    lastReaderLocal = 0;
    
    generateCode();
    
    return genClass;
  }
  
  private void generateCode() {
    
    writerCode.loadLocalReference(1); // cast object to its real type
    writerCode.cast(dataClassType);
    
    generateIntro(writerCode, outputStreamType, "out", 1);
    generateIntro(readerCode, inputStreamType, "in", 2);
    
    Parm parm = getParm(clazz);
    if (parm.isPredefinedObjectType()) {
      generateBuiltinRoot(parm);
    } else {      
      generateUnknown(clazz);
    }
    
    writerCode.returnVoid();
    readerCode.returnReference();
    
    generateNullStream(writerCode);
    generateNullStream(readerCode);
  }
  
  private void generateIntro(CodeAttribute code, Type streamType, String streamFieldName, int local) {
    
    code.loadLocalReference(0); // this
    code.getField(genClassType, streamType, streamFieldName); // get stream
    code.duplicate();
    code.branchIfNull("nullStream");
    code.storeLocalReference(local); // store stream in local variable
  }
  
  private void generateNullStream(CodeAttribute code) {
    
    code.defineLabel("nullStream");
    code.pop(); // discard duplicated stream object
    code.invokeStatic(genClassType, voidType, "nullStream"); // call nullStream() to throw IllegalStateException
  }
  
  private void generateUnknown(Class<?> currentClass) {

    for (Property property : getPropertyList(currentClass)) {
      
      Parm parm = getParm(property.getClazz());
      
      switch (parm.getKind()) {

        case PRIMITIVE:
        case BUILTIN:
          break;
          
        case STREAMABLE:
        case ENUM:
          break;
          
        case UNKNOWN:
          break;
      }
    }
  }
  
  private void incrementLastLocal() {
    
    if (lastWriterLocal == 1) {
      lastWriterLocal++;
      lastReaderLocal++;
    }
    lastWriterLocal++;
    lastReaderLocal++;
  }
  
  private void generateBuiltinRoot(Parm parm) {

    writerCode.loadLocalReference(2); // stream
    writerCode.loadLocalReference(1); // object
    writerCode.invokeVirtual(outputStreamType, voidType, parm.getWriterMethod(), parm.getType()); // writeXXX
    
    readerCode.loadLocalReference(1); // stream
    if (parm.needsClassParameter()) {
      readerCode.loadConstant(clazz);
      readerCode.invokeVirtual(inputStreamType, parm.getType(), parm.getReaderMethod(), classType); // readXXX(Class)
    } else {      
      readerCode.invokeVirtual(inputStreamType, parm.getType(), parm.getReaderMethod()); // readXXX
    }
  }
  
  private Parm getParm(Class<?> currentClass) {
    
    Parm parm = parmMap.get(currentClass);
    
    if (parm == null) {
      if (Streamable.class.isAssignableFrom(currentClass)) {
        parm = Parm.STREAMABLE;
      } else if (currentClass.isEnum()) {
        parm = Parm.ENUM;
      } else {
        parm = Parm.OBJECT;
      }
    }
    
    return parm;
  }
  
}
