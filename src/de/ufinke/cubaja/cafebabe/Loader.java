// Copyright (c) 2009 - 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * <tt>ClassLoader</tt> which loads generated bytecode.
 * A {@link Generator} must be set by {@link #setGenerator setGenerator} before {@link #findClass findClass} 
 * is called indirectly
 * by {@link java.lang.ClassLoader#loadClass loadClass}.
 * <p>
 * There may be a system property <tt>de.ufinke.cubaja.cafebabe.dump</tt> with a
 * path name in it's value. If present, the 'loaded' bytecode will be dumped into that
 * directory.
 * <p>
 * Because of the dependencies between <tt>setGenerator</tt> and
 * <tt>findClass</tt>, this class is not threadsafe.
 * @author Uwe Finke
 */
public class Loader extends ClassLoader {

  /**
   * Convenience method to generate and load a class.
   * <p>
   * Creates a <tt>Loader</tt> with the <tt>generator</tt>'s <tt>ClassLoader</tt> as parent loader
   * and passes the <tt>generator</tt> to it.
   * The name of the generated class is <tt>'Generated'</tt> plus
   * the given <tt>nameSuffix</tt>es, each separated by a hyphen.
   * If a <tt>nameSuffix</tt> is of type <tt>Class</tt>, the class name
   * is used as nameSuffix.
   * Dots in a <tt>nameSuffix</tt> are replaced by hyphens.
   * @param generator
   * @param nameSuffix
   * @return generated class
   * @throws ClassNotFoundException
   */
  public static Class<?> createClass(Generator generator, Object... nameSuffix) throws ClassNotFoundException {
    
    Loader loader = new Loader(generator.getClass().getClassLoader());
    loader.setGenerator(generator);
    
    StringBuilder sb = new StringBuilder(200);
    sb.append(generator.getClass().getPackage().getName());
    sb.append(".Generated");
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
  
  /**
   * Default constructor.
   * Parent class loader is the system class loader.
   */
  public Loader() {
    
  }
  
  /**
   * Constructor with explicit parent class loader.
   * @param parentLoader
   */
  public Loader(ClassLoader parentLoader) {
    
    super(parentLoader);
  }
  
  /**
   * Sets the generator.
   * @param generator
   */
  public void setGenerator(Generator generator) {
    
    this.generator = generator;
  }
  
  /**
   * Calls the generator and defines the <tt>Class</tt>.
   */
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
    
    String dumpDirectory = System.getProperty(Loader.class.getPackage().getName() + ".dump");
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
