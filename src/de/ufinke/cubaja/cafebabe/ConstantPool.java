package de.ufinke.cubaja.cafebabe;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ConstantPool implements Generatable {

  private int size;
  private List<Generatable> entryList;
  private Map<Object, Integer> entryMap;
  
  ConstantPool() {
    
    size = 1;
    entryList = new ArrayList<Generatable>();
    entryMap = new HashMap<Object, Integer>();
  }
  
  private int addUtf8(String string) {
    
    return addEntry(new ConstantEntryUtf8(string), 1);
  }
  
  int addName(String name) {
    
    return addUtf8(name);
  }
  
  int addFieldDescriptor(Type type) {
    
    return addUtf8(type.getDescriptor());
  }
  
  int addMethodDescriptor(Type returnType, Type[] args) {
    
    return addUtf8(createDescriptor(returnType, args));
  }
  
  int addInteger(int value) {

    return addEntry(new ConstantEntryInteger(value), 1);
  }
  
  int addFloat(float value) {

    return addEntry(new ConstantEntryFloat(value), 1);
  }
  
  int addLong(long value) {

    return addEntry(new ConstantEntryLong(value), 2);
  }
  
  int addDouble(double value) {

    return addEntry(new ConstantEntryDouble(value), 2);
  }
  
  int addClass(Type type) {

    String className = type.getClassName();    
    int utf8Index = addUtf8(className);
    return addEntry(new ConstantEntryClass(utf8Index), 1);
  }
  
  int addString(String string) {
    
    int utf8Index = addUtf8(string);
    return addEntry(new ConstantEntryString(utf8Index), 1);
  }
  
  int addFieldref(Type clazz, String fieldName, Type fieldType) {
    
    int classIndex = addClass(clazz);
    int nameAndTypeIndex = addNameAndType(fieldName, createDescriptor(fieldType, null));
    return addEntry(new ConstantEntryFieldref(classIndex, nameAndTypeIndex), 1);
  }
  
  int addMethodref(Type clazz, String methodName, Type returnType, Type[] argTypes) {
    
    int classIndex = addClass(clazz);
    int nameAndTypeIndex = addNameAndType(methodName, createDescriptor(returnType, argTypes));
    return addEntry(new ConstantEntryMethodref(classIndex, nameAndTypeIndex), 1);
  }
  
  int addInterfaceMethodref(Type clazz, String methodName, Type returnType, Type[] argTypes) {
    
    int classIndex = addClass(clazz);
    int nameAndTypeIndex = addNameAndType(methodName, createDescriptor(returnType, argTypes));
    return addEntry(new ConstantEntryInterfaceMethodref(classIndex, nameAndTypeIndex), 1);
  }
  
  private int addNameAndType(String name, String descriptor) {
    
    int nameIndex = addUtf8(name);
    int descriptorIndex = addUtf8(descriptor);
    return addEntry(new ConstantEntryNameAndType(nameIndex, descriptorIndex), 1);
  }
  
  private int addEntry(Generatable entry, int sizeIncrement) {
    
    Integer index = entryMap.get(entry);
    if (index == null) {      
      index = size;
      entryList.add(entry);
      entryMap.put(entry, index);
      size += sizeIncrement;
    }
    return index;
  }
  
  private String createDescriptor(Type type, Type[] args) {
    
    if (args == null) {
      return type.getDescriptor();
    }
    
    StringBuilder sb = new StringBuilder(255);
    
    sb.append('(');
    for (Type arg : args) {
      sb.append(arg.getDescriptor());
    }
    sb.append(')');

    sb.append(type.getDescriptor());
    
    return sb.toString();
  }
  
  public void generate(DataOutputStream out) throws IOException {
    
    out.writeShort(size);
    for (Generatable entry : entryList) {
      entry.generate(out);
    }
  }
}
