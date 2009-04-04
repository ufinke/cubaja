// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import de.ufinke.cubaja.util.Text;

public class GenClassLoader extends ClassLoader {

  static private Text text = new Text(GenClassLoader.class);
  static private String dumpDirectory = System.getProperty(GenClassLoader.class.getPackage().getName() + ".dump");

  private Generator currentGenerator;
  
  public GenClassLoader() {
  
  }
  
  public GenClassLoader(ClassLoader parentClassLoader) {
    
    super(parentClassLoader);
  }
  
  public synchronized Object createInstance(Generator generator) throws Exception {
  
    currentGenerator = generator;
    Class<?> clazz = loadClass(currentGenerator.getClassName());
    return clazz.newInstance();
  }
  
  protected Class<?> findClass(String className) throws ClassNotFoundException {
    
    try {
      return doFindClass(className);
    } catch (Throwable t) {
      throw new ClassNotFoundException(className, t);
    }
  }
  
  private Class<?> doFindClass(String className) throws Exception {
    
    if (currentGenerator == null) {
      throw new CafebabeException(text.get("noGenerator", className));
    }
    
    GenClass genClass = currentGenerator.generate();
    
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(buffer);
    genClass.generate(out);
    out.close();
    byte[] array = buffer.toByteArray();
    
    dump(className, array);
    
    Class<?> clazz = defineClass(className, array, 0, array.length);
    resolveClass(clazz);
    
    currentGenerator = null;
    
    return clazz;
  }
  
  private void dump(String className, byte[] array) throws Exception {
    
    if (dumpDirectory == null) {
      return;
    }
    
    File classFile = new File(className.replace('.', '/'));
    String parent = classFile.getParent();
    if (parent != null) {
      dumpDirectory = dumpDirectory + "/" + parent;
    }
    File dir = new File(dumpDirectory);
    dir.mkdirs();
    File file = new File(dir, classFile.getName() + ".class");
    
    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
    stream.write(array);
    stream.close();
  }
}
