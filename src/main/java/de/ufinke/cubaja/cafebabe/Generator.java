// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

/**
 * Bytecode generator.
 * <p>
 * Example (the sample method in the generated class does nothing):
 * <pre>
 *   public GenClass generate(String className) throws Exception {
 *  
 *     Type objectType = new Type(Object.class);
 *     GenClass genClass = new GenClass(ACC_PUBLIC | ACC_FINAL, className, objectType);
 *   
 *     genClass.createDefaultConstructor();
 *   
 *     Type voidType = new Type(Void.TYPE);
 *     GenMethod method = genClass.createMethod(ACC_PUBLIC, voidType, "doNothing");
 *     CodeAttribute code = method.getCode();
 *     code.returnVoid();
 *  
 *     return genClass;
 *   }
 * </pre>
 * @author Uwe Finke
 */
public interface Generator extends AccessFlags {

  /**
   * Generates bytecode.
   * @param className
   * @return <tt>GenClass</tt> containing the bytecode.
   * @throws Exception
   */
  public GenClass generate(String className) throws Exception;
}
