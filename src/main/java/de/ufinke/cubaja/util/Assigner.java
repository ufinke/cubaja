// Copyright (c) 2010 - 2020, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.util;

import java.lang.reflect.Method;
import de.ufinke.cubaja.cafebabe.CodeAttribute;
import de.ufinke.cubaja.cafebabe.GenClass;
import de.ufinke.cubaja.cafebabe.GenMethod;
import de.ufinke.cubaja.cafebabe.Generator;
import de.ufinke.cubaja.cafebabe.Loader;
import de.ufinke.cubaja.cafebabe.Type;

/**
 * Assigns values from one data object to another data object.
 * <p>
 * An instance is created by <code>create</code>.
 * The <code>assign</code> method of the instance takes
 * values from the getter methods of the source object
 * and passes them to the matching setter methods of the target object.
 * @author Uwe Finke
 */
abstract public class Assigner {

  static class AssignerGenerator implements Generator {

    private final Class<?> fromClass;
    private final Class<?> toClass;
    private final Type fromType;
    private final Type toType;
    
    private CodeAttribute code;
    
    AssignerGenerator(Class<?> fromClass, Class<?> toClass) {
    
      this.fromClass = fromClass;
      this.toClass = toClass;
      fromType = new Type(fromClass);
      toType = new Type(toClass);
    }
    
    public GenClass generate(String className) throws Exception {

      GenClass clazz = new GenClass(ACC_PUBLIC | ACC_FINAL, className, new Type(Assigner.class));
      clazz.createDefaultConstructor();
      
      GenMethod assign = clazz.createMethod(ACC_PUBLIC, Type.VOID, "assign", Type.OBJECT, Type.OBJECT);
      assign.addException(new Type(Exception.class));
      code = assign.getCode();
      
      code.loadLocalReference(2);
      code.cast(toType);
      code.loadLocalReference(1);
      code.cast(fromType);
      
      findMatchingMethods();
      
      code.returnVoid();
      
      return clazz;
    }
    
    private void findMatchingMethods() {
      
      for (Method getter : fromClass.getMethods()) {
        Class<?> dataClass = getter.getReturnType();
        if (getter.getParameterTypes().length == 0 && dataClass != Void.TYPE) {
          String getterName = getter.getName();
          int substrStart = 0;
          if (getterName.startsWith("get")) {
            substrStart = 3;
          } else if (getterName.startsWith("is")) {
            substrStart = 2;
          }
          try {
            Method setter = toClass.getMethod("set" + getterName.substring(substrStart), dataClass);
            generateAssign(getter, setter, dataClass);
          } catch (Exception e) {
          }
        }
      }
    }
    
    private void generateAssign(Method fromMethod, Method toMethod, Class<?> dataClass) {

      Type dataType = new Type(dataClass);
      
      code.duplicateDouble();
      code.invokeVirtual(fromType, dataType, fromMethod.getName());
      code.invokeVirtual(toType, Type.VOID, toMethod.getName(), dataType);
    }
  }
  
  /**
   * Craetes an instance.
   * @param fromClass class with getter methods
   * @param toClass class with setter methods
   * @return an instance
   * @throws Exception any exception
   */
  static public Assigner create(final Class<?> fromClass, final Class<?> toClass) throws Exception {
    
    AssignerGenerator generator = new AssignerGenerator(fromClass, toClass);
    Class<?> generated = Loader.createClass(toClass, generator, "Assigner", fromClass, toClass);
    return (Assigner) generated.newInstance();
  }
  
  /**
   * Protected constructor.
   */
  protected Assigner() {
    
  }
  
  /**
   * Assigns values from one object to another object.
   * @param from object with getter methods
   * @param to object with setter methods
   * @throws Exception any exception
   */
  abstract public void assign(Object from, Object to) throws Exception;
}
