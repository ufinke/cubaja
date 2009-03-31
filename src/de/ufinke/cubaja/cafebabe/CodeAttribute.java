// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeAttribute implements Generatable {

  private ConstantPool constantPool;
  private int nameIndex;
  private int currentStackSize;
  private int maxStackSize;
  private int maxLocals;
  private Map<String, Integer> localVariableMap;
  private ByteArrayOutputStream buffer;
  private Map<String, Label> labelMap;
  private List<Jump> jumpList;
  private int opCodeOffset;
  
  CodeAttribute(GenClass genClass, boolean isStatic, Type[] args) {
  
    constantPool = genClass.getConstantPool();
    nameIndex = constantPool.addName("Code");
    
    localVariableMap = new HashMap<String, Integer>();
    if (! isStatic) {
      getLocalVariable("this");
      maxLocals++;
    }
    for (int i = 0; i < args.length; i++) {
      String parmName = args[i].getParameterName();
      if (parmName == null) {
        parmName = "parm_" + (i + 1);
      }
      getLocalVariable(parmName);
      maxLocals += (args[i].getSize() == 1) ? 1 : 2;
    }
    
    labelMap = new HashMap<String, Label>();
    jumpList = new ArrayList<Jump>();
    buffer = new ByteArrayOutputStream();
  }
  
  private Label getLabel(String labelName) {
    
    Label label = labelMap.get(labelName);
    if (label == null) {
      label = new Label(labelName);
      labelMap.put(labelName, label);
    }
    return label;
  }
  
  private void createJump(int size, String labelName) {
        
    jumpList.add(new Jump(size, opCodeOffset, buffer.size(), getLabel(labelName)));

    for (int i = 0; i < size; i++) {
      write1(0);
    }
  }
  
  private int getLocalVariable(String name) {
    
    Integer index = localVariableMap.get(name);
    if (index == null) {
      index = maxLocals;
    }
    return index;
  }
  
  private void checkMaxLocals(int index) {
    
    maxLocals = Math.max(maxLocals, index);
  }
  
  private void push(int stackIncrement) {
    
    currentStackSize += stackIncrement;
    maxStackSize = Math.max(maxStackSize, currentStackSize);
  }
  
  private void pop(int stackDecrement) {
    
    currentStackSize -= stackDecrement;
  }
  
  private void writeOpCode(int opCode) {

    opCodeOffset = buffer.size();
    buffer.write(opCode);
  }
  
  private void write1(int value) {
    
    buffer.write(value);
  }
  
  private void write2(int value) {
    
    buffer.write(value >>> 8);
    buffer.write(value);
  }
  
  private void write4(int value) {
    
    buffer.write(value >>> 24);
    buffer.write(value >>> 16);
    buffer.write(value >>> 8);
    buffer.write(value);
  }
  
  public void defineLabel(String labelName) {
    
    getLabel(labelName).define(buffer.size(), currentStackSize);
  }
  
  public int getLocalVariable(String variableName, Type type) {
    
    int index = getLocalVariable(variableName);
    checkMaxLocals(index + type.getSize() - 1);
    return index;
  }
  
  public void loadNull() {
  
    writeOpCode(0x01); // aconst_null
    push(1);
  }
  
  public void loadConstant(int value) {
    
    switch (value) {
      case -1:
        writeOpCode(0x02); // iconst_m1
        break;
      case 0:
        writeOpCode(0x03); // iconst_0
        break;
      case 1:
        writeOpCode(0x04); // iconst_1
        break;
      case 2:
        writeOpCode(0x05); // iconst_2
        break;
      case 3:
        writeOpCode(0x06); // iconst_3
        break;
      case 4:
        writeOpCode(0x07); // iconst_4
        break;
      case 5:
        writeOpCode(0x08); // iconst_5
        break;
      default:
        if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
          writeOpCode(0x10); // bipush
          write1(value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
          writeOpCode(0x11); // sipush
          write2(value);
        } else {
          int index = constantPool.addInteger(value);
          if (index <= Byte.MAX_VALUE) {
            writeOpCode(0x12); // ldc
            write1(index);
          } else {
            writeOpCode(0x13); // ldc_w
            write2(index);
          }
        }
    }
    
    push(1);
  }
  
  public void loadConstant(long value) {

    if (value == 0L) {
      writeOpCode(0x09); // lconst_0
    } else if (value == 1L) {
      writeOpCode(0x0A); // lconst_1
    } else {
      writeOpCode(0x14); // ldc2_w
      write2(constantPool.addLong(value));
    }
    
    push(2);
  }
  
  public void loadConstant(float value) {

    if (value == 0.0F) {
      writeOpCode(0x0B); // fconst_0
    } else if (value == 1.0F) {
      writeOpCode(0x0C); // fconst_1
    } else if (value == 2.0F) {
      writeOpCode(0x0D); // fconst_2
    } else {
      int index = constantPool.addFloat(value);
      if (index <= Byte.MAX_VALUE) {
        writeOpCode(0x12); // ldc
        write1(index);
      } else {
        writeOpCode(0x13); // ldc_w
        write2(index);
      }
    }
    
    push(1);
  }
  
  public void loadConstant(double value) {

    if (value == 0.0D) {
      writeOpCode(0x0E); // dconst_0
    } else if (value == 1.0D) {
      writeOpCode(0x0F); // dconst_1
    } else {
      writeOpCode(0x14); // ldc2_w
      write2(constantPool.addDouble(value));
    }
    
    push(2);
  }
  
  public void loadConstant(String value) {
    
    int index = constantPool.addString(value);
    if (index <= Byte.MAX_VALUE) {
      writeOpCode(0x12); // ldc
      write1(index);
    } else {
      writeOpCode(0x13); // ldc_w
      write2(index);
    }
    
    push(1);
  }
  
  public void loadConstant(Class<?> value) {
    
    int index = constantPool.addClass(new Type(value));
    if (index <= Byte.MAX_VALUE) {
      writeOpCode(0x12); // ldc
      write1(index);
    } else {
      writeOpCode(0x13); // ldc_w
      write2(index);
    }
    
    push(1);
  }
  
  public void loadLocalInt(String variableName) {
    
    loadLocalInt(getLocalVariable(variableName));
  }
  
  public void loadLocalInt(int index) {
    
    checkMaxLocals(index);
    
    switch (index) {
      case 0:
        writeOpCode(0x1A); // iload_0
        break;
      case 1:
        writeOpCode(0x1B); // iload_1
        break;
      case 2:
        writeOpCode(0x1C); // iload_2
        break;
      case 3:
        writeOpCode(0x1D); // iload_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x15); // iload
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x15); // iload
          write2(index);
        }
    }
    
    push(1);
  }
  
  public void loadLocalLong(String variableName) {
    
    loadLocalLong(getLocalVariable(variableName));
  }
  
  public void loadLocalLong(int index) {
   
    checkMaxLocals(index + 1);
    
    switch (index) {
      case 0:
        writeOpCode(0x1E); // lload_0
        break;
      case 1:
        writeOpCode(0x1F); // lload_1
        break;
      case 2:
        writeOpCode(0x20); // lload_2
        break;
      case 3:
        writeOpCode(0x21); // lload_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x16); // lload
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x16); // lload
          write2(index);
        }
    }
    
    push(2);
  }
  
  public void loadLocalFloat(String variableName) {
    
    loadLocalFloat(getLocalVariable(variableName));
  }
  
  public void loadLocalFloat(int index) {
    
    checkMaxLocals(index);
    
    switch (index) {
      case 0:
        writeOpCode(0x22); // fload_0
        break;
      case 1:
        writeOpCode(0x23); // fload_1
        break;
      case 2:
        writeOpCode(0x24); // fload_2
        break;
      case 3:
        writeOpCode(0x25); // fload_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x17); // fload
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x17); // fload
          write2(index);
        }
    }
    
    push(1);
  }
  
  public void loadLocalDouble(String variableName) {
    
    loadLocalDouble(getLocalVariable(variableName));
  }
  
  public void loadLocalDouble(int index) {
   
    checkMaxLocals(index + 1);
    
    switch (index) {
      case 0:
        writeOpCode(0x26); // dload_0
        break;
      case 1:
        writeOpCode(0x27); // dload_1
        break;
      case 2:
        writeOpCode(0x28); // dload_2
        break;
      case 3:
        writeOpCode(0x29); // dload_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x18); // dload
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x18); // dload
          write2(index);
        }
    }
    
    push(2);
  }
  
  public void loadLocalReference(String variableName) {
    
    loadLocalReference(getLocalVariable(variableName));
  }
  
  public void loadLocalReference(int index) {
    
    checkMaxLocals(index);
    
    switch (index) {
      case 0:
        writeOpCode(0x2A); // aload_0
        break;
      case 1:
        writeOpCode(0x2B); // aload_1
        break;
      case 2:
        writeOpCode(0x2C); // aload_2
        break;
      case 3:
        writeOpCode(0x2D); // aload_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x19); // aload
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x19); // aload
          write2(index);
        }
    }
    
    push(1);
  }
  
  public void loadIntArrayElement() {
    
    writeOpCode(0x2E); // iaload
    pop(2);
    push(1);
  }
  
  public void loadLongArrayElement() {
    
    pop(2);
    writeOpCode(0x2F); // laload
    push(2);
  }
  
  public void loadFloatArrayElement() {
    
    pop(2);
    writeOpCode(0x30); // faload
    push(1);
  }
  
  public void loadDoubleArrayElement() {
    
    pop(2);
    writeOpCode(0x31); // laload
    push(2);
  }
  
  public void loadReferenceArrayElement() {
    
    pop(2);
    writeOpCode(0x32); // aaload
    push(1);
  }
  
  public void loadBooleanArrayElement() {
    
    pop(2);
    writeOpCode(0x33); // baload
    push(1);
  }
  
  public void loadByteArrayElement() {
    
    pop(2);
    writeOpCode(0x33); // baload
    push(1);
  }
  
  public void loadCharArrayElement() {
    
    pop(2);
    writeOpCode(0x34); // caload
    push(1);
  }
  
  public void loadShortArrayElement() {
    
    pop(2);
    writeOpCode(0x35); // saload
    push(1);
  }
  
  public void storeLocalInt(String variableName) {
    
    storeLocalInt(getLocalVariable(variableName));
  }
  
  public void storeLocalInt(int index) {
   
    checkMaxLocals(index);
    
    pop(1);
    
    switch (index) {
      case 0:
        writeOpCode(0x3B); // istore_0
        break;
      case 1:
        writeOpCode(0x3C); // istore_1
        break;
      case 2:
        writeOpCode(0x3D); // istore_2
        break;
      case 3:
        writeOpCode(0x3E); // istore_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x36); // istore
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x36); // istore
          write2(index);
        }
    }
  }
  
  public void storeLocalLong(String variableName) {
    
    storeLocalLong(getLocalVariable(variableName));
  }
  
  public void storeLocalLong(int index) {
    
    checkMaxLocals(index + 1);
    
    pop(2);
    
    switch (index) {
      case 0:
        writeOpCode(0x3F); // lstore_0
        break;
      case 1:
        writeOpCode(0x40); // lstore_1
        break;
      case 2:
        writeOpCode(0x41); // lstore_2
        break;
      case 3:
        writeOpCode(0x42); // lstore_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x37); // lstore
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x37); // lstore
          write2(index);
        }
    }
  }
  
  public void storeLocalFloat(String variableName) {
    
    storeLocalFloat(getLocalVariable(variableName));
  }
  
  public void storeLocalFloat(int index) {
    
    checkMaxLocals(index);
    
    pop(1);
    
    switch (index) {
      case 0:
        writeOpCode(0x43); // fstore_0
        break;
      case 1:
        writeOpCode(0x44); // fstore_1
        break;
      case 2:
        writeOpCode(0x45); // fstore_2
        break;
      case 3:
        writeOpCode(0x46); // fstore_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x38); // fstore
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x38); // fstore
          write2(index);
        }
    }
  }
  
  public void storeLocalDouble(String variableName) {
    
    storeLocalDouble(getLocalVariable(variableName));
  }
  
  public void storeLocalDouble(int index) {
    
    checkMaxLocals(index + 1);
    
    pop(2);
    
    switch (index) {
      case 0:
        writeOpCode(0x47); // dstore_0
        break;
      case 1:
        writeOpCode(0x48); // dstore_1
        break;
      case 2:
        writeOpCode(0x49); // dstore_2
        break;
      case 3:
        writeOpCode(0x4A); // dstore_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x39); // dstore
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x39); // dstore
          write2(index);
        }
    }
  }
  
  public void storeLocalReference(String variableName) {
    
    storeLocalReference(getLocalVariable(variableName));
  }
  
  public void storeLocalReference(int index) {
    
    checkMaxLocals(index);
    
    pop(1);
    
    switch (index) {
      case 0:
        writeOpCode(0x4B); // astore_0
        break;
      case 1:
        writeOpCode(0x4C); // astore_1
        break;
      case 2:
        writeOpCode(0x4D); // astore_2
        break;
      case 3:
        writeOpCode(0x4E); // astore_3
        break;
      default:
        if (index <= Byte.MAX_VALUE) {
          writeOpCode(0x3A); // astore
          write1(index);
        } else {
          writeOpCode(0xC4); // wide
          write1(0x3A); // astore
          write2(index);
        }
    }
  }
  
  public void storeIntArrayElement() {
    
    pop(3);
    writeOpCode(0x4F); // iastore
  }
  
  public void storeLongArrayElement() {
    
    pop(4);
    writeOpCode(0x50); // lastore
  }
  
  public void storeFloatArrayElement() {
    
    pop(3);
    writeOpCode(0x51); // fastore
  }
  
  public void storeDoubleArrayElement() {
    
    pop(4);
    writeOpCode(0x52); // lastore
  }
  
  public void storeReferenceArrayElement() {
    
    pop(3);
    writeOpCode(0x53); // aastore
  }
  
  public void storeBooleanArrayElement() {
    
    pop(3);
    writeOpCode(0x54); // bastore
  }
  
  public void storeByteArrayElement() {
    
    pop(3);
    writeOpCode(0x54); // bastore
  }
  
  public void storeCharArrayElement() {
    
    pop(3);
    writeOpCode(0x55); // castore
  }
  
  public void storeShortArrayElement() {
    
    pop(3);
    writeOpCode(0x56); // sastore
  }
  
  public void pop() {
    
    pop(1);
    writeOpCode(0x57); // pop
  }
  
  public void popDouble() {
    
    pop(2);
    writeOpCode(0x58); // pop2
  }
  
  public void duplicate() {
    
    writeOpCode(0x59); // dup
    push(1);
  }
  
  public void duplicateSkip() {
    
    writeOpCode(0x5A); // dup_x1
    push(1);
  }
  
  public void duplicateSkipDouble() {
    
    writeOpCode(0x5B); // dup_x2
    push(1);
  }
  
  public void duplicateDouble() {
    
    writeOpCode(0x5C); // dup2
    push(2);
  }
  
  public void duplicateDoubleSkip() {
    
    writeOpCode(0x5D); // dup2_x1
    push(2);
  }
  
  public void duplicateDoubleSkipDouble() {
    
    writeOpCode(0x5E); // dup2_x2
    push(2);
  }
  
  public void swap() {
    
    writeOpCode(0x5F); // swap
  }
  
  public void addInt() {
    
    pop(2);
    writeOpCode(0x60); // iadd
    push(1);
  }
  
  public void addLong() {
    
    pop(4);
    writeOpCode(0x61); // ladd
    push(2);
  }
  
  public void addFloat() {
    
    pop(2);
    writeOpCode(0x62); // fadd
    push(1);
  }
  
  public void addDouble() {
    
    pop(4);
    writeOpCode(0x63); // dadd
    push(2);
  }
  
  public void subtractInt() {
    
    pop(2);
    writeOpCode(0x64); // isub
    push(1);
  }
  
  public void subtractLong() {
    
    pop(4);
    writeOpCode(0x65); // lsub
    push(2);
  }
  
  public void subtractFloat() {
    
    pop(2);
    writeOpCode(0x66); // fsub
    push(1);
  }
  
  public void subtractDouble() {
    
    pop(4);
    writeOpCode(0x67); // dsub
    push(2);
  }
  
  public void multiplyInt() {
    
    pop(2);
    writeOpCode(0x68); // imul
    push(1);
  }
  
  public void mulitiplyLong() {
    
    pop(4);
    writeOpCode(0x69); // lmul
    push(2);
  }
  
  public void multiplyFloat() {
    
    pop(2);
    writeOpCode(0x6A); // fmul
    push(1);
  }
  
  public void multiplyDouble() {
    
    pop(4);
    writeOpCode(0x6B); // dmul
    push(2);
  }
  
  public void divideInt() {
    
    pop(2);
    writeOpCode(0x6C); // idiv
    push(1);
  }
  
  public void divideLong() {
    
    pop(4);
    writeOpCode(0x6D); // ldiv
    push(2);
  }
  
  public void divideFloat() {
    
    pop(2);
    writeOpCode(0x6E); // fdiv
    push(1);
  }
  
  public void divideDouble() {
    
    pop(4);
    writeOpCode(0x6F); // ddiv
    push(2);
  }
  
  public void remainderInt() {
    
    pop(2);
    writeOpCode(0x70); // irem
    push(1);
  }
  
  public void remainderLong() {
    
    pop(4);
    writeOpCode(0x71); // lrem
    push(2);
  }
  
  public void remainderFloat() {
    
    pop(2);
    writeOpCode(0x72); // frem
    push(1);
  }
  
  public void remainderDouble() {
    
    pop(4);
    writeOpCode(0x73); // drem
    push(2);
  }
  
  public void negateInt() {
    
    pop(1);
    writeOpCode(0x74); // ineg
    push(1);
  }
  
  public void negateLong() {
    
    pop(2);
    writeOpCode(0x75); // lneg
    push(2);
  }
  
  public void negateFloat() {
    
    pop(1);
    writeOpCode(0x76); // fneg
    push(1);
  }
  
  public void negateDouble() {
    
    pop(2);
    writeOpCode(0x77); // dneg
    push(2);
  }
  
  public void shiftLeftInt() {
    
    pop(2);
    writeOpCode(0x78); // ishl
    push(1);
  }
  
  public void shiftLeftLong() {
    
    pop(3);
    writeOpCode(0x79); // lshl
    push(2);
  }
  
  public void arithmeticShiftRightInt() {
    
    pop(2);
    writeOpCode(0x7A); // ishr
    push(1);
  }
  
  public void arithmeticShiftRightLong() {
    
    pop(3);
    writeOpCode(0x7B); // lshr
    push(2);
  }
  
  public void logicalShiftRightInt() {
    
    pop(2);
    writeOpCode(0x7C); // iushr
    push(1);
  }
  
  public void logicalShiftRightLong() {
    
    pop(3);
    writeOpCode(0x7D); // lushr
    push(2);
  }
  
  public void booleanAndInt() {
    
    pop(2);
    writeOpCode(0x7E); // iand
    push(1);
  }
  
  public void booleanAndLong() {
    
    pop(4);
    writeOpCode(0x7F); // land
    push(2);
  }
  
  public void booleanOrInt() {
    
    pop(2);
    writeOpCode(0x80); // ior
    push(1);
  }
  
  public void booleanOrLong() {
    
    pop(4);
    writeOpCode(0x81); // lor
    push(2);
  }
  
  public void booleanXorInt() {
    
    pop(2);
    writeOpCode(0x82); // ixor
    push(1);
  }
  
  public void booleanXorLong() {
    
    pop(4);
    writeOpCode(0x83); // lxor
    push(2);
  }
  
  public void incrementLocalInt(String variableName, int increment) {
    
    incrementLocalInt(getLocalVariable(variableName), increment);
  }
  
  public void incrementLocalInt(int index, int increment) {
    
    checkMaxLocals(index);
    
    if (index <= Byte.MAX_VALUE && increment <= Byte.MAX_VALUE && increment >= Byte.MIN_VALUE) {      
      writeOpCode(0x84); // iinc
      write1(index);
      write1(increment);
    } else if (increment <= Short.MAX_VALUE && increment >= Short.MIN_VALUE) {
      writeOpCode(0xC4); // wide
      write1(0x84); // iinc
      write2(index);
      write2(increment);
    } else {
      loadLocalInt(index);
      loadConstant(increment);
      addInt();
      storeLocalInt(index);
    }
  }
  
  public void convertIntToLong() {
    
    pop(1);
    writeOpCode(0x85); // i2l
    push(2);
  }
  
  public void convertIntToFloat() {
    
    pop(1);
    writeOpCode(0x86); // i2f
    push(1);
  }
  
  public void convertIntToDouble() {
    
    pop(1);
    writeOpCode(0x87); // i2d
    push(2);
  }
  
  public void convertLongToInt() {
    
    pop(2);
    writeOpCode(0x88); // l2i
    push(1);
  }
  
  public void convertLongToFloat() {
    
    pop(2);
    writeOpCode(0x89); // l2f
    push(1);
  }
  
  public void convertLongToDouble() {
    
    pop(2);
    writeOpCode(0x8A); // l2d
    push(2);
  }
  
  public void convertFloatToInt() {
    
    pop(1);
    writeOpCode(0x8B); // f2i
    push(1);
  }
  
  public void convertFloatToLong() {
    
    pop(1);
    writeOpCode(0x8C); // f2l
    push(2);
  }
  
  public void convertFloatToDouble() {
    
    pop(1);
    writeOpCode(0x8D); // f2d
    push(2);
  }
  
  public void convertDoubleToInt() {
    
    pop(2);
    writeOpCode(0x8E); // d2i
    push(1);
  }
  
  public void convertDoubleToLong() {
    
    pop(2);
    writeOpCode(0x8F); // d2l
    push(2);
  }
  
  public void convertDoubleToFloat() {
    
    pop(2);
    writeOpCode(0x90); // d2f
    push(1);
  }
  
  public void convertIntToByte() {
    
    pop(1);
    writeOpCode(0x91); // i2b
    push(1);
  }
  
  public void convertIntToChar() {
    
    pop(1);
    writeOpCode(0x92); // i2c
    push(1);
  }
  
  public void convertIntToShort() {
    
    pop(1);
    writeOpCode(0x93); // i2s
    push(1);
  }
  
  public void compareLong() {
    
    pop(4);
    writeOpCode(0x94); // lcmp
    push(1);
  }
  
  public void compareFloat(boolean nanIsMinus) {
    
    pop(2);
    writeOpCode(nanIsMinus ? 0x95 : 0x96); // fcmpg : fcmpl
    push(1);
  }
  
  public void compareDouble(boolean nanIsMinus) {
    
    pop(4);
    writeOpCode(nanIsMinus ? 0x97 : 0x98); // dcmpg : dcmpl
    push(1);
  }
  
  public void branchIfEqual(String labelName) {
    
    pop(1);
    writeOpCode(0x99); // ifeq
    createJump(2, labelName);
  }
  
  public void branchIfNotEqual(String labelName) {
    
    pop(1);
    writeOpCode(0x9A); // ifne
    createJump(2, labelName);
  }
  
  public void branchIfLess(String labelName) {
    
    pop(1);
    writeOpCode(0x9B); // iflt
    createJump(2, labelName);
  }
  
  public void branchIfGreaterEqual(String labelName) {
    
    pop(1);
    writeOpCode(0x9C); // ifge
    createJump(2, labelName);
  }
  
  public void branchIfGreater(String labelName) {
    
    pop(1);
    writeOpCode(0x9D); // ifgt
    createJump(2, labelName);
  }
  
  public void branchIfLessEqual(String labelName) {
    
    pop(1);
    writeOpCode(0x9E); // ifle
    createJump(2, labelName);
  }
  
  public void compareIntBranchIfEqual(String labelName) {
    
    pop(2);
    writeOpCode(0x9F); // if_icmpeq
    createJump(2, labelName);
  }
  
  public void compareIntBranchIfNotEqual(String labelName) {
    
    pop(2);
    writeOpCode(0xA0); // if_icmpne
    createJump(2, labelName);
  }
  
  public void compareIntBranchIfLess(String labelName) {
    
    pop(2);
    writeOpCode(0xA1); // if_icmplt
    createJump(2, labelName);
  }
  
  public void compareIntBranchIfGreaterEqual(String labelName) {
    
    pop(2);
    writeOpCode(0xA2); // if_icmpge
    createJump(2, labelName);
  }
  
  public void compareIntBranchIfGreater(String labelName) {
    
    pop(2);
    writeOpCode(0xA3); // if_icmpgt
    createJump(2, labelName);
  }
  
  public void compareIntBranchIfLessEqual(String labelName) {
    
    pop(2);
    writeOpCode(0xA4); // if_icmple
    createJump(2, labelName);
  }
  
  public void compareReferenceBranchIfEqual(String labelName) {
    
    pop(2);
    writeOpCode(0xA5); // if_acmpeq
    createJump(2, labelName);
  }
  
  public void compareReferenceBranchIfNotEqual(String labelName) {
    
    pop(2);
    writeOpCode(0xA6); // if_acmpne
    createJump(2, labelName);
  }
  
  public void branch(String labelName) {
    
    writeOpCode(0xA7); // goto
    createJump(2, labelName);
  }
  
  public void jumpSubroutine(String labelName) {
    
    writeOpCode(0xA8); // jsr
    createJump(2, labelName);
    push(1);
  }
  
  public void returnFromSubroutine(String variableName) {
    
    returnFromSubroutine(getLocalVariable(variableName));
  }
  
  public void returnFromSubroutine(int index) {
    
    checkMaxLocals(index);
    
    if (index <= Byte.MAX_VALUE) {
      writeOpCode(0xA9); // ret
      write1(index);
    } else {
      writeOpCode(0xC4); // wide
      write1(0xA9); // ret
      write2(index);
    }
  }
  
  public void tableswitch(BranchTable table) {
    
    codeSwitch(0xAA, table); // tableswitch
  }
  
  public void lookupswitch(BranchTable table) {
    
    codeSwitch(0xAB, table); // lookupswitch
  }
  
  private void codeSwitch(int opCode, BranchTable table) {
    
    if (table.getPairList().size() == 0) {
      if (table.getDefaultLabelName() != null) {
        branch(table.getDefaultLabelName()); // goto
      }
      return;
    }
        
    pop(1);
    
    writeOpCode(opCode); // tableswitch, lookupswitch
    
    while ((buffer.size() & 0x3) != 0) {
      write1(0);
    }
    
    createJump(4, table.getDefaultLabelName());
    
    switch (opCode) {
      case 0xAA:
        codeTableswitch(table);
        break;
      case 0xAB:
        codeLookupswitch(table);
        break;
    }
  }
  
  private void codeTableswitch(BranchTable table) {

    List<BranchTable.Pair> pairList = table.getPairList();
    
    int minKey = pairList.get(0).getKey();
    int maxKey = pairList.get(pairList.size() - 1).getKey();
    
    write4(minKey);
    write4(maxKey);

    int expectedKey = minKey; 
    int tableIndex = 0;
    
    while (expectedKey <= maxKey) {
      BranchTable.Pair pair = pairList.get(tableIndex++);
      while (expectedKey < pair.getKey()) {
        createJump(4, table.getDefaultLabelName());
        expectedKey++;
      }
      createJump(4, pair.getLabelName());
      expectedKey++;
    }
  }
  
  private void codeLookupswitch(BranchTable table) {
    
    List<BranchTable.Pair> pairList = table.getPairList();
    
    write4(pairList.size());
    
    for (BranchTable.Pair pair : pairList) {
      write4(pair.getKey());
      createJump(4, pair.getLabelName());
    }
  }
  
  public void returnInt() {
    
    pop(currentStackSize);
    writeOpCode(0xAC); // ireturn
  }
  
  public void returnLong() {
    
    pop(currentStackSize);
    writeOpCode(0xAD); // lreturn
  }
  
  public void returnFloat() {
    
    pop(currentStackSize);
    writeOpCode(0xAE); // freturn
  }
  
  public void returnDouble() {
    
    pop(currentStackSize);
    writeOpCode(0xAF); // dreturn
  }
  
  public void returnReference() {
    
    pop(currentStackSize);
    writeOpCode(0xB0); // areturn
  }
  
  public void returnVoid() {
    
    pop(currentStackSize);
    writeOpCode(0xB1); // return
  }
  
  public void getStatic(Type fieldClass, Type fieldType, String fieldName) {
    
    writeOpCode(0xB2); // getstatic
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
    push(1);
  }
  
  public void putStatic(Type fieldClass, Type fieldType, String fieldName) {

    pop(1);
    writeOpCode(0xB3); // putstatic
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
  }
  
  public void getField(Type fieldClass, Type fieldType, String fieldName) {
    
    pop(1);
    writeOpCode(0xB4); // getfield
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
    push(1);
  }
  
  public void putField(Type fieldClass, Type fieldType, String fieldName) {

    pop(2);
    writeOpCode(0xB5); // putfield
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
  }
  
  public void invokeVirtual(Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    invoke(0xB6, 1, methodClass, returnType, methodName, argTypes); // invokevirtual
  }
  
  public void invokeSpecial(Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    invoke(0xB7, 1, methodClass, returnType, methodName, argTypes); // invokespecial
  }
  
  public void invokeStatic(Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    invoke(0xB8, 0, methodClass, returnType, methodName, argTypes); // invokestatic
  }
  
  public void invokeInterface(Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    invoke(0xB9, 1, methodClass, returnType, methodName, argTypes); // invokeinterface
  }
  
  private void invoke(int opCode, int referenceCount, Type methodClass, Type returnType, String methodName, Type... argTypes) {
    
    int popCount = referenceCount;
    for (int i = 0; i < argTypes.length; i++) {
      popCount += argTypes[i].getSize();
    }
    pop(popCount);
    
    writeOpCode(opCode);
    write2(constantPool.addMethodref(methodClass, methodName, returnType, argTypes));
    
    if (opCode == 0xB9) { // invokeinterface (historical)     
      write1(popCount);
      write1(0);
    }
    
    push(returnType.getSize());
  }
  
  // opCode 0xBA is unused
  
  public void newObject(Type clazz) {
    
    writeOpCode(0xBB); // new
    write2(constantPool.addClass(clazz));
    push(1);
  }
  
  public void newArray(Type elementType) {
    
    pop(1);
    
    int arrayType = 0;
    switch (elementType.getDescriptor().charAt(0)) {
      case 'Z':
        arrayType = 4;
        break;
      case 'C':
        arrayType = 5;
        break;
      case 'F':
        arrayType = 6;
        break;
      case 'D':
        arrayType = 7;
        break;
      case 'B':
        arrayType = 8;
        break;
      case 'S':
        arrayType = 9;
        break;
      case 'I':
        arrayType = 10;
        break;
      case 'J':
        arrayType = 11;
        break;
    }

    if (arrayType != 0) {      
      writeOpCode(0xBC); // newarray
      write1(arrayType);
    } else {
      writeOpCode(0xBD); // anewarray
      write2(constantPool.addClass(elementType));
    }
    
    push(1);
  }
  
  public void arraylength() {
    
    pop(1);
    writeOpCode(0xBE); // arraylength
    push(1);
  }
  
  public void throwException() {
    
    pop(currentStackSize);
    writeOpCode(0xBF); // athrow
    push(1);
  }
  
  public void cast(Type checkedType) {
    
    writeOpCode(0xC0); // checkcast
    write2(constantPool.addClass(checkedType));
  }
  
  public void checkInstance(Type checkedType) {
    
    pop(1);
    writeOpCode(0xC1); // instanceof
    write2(constantPool.addClass(checkedType));
    push(1);
  }
  
  public void monitorEnter() {
    
    pop(1);
    writeOpCode(0xC2); // monitorenter
  }
  
  public void monitorExit() {
    
    pop(1);
    writeOpCode(0xC3); // monitorexit
  }
  
  // opCode 0xC4 (wide) handled with appropriate opCodes
  
  // opCode 0xC5 (multianewarray) not supported (too exotic use case; only available for reference arrays)
  
  public void branchIfNull(String labelName) {
    
    pop(1);
    writeOpCode(0xC6); // ifnull
    createJump(2, labelName);
  }
    
  public void branchIfNonNull(String labelName) {
    
    pop(1);
    writeOpCode(0xC7); // ifnonnull
    createJump(2, labelName);
  }

  // opCode 0xC8 (goto_w) not supported (would be nonsense, see VM Spec note)
  
  // opCode 0xC9 (jsr_w) not supported (would be nonsense, see VM Spec note)
  
  public void generate(DataOutputStream out) throws IOException {
    
    buffer.close();
    byte[] code = buffer.toByteArray();
    //TODO resolve jump / verify label
    
    out.writeShort(nameIndex);
    out.writeInt(12 + code.length);
    out.writeShort(maxStackSize);
    out.writeShort(maxLocals);
    out.writeInt(code.length);
    out.write(code);
    out.writeShort(0); // try / catch not implemented
    out.writeShort(0); // attributes
  }
}
