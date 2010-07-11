// Copyright (c) 2008 - 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import de.ufinke.cubaja.util.Text;

class ParameterManager implements ParameterFactoryFinder {
  
  static Text text = Text.getPackageInstance(ParameterManager.class);
  
  static class NodeFactory implements ParameterFactory {
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
      
      return type.newInstance();
    }
    
    public boolean isNode() {
      
      return true;
    }
  }
  
  static class EnumFactory implements ParameterFactory {
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {

      value = value.trim();
      Class<Enum> enumType = (Class<Enum>) type;
      try {      
        return Enum.valueOf(enumType, value.toUpperCase());
      } catch (IllegalArgumentException iae) {
        for (Enum constant : enumType.getEnumConstants()) {
          if (constant.name().equalsIgnoreCase(value)) {              
            return constant;
          }
        }
        StringBuffer sb = new StringBuffer(150);
        for (Enum constant : enumType.getEnumConstants()) {
          if (constant.ordinal() > 0) {
            sb.append(", ");
          }
          sb.append(constant.name().toLowerCase());
        }
        throw new ConfigException(text.get("parmEnum", sb.toString()));
      }
    }
    
    public boolean isNode() {
      
      return false;
    }
  }
  
  static class StringFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    StringFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
      
      parameterManager.checkPattern(value, annotations);
      
      return value;
    }
    
    public boolean isNode() {
      
      return false;
    }
  }

  static class CharacterFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    CharacterFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {

      parameterManager.checkPattern(value, annotations);
      
      if (value.length() != 1) {
        throw new ConfigException(text.get("parmCharacter"));
      }
      
      return value.charAt(0);
    }
    
    public boolean isNode() {
      
      return false;
    }
  }

  static class BooleanFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    BooleanFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
      
      parameterManager.checkPattern(value, annotations);
            
      value = value.toLowerCase();
      
      for (String s : parameterManager.getTrueValues()) {
        if (s.equals(value)) {
          return true;
        }
      }
      
      for (String s : parameterManager.getFalseValues()) {
        if (s.equals(value)) {
          return false;
        }
      }
      
      throw new ConfigException(text.get("parmBoolean"));
    }
    
    public boolean isNode() {
      
      return false;
    }
  }

  static class ByteFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    ByteFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
      
      value = parameterManager.prepareNumber(value, annotations);

      try {
        return Byte.parseByte(value);
      } catch (Exception e) {
        throw new ConfigException(text.get("parmByte"));
      }
    }
    
    public boolean isNode() {
      
      return false;
    }
  }
  
  static class ShortFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    ShortFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
      
      value = parameterManager.prepareNumber(value, annotations);

      try {
        return Short.parseShort(value);
      } catch (Exception e) {
        throw new ConfigException(text.get("parmShort"));
      }
    }
    
    public boolean isNode() {
      
      return false;
    }
  }
  
  static class IntegerFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    IntegerFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
      
      value = parameterManager.prepareNumber(value, annotations);

      try {
        return Integer.parseInt(value);
      } catch (Exception e) {
        throw new ConfigException(text.get("parmInteger"));
      }
    }
    
    public boolean isNode() {
      
      return false;
    }
  }
  
  static class LongFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    LongFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
      
      value = parameterManager.prepareNumber(value, annotations);

      try {
        return Long.parseLong(value);
      } catch (Exception e) {
        throw new ConfigException(text.get("parmLong"));
      }
    }
    
    public boolean isNode() {
      
      return false;
    }
  }
  
  static class FloatFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    FloatFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
      
      value = parameterManager.prepareNumber(value, annotations);

      try {
        return Float.parseFloat(value);
      } catch (Exception e) {
        throw new ConfigException(text.get("parmDecimal"));
      }
    }
    
    public boolean isNode() {
      
      return false;
    }
  }
  
  static class DoubleFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    DoubleFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
      
      value = parameterManager.prepareNumber(value, annotations);

      try {
        return Double.parseDouble(value);
      } catch (Exception e) {
        throw new ConfigException(text.get("parmDecimal"));
      }
    }
    
    public boolean isNode() {
      
      return false;
    }
  }
  
  static class BigIntegerFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    BigIntegerFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
      
      value = parameterManager.prepareNumber(value, annotations);

      try {
        return new BigInteger(value);
      } catch (Exception e) {
        throw new ConfigException(text.get("parmBigInteger"));
      }
    }
    
    public boolean isNode() {
      
      return false;
    }
  }
  
  static class BigDecimalFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    BigDecimalFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
      
      value = parameterManager.prepareNumber(value, annotations);

      try {
        return new BigDecimal(value);
      } catch (Exception e) {
        throw new ConfigException(text.get("parmDecimal"));
      }
    }
    
    public boolean isNode() {
      
      return false;
    }
  }
  
  static class DateFactory implements ParameterFactory {

    private ParameterManager parameterManager;
    
    DateFactory(ParameterManager parameterManager) {
    
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
    
      value = value.trim();
      
      SimpleDateFormat format = null;
      
      Pattern pattern = null;
      int i = 0;
      while (pattern == null && i < annotations.length) {
        if (Pattern.class.isAssignableFrom(annotations[i].getClass())) {
          pattern = (Pattern) annotations[i];
        }
        i++;
      }
      
      if (pattern != null) {
        format = new SimpleDateFormat(pattern.value());
      } else {
        format = parameterManager.getDateFormat();
      }             
      
      try {          
        return format.parse(value);
      } catch (ParseException e) {
        String hint = parameterManager.getDateHint();
        if (pattern != null) {
          if (pattern.hint().length() > 0) {
            hint = pattern.hint();
          }
        }
        throw new ConfigException(text.get("parmDate", hint));
      }
    }
    
    public boolean isNode() {
      
      return false;
    }
  }
  
  static class ClassFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    ClassFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {
      
      value = value.trim();
      parameterManager.checkPattern(value, annotations);
      
      try {
        return Class.forName(value);
      } catch (Exception e) {
        throw new ConfigException(e.toString());
      }
    }
    
    public boolean isNode() {
      
      return false;
    }
  }
  
  static class InterfaceFactory implements ParameterFactory {
    
    private ParameterManager parameterManager;
    
    InterfaceFactory(ParameterManager parameterManager) {
      
      this.parameterManager = parameterManager;
    }
    
    public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {

      value = value.trim();
      parameterManager.checkPattern(value, annotations);

      Class<?> clazz = null;
      
      try {
        clazz = Class.forName(value);
      } catch (Exception e) {
        throw new ConfigException(e.toString());
      }
      
      if (! type.isAssignableFrom(clazz)) {
        throw new ConfigException(text.get("parmInterface", type.getName()));
      }
      
      try {
        return clazz.newInstance();
      } catch (Exception e) {
        throw new ConfigException(e.toString());
      }
    }
    
    public boolean isNode() {
      
      return false;
    }
  }
  
  private Map<Class<?>, ParameterFactory> parameterFactoryMap;
  private Stack<ParameterFactoryFinder> finderStack;
  private Map<Class<?>, Class<?>> primitivesMap;
  private SimpleDateFormat dateFormat;
  private String dateHint;
  private String[] trueValues;
  private String[] falseValues;
  private Character decimalPoint;

  ParameterManager() {

    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateHint = dateFormat.toPattern();
    
    primitivesMap = new HashMap<Class<?>, Class<?>>();
    primitivesMap.put(Byte.TYPE, Byte.class);
    primitivesMap.put(Short.TYPE, Short.class);
    primitivesMap.put(Character.TYPE, Character.class);
    primitivesMap.put(Integer.TYPE, Integer.class);
    primitivesMap.put(Long.TYPE, Long.class);
    primitivesMap.put(Float.TYPE, Float.class);
    primitivesMap.put(Double.TYPE, Double.class);
    primitivesMap.put(Boolean.TYPE, Boolean.class);
    
    finderStack = new Stack<ParameterFactoryFinder>();
    finderStack.add(this);
    
    parameterFactoryMap = new HashMap<Class<?>, ParameterFactory>();    
    parameterFactoryMap.put(Object.class, new NodeFactory());
    parameterFactoryMap.put(Enum.class, new EnumFactory());
    parameterFactoryMap.put(String.class, new StringFactory(this));
    parameterFactoryMap.put(Character.class, new CharacterFactory(this));
    parameterFactoryMap.put(Boolean.class, new BooleanFactory(this));
    parameterFactoryMap.put(Byte.class, new ByteFactory(this));
    parameterFactoryMap.put(Short.class, new ShortFactory(this));
    parameterFactoryMap.put(Integer.class, new IntegerFactory(this));
    parameterFactoryMap.put(Long.class, new LongFactory(this));
    parameterFactoryMap.put(Float.class, new FloatFactory(this));
    parameterFactoryMap.put(Double.class, new DoubleFactory(this));
    parameterFactoryMap.put(BigInteger.class, new BigIntegerFactory(this));
    parameterFactoryMap.put(BigDecimal.class, new BigDecimalFactory(this));
    parameterFactoryMap.put(Date.class, new DateFactory(this));
    parameterFactoryMap.put(Class.class, new ClassFactory(this));
    parameterFactoryMap.put(InterfaceFactory.class, new InterfaceFactory(this));
  }
  
  void pushParameterFactoryFinder(ParameterFactoryFinder finder) {
    
    if (finder == null) {
      finder = new ParameterFactoryFinder() {
        public ParameterFactory findFactory(Class<?> clazz) {
          return null;
        }
      };
    }
    
    finderStack.push(finder);
  }
  
  void popParameterFactoryFinder() {
    
    finderStack.pop();
  }
  
  void setDatePattern(String pattern, String hint) {
    
    dateFormat = new SimpleDateFormat(pattern);
    dateHint = (hint == null) ? dateFormat.toPattern() : hint;
  }
  
  SimpleDateFormat getDateFormat() {
    
    return dateFormat;
  }
  
  String getDateHint() {
    
    return dateHint;
  }
  
  void setTrueValues(String[] trueValues) {
    
    this.trueValues = trueValues;
  }
  
  void setFalseValues(String[] falseValues) {
    
    this.falseValues = falseValues;
  }
  
  String[] getTrueValues() {
    
    if (trueValues == null) {
      trueValues = new String[] {"true", "yes", "on"};
    }
    return trueValues;
  }
  
  String[] getFalseValues() {
    
    if (falseValues == null) {
      falseValues = new String[] {"false", "no", "off"};
    }
    return falseValues;
  }
  
  void setDecimalPoint(Character decimalPoint) {
    
    this.decimalPoint = decimalPoint;
  }
  
  String prepareNumber(String value, Annotation[] annotations) throws Exception {
    
    String result = value.trim();
    checkPattern(result, annotations);
    
    if (decimalPoint == null) {
      result = result.replace(',', '.');
    } else {
      StringBuilder sb = new StringBuilder(result.length());
      for (int i = 0; i < result.length(); i++) {
        char c = result.charAt(i);
        if (c == ',') {
          if (decimalPoint == ',') {
            sb.append('.');
          }
        } else if (c == '.') {
          if (decimalPoint == '.') {
            sb.append('.');
          }
        } else {
          sb.append(c);
        }
      }
      result = sb.toString();
    }
    
    return result;
  }
  
  void checkPattern(String value, Annotation[] annotations) throws Exception {
    
    for (Annotation annotation : annotations) {
      if (annotation.getClass() == Pattern.class) {
        Pattern pattern = (Pattern) annotation;
        if (value.matches(pattern.value())) {
          return;
        } else {
          String hint = (pattern.hint().length() == 0) ? pattern.value() : pattern.hint();
          throw new ConfigException(text.get("parmPattern", hint));
        }
      }
    }
  }
  
  Object createParameter(ParameterFactory factory, String value, MethodProxy method) throws Exception {
    
    Class<?> parmType = method.getMethod().getParameterTypes()[0];
    Annotation[] annotations = method.getMethod().getAnnotations();
    return parmType.isArray() ? createArray(factory, value, parmType, annotations) : createParameter(factory, value, parmType, annotations);
  }
  
  private Object createArray(ParameterFactory factory, String value, Class<?> arrayType, Annotation[] annotations) throws Exception {
    
    String[] values = value.split(",");
    
    Class<?> componentType = arrayType.getComponentType();

    Object array = Array.newInstance(componentType, values.length);
    for (int i = 0; i < values.length; i++) {
      Array.set(array, i, createParameter(factory, values[i].trim(), componentType, annotations));
    }     
    return array;    
  }
  
  private Object createParameter(ParameterFactory factory, String value, Class<?> parmType, Annotation[] annotations) throws Exception {
    
    if (parmType.isPrimitive()) {
      parmType = primitivesMap.get(parmType);
    }
    
    Object parm = factory.createParameter(value, parmType, annotations);
    if (! isCorrectType(parm.getClass(), parmType)) {
      throw new ConfigException(text.get("wrongType", parm.getClass().getName(), parmType.getName()));
    }
    return parm;
  }
  
  private boolean isCorrectType(Class<?> created, Class<?> expected) {
    
    if (created == expected) {
      return true;
    }
    
    if (expected.isAssignableFrom(created)) {
      return true;
    }
    
    return false;
  }
  
  ParameterFactory getFactory(Class<?> parmType) throws Exception {
    
    ParameterFactory factory = null;
    
    int i = finderStack.size();
    while (factory == null && i > 0) {
      i--;
      factory = finderStack.get(i).findFactory(parmType);
    }
    
    if (factory == null) {
      throw new ConfigException(text.get("unsupportedType", parmType.getName()));
    }
    
    return factory;
  }
  
  public ParameterFactory findFactory(Class<?> parmType) throws ConfigException {
    
    if (parmType.isArray()) {
      parmType = parmType.getComponentType();
    }
    
    if (parmType.isPrimitive()) {
      parmType = primitivesMap.get(parmType);
    }
    
    ParameterFactory factory = parameterFactoryMap.get(parmType);
    
    if (factory == null) {      
      if (parmType.isEnum()) {
        factory = parameterFactoryMap.get(Enum.class);
      } else if (parmType.isInterface()) {
        factory = parameterFactoryMap.get(InterfaceFactory.class);
      } else {
        try {          
          parmType.getConstructor(); // public default constructor available ?
          factory = parameterFactoryMap.get(Object.class);
        } catch (NoSuchMethodException e) {
        }
      }
    }
    
    return factory;
  }
  
}
