// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import de.ufinke.cubaja.util.Text;

public class Loader extends ClassLoader {

  static private final String dumpDirectory = System.getProperty(Loader.class.getPackage().getName() + ".dump");
  static private final Text text = new Text(Loader.class);
  
  static private final Loader loader = new Loader();
  static private final Map<String, Generator> generatorMap = new ConcurrentHashMap<String, Generator>(); 
  
  static public String createClassName(Class<?> owner, String prefix, Class<?> suffix) {
    
    StringBuilder sb = new StringBuilder(200);
    sb.append(owner.getPackage().getName());
    sb.append('.');
    sb.append(prefix);
    if (suffix != null) {      
      sb.append('_');
      sb.append(suffix.getName().replace('.', '_'));
    }
    return sb.toString();
  }
  
  static public void defineGenerator(String className, Generator generator) {
    
    generatorMap.put(className, generator);
  }

  static public Object createInstance(String className, Generator generator, Object... constructorArgs) throws Exception {
    
    defineGenerator(className, generator);
    
    Class<?> clazz = loader.loadClass(className);
    
    if (constructorArgs.length == 0) {
      return clazz.newInstance();
    }
    
    Class<?>[] argClasses = new Class<?>[constructorArgs.length];
    for (int i = 0; i < constructorArgs.length; i++) {
      argClasses[i] = constructorArgs[i].getClass();
    }
    Constructor<?> constructor = clazz.getConstructor(argClasses);
    return constructor.newInstance(constructorArgs);
  }
  
  static public ClassLoader getLoader() {
    
    return loader;
  }
  
  private Loader() {
    
  }
  
  protected Class<?> findClass(String className) throws ClassNotFoundException {
    
    try {
      return doFindClass(className);
    } catch (Throwable t) {
      throw new ClassNotFoundException(className, t);
    }
  }
  
  private Class<?> doFindClass(String className) throws Exception {
    
    Generator generator = generatorMap.get(className);
    if (generator == null) {
      throw new CafebabeException(text.get("noGenerator", className));
    }
    
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
