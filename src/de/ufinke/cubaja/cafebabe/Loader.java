// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class Loader extends ClassLoader {

  static private final String dumpDirectory = System.getProperty(Loader.class.getPackage().getName() + ".dump");
  static private final String classNamePrefix = Loader.class.getPackage() + ".Generated";
  
  public static Class<?> createClass(Generator generator, Object... nameSuffix) throws ClassNotFoundException {
    
    Loader loader = new Loader(generator, nameSuffix);
    
    StringBuilder sb = new StringBuilder(200);
    sb.append(classNamePrefix);
    for (int i = 0; i < nameSuffix.length; i++) {
      sb.append('_');
      Object suffix = nameSuffix[i];
      if (suffix.getClass() == Class.class) {
        Class<?> clazz = (Class<?>) suffix;
        suffix = clazz.getName();
      }
      sb.append(suffix.toString().replace('.', '_'));
    }
    String className = sb.toString();
    
    return loader.loadClass(className);
  }
  
  private Generator generator;
  
  private Loader(Generator generator, Object... nameSuffix) {
    
    this.generator = generator;
  }
  
  protected Class<?> findClass(String className) throws ClassNotFoundException {
    
    try {
      return doFindClass(className);
    } catch (Throwable t) {
      throw new ClassNotFoundException(className, t);
    }
  }
  
  private Class<?> doFindClass(String className) throws Exception {
    
    GenClass genClass = generator.generate(className);
    
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(buffer);
    genClass.generate(out);
    out.close();
    byte[] array = buffer.toByteArray();
    
    dump(className, array);
    
    Class<?> clazz = defineClass(className, array, 0, array.length);
    resolveClass(clazz);
    
    return clazz;
  }
  
  private void dump(String className, byte[] array) throws Exception {
    
    if (dumpDirectory == null) {
      return;
    }
    
    File classFile = new File(className.replace('.', '/'));
    String parent = classFile.getParent();
    String dirName = (parent == null) ? dumpDirectory : dumpDirectory + "/" + parent;
    File dir = new File(dirName);
    dir.mkdirs();
    File file = new File(dir, classFile.getName() + ".class");
    
    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
    stream.write(array);
    stream.close();
  }
}
