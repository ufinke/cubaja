// Copyright (c) 2009, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.cafebabe;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.util.Text;

public class CodeAttribute implements Generatable {

  static private Text text = new Text(CodeAttribute.class);
  
  private ConstantPool constantPool;
  private int nameIndex;
  private int currentStack;
  private int maxStackSize;
  private int maxLocals;
  private Map<String, Integer> localVariableMap;
  private ByteArrayOutputStream buffer;
  private Map<String, Label> labelMap;
  private List<Jump> jumpList;
  private List<ExceptionHandlerDefinition> exceptionHandlerList;
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
    exceptionHandlerList = new ArrayList<ExceptionHandlerDefinition>();
    buffer = new ByteArrayOutputStream();
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
  
  private void incrementStack(int increment) {
    
    currentStack += increment;
    maxStackSize = Math.max(maxStackSize, currentStack);
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
  
  private Label getLabel(String labelName) {
    
    Label label = labelMap.get(labelName);
    if (label == null) {
      label = new Label(labelName);
      labelMap.put(labelName, label);
    }
    return label;
  }
  
  private void checkLabelStack(Label label) {
    
    currentStack = label.stackSize(currentStack);
  }
  
  private void createJump(int size, String labelName) {
        
    Label label = getLabel(labelName);
    
    jumpList.add(new Jump(size, opCodeOffset, buffer.size(), label));

    for (int i = 0; i < size; i++) {
      write1(0);
    }
    
    checkLabelStack(label);
  }
  
  public void defineLabel(String labelName) {
    
    Label label = getLabel(labelName);
    label.define(buffer.size());
    checkLabelStack(label);
  }
  
  public void defineExceptionHandler(String startLabelName, String endLabelName, Type exceptionType, String handlerLabelName) {
    
    exceptionHandlerList.add(new ExceptionHandlerDefinition(getLabel(startLabelName), getLabel(endLabelName), exceptionType, getLabel(handlerLabelName)));
  }
  
  public int getLocalVariable(String variableName, Type type) {
    
    int index = getLocalVariable(variableName);
    checkMaxLocals(index + type.getSize() - 1);
    return index;
  }

  public void nop() {
    
    writeOpCode(0x00); // nop
  }
  
  public void loadNull() {
  
    writeOpCode(0x01); // aconst_null
    incrementStack(1);
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
    
    incrementStack(1);
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
    
    incrementStack(2);
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
    
    incrementStack(1);
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
    
    incrementStack(2);
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
    
    incrementStack(1);
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
    
    incrementStack(1);
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
    
    incrementStack(1);
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
    
    incrementStack(2);
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
    
    incrementStack(1);
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
    
    incrementStack(2);
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
    
    incrementStack(1);
  }
  
  public void loadIntArrayElement() {
    
    writeOpCode(0x2E); // iaload
    currentStack--;
  }
  
  public void loadLongArrayElement() {
    
    writeOpCode(0x2F); // laload
  }
  
  public void loadFloatArrayElement() {
    
    writeOpCode(0x30); // faload
    currentStack--;
  }
  
  public void loadDoubleArrayElement() {
    
    writeOpCode(0x31); // laload
  }
  
  public void loadReferenceArrayElement() {
    
    writeOpCode(0x32); // aaload
    currentStack--;
  }
  
  public void loadBooleanArrayElement() {
    
    writeOpCode(0x33); // baload
    currentStack--;
  }
  
  public void loadByteArrayElement() {
    
    writeOpCode(0x33); // baload
    currentStack--;
  }
  
  public void loadCharArrayElement() {
    
    writeOpCode(0x34); // caload
    currentStack--;
  }
  
  public void loadShortArrayElement() {
    
    writeOpCode(0x35); // saload
    currentStack--;
  }
  
  public void storeLocalInt(String variableName) {
    
    storeLocalInt(getLocalVariable(variableName));
  }
  
  public void storeLocalInt(int index) {
   
    checkMaxLocals(index);
    
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
    
    currentStack--;
  }
  
  public void storeLocalLong(String variableName) {
    
    storeLocalLong(getLocalVariable(variableName));
  }
  
  public void storeLocalLong(int index) {
    
    checkMaxLocals(index + 1);
    
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
    
    currentStack -= 2;
  }
  
  public void storeLocalFloat(String variableName) {
    
    storeLocalFloat(getLocalVariable(variableName));
  }
  
  public void storeLocalFloat(int index) {
    
    checkMaxLocals(index);
    
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
    
    currentStack--;
  }
  
  public void storeLocalDouble(String variableName) {
    
    storeLocalDouble(getLocalVariable(variableName));
  }
  
  public void storeLocalDouble(int index) {
    
    checkMaxLocals(index + 1);
    
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
    
    currentStack -= 2;
  }
  
  public void storeLocalReference(String variableName) {
    
    storeLocalReference(getLocalVariable(variableName));
  }
  
  public void storeLocalReference(int index) {
    
    checkMaxLocals(index);
    
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
    
    currentStack--;
  }
  
  public void storeIntArrayElement() {
    
    writeOpCode(0x4F); // iastore
    currentStack -= 3;
  }
  
  public void storeLongArrayElement() {
    
    writeOpCode(0x50); // lastore
    currentStack -= 4;
  }
  
  public void storeFloatArrayElement() {
    
    writeOpCode(0x51); // fastore
    currentStack -= 3;
  }
  
  public void storeDoubleArrayElement() {
    
    writeOpCode(0x52); // lastore
    currentStack -= 4;
  }
  
  public void storeReferenceArrayElement() {
    
    writeOpCode(0x53); // aastore
    currentStack -= 3;
  }
  
  public void storeBooleanArrayElement() {
    
    writeOpCode(0x54); // bastore
    currentStack -= 3;
  }
  
  public void storeByteArrayElement() {
    
    writeOpCode(0x54); // bastore
    currentStack -= 3;
  }
  
  public void storeCharArrayElement() {
    
    writeOpCode(0x55); // castore
    currentStack -= 3;
  }
  
  public void storeShortArrayElement() {
    
    writeOpCode(0x56); // sastore
    currentStack -= 3;
  }
  
  public void pop() {
    
    writeOpCode(0x57); // pop
    currentStack--;
  }
  
  public void popDouble() {
    
    writeOpCode(0x58); // pop2
    currentStack -= 2;
  }
  
  public void duplicate() {
    
    writeOpCode(0x59); // dup
    incrementStack(1);
  }
  
  public void duplicateSkip() {
    
    writeOpCode(0x5A); // dup_x1
    incrementStack(1);
  }
  
  public void duplicateSkipDouble() {
    
    writeOpCode(0x5B); // dup_x2
    incrementStack(1);
  }
  
  public void duplicateDouble() {
    
    writeOpCode(0x5C); // dup2
    incrementStack(2);
  }
  
  public void duplicateDoubleSkip() {
    
    writeOpCode(0x5D); // dup2_x1
    incrementStack(2);
  }
  
  public void duplicateDoubleSkipDouble() {
    
    writeOpCode(0x5E); // dup2_x2
    incrementStack(2);
  }
  
  public void swap() {
    
    writeOpCode(0x5F); // swap
  }
  
  public void addInt() {
    
    writeOpCode(0x60); // iadd
    currentStack--;
  }
  
  public void addLong() {
    
    writeOpCode(0x61); // ladd
    currentStack -= 2;
  }
  
  public void addFloat() {
    
    writeOpCode(0x62); // fadd
    currentStack--;
  }
  
  public void addDouble() {
    
    writeOpCode(0x63); // dadd
    currentStack -= 2;
  }
  
  public void subtractInt() {
    
    writeOpCode(0x64); // isub
    currentStack--;
  }
  
  public void subtractLong() {
    
    writeOpCode(0x65); // lsub
    currentStack -= 2;
  }
  
  public void subtractFloat() {
    
    writeOpCode(0x66); // fsub
    currentStack--;
  }
  
  public void subtractDouble() {
    
    writeOpCode(0x67); // dsub
    currentStack -= 2;
  }
  
  public void multiplyInt() {
    
    writeOpCode(0x68); // imul
    currentStack--;
  }
  
  public void mulitiplyLong() {
    
    writeOpCode(0x69); // lmul
    currentStack -= 2;
  }
  
  public void multiplyFloat() {
    
    writeOpCode(0x6A); // fmul
    currentStack--;
  }
  
  public void multiplyDouble() {
    
    writeOpCode(0x6B); // dmul
    currentStack -= 2;
  }
  
  public void divideInt() {
    
    writeOpCode(0x6C); // idiv
    currentStack--;
  }
  
  public void divideLong() {
    
    writeOpCode(0x6D); // ldiv
    currentStack -= 2;
  }
  
  public void divideFloat() {
    
    writeOpCode(0x6E); // fdiv
    currentStack--;
  }
  
  public void divideDouble() {
    
    writeOpCode(0x6F); // ddiv
    currentStack -= 2;
  }
  
  public void remainderInt() {
    
    writeOpCode(0x70); // irem
    currentStack--;
  }
  
  public void remainderLong() {
    
    writeOpCode(0x71); // lrem
    currentStack -= 2;
  }
  
  public void remainderFloat() {
    
    writeOpCode(0x72); // frem
    currentStack--;
  }
  
  public void remainderDouble() {
    
    writeOpCode(0x73); // drem
    currentStack -= 2;
  }
  
  public void negateInt() {
    
    writeOpCode(0x74); // ineg
  }
  
  public void negateLong() {
    
    writeOpCode(0x75); // lneg
  }
  
  public void negateFloat() {
    
    writeOpCode(0x76); // fneg
  }
  
  public void negateDouble() {
    
    writeOpCode(0x77); // dneg
  }
  
  public void shiftLeftInt() {
    
    writeOpCode(0x78); // ishl
    currentStack--;
  }
  
  public void shiftLeftLong() {
    
    writeOpCode(0x79); // lshl
    currentStack--;
  }
  
  public void arithmeticShiftRightInt() {
    
    writeOpCode(0x7A); // ishr
    currentStack--;
  }
  
  public void arithmeticShiftRightLong() {
    
    writeOpCode(0x7B); // lshr
    currentStack--;
  }
  
  public void logicalShiftRightInt() {
    
    writeOpCode(0x7C); // iushr
    currentStack--;
  }
  
  public void logicalShiftRightLong() {
    
    writeOpCode(0x7D); // lushr
    currentStack--;
  }
  
  public void booleanAndInt() {
    
    writeOpCode(0x7E); // iand
    currentStack--;
  }
  
  public void booleanAndLong() {
    
    writeOpCode(0x7F); // land
    currentStack -= 2;
  }
  
  public void booleanOrInt() {
    
    writeOpCode(0x80); // ior
    currentStack--;
  }
  
  public void booleanOrLong() {
    
    writeOpCode(0x81); // lor
    currentStack -= 2;
  }
  
  public void booleanXorInt() {
    
    writeOpCode(0x82); // ixor
    currentStack--;
  }
  
  public void booleanXorLong() {
    
    writeOpCode(0x83); // lxor
    currentStack -= 2;
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
    
    writeOpCode(0x85); // i2l
    incrementStack(1);
  }
  
  public void convertIntToFloat() {
    
    writeOpCode(0x86); // i2f
  }
  
  public void convertIntToDouble() {
    
    writeOpCode(0x87); // i2d
    incrementStack(1);
  }
  
  public void convertLongToInt() {
    
    writeOpCode(0x88); // l2i
    currentStack--;
  }
  
  public void convertLongToFloat() {
    
    writeOpCode(0x89); // l2f
    currentStack--;
  }
  
  public void convertLongToDouble() {
    
    writeOpCode(0x8A); // l2d
  }
  
  public void convertFloatToInt() {
    
    writeOpCode(0x8B); // f2i
  }
  
  public void convertFloatToLong() {
    
    writeOpCode(0x8C); // f2l
    incrementStack(1);
  }
  
  public void convertFloatToDouble() {
    
    writeOpCode(0x8D); // f2d
    incrementStack(1);
  }
  
  public void convertDoubleToInt() {
    
    writeOpCode(0x8E); // d2i
    currentStack--;
  }
  
  public void convertDoubleToLong() {
    
    writeOpCode(0x8F); // d2l
  }
  
  public void convertDoubleToFloat() {
    
    writeOpCode(0x90); // d2f
    currentStack--;
  }
  
  public void convertIntToByte() {
    
    writeOpCode(0x91); // i2b
  }
  
  public void convertIntToChar() {
    
    writeOpCode(0x92); // i2c
  }
  
  public void convertIntToShort() {
    
    writeOpCode(0x93); // i2s
  }
  
  public void compareLong() {
    
    writeOpCode(0x94); // lcmp
    currentStack -= 3;
  }
  
  public void compareFloat(boolean nanIsMinus) {
    
    writeOpCode(nanIsMinus ? 0x95 : 0x96); // fcmpg : fcmpl
    currentStack--;
  }
  
  public void compareDouble(boolean nanIsMinus) {
    
    writeOpCode(nanIsMinus ? 0x97 : 0x98); // dcmpg : dcmpl
    currentStack -= 3;
  }
  
  public void branchIfEqual(String labelName) {
    
    writeOpCode(0x99); // ifeq
    currentStack--;
    createJump(2, labelName);
  }
  
  public void branchIfNotEqual(String labelName) {
    
    writeOpCode(0x9A); // ifne
    currentStack--;
    createJump(2, labelName);
  }
  
  public void branchIfLess(String labelName) {
    
    writeOpCode(0x9B); // iflt
    currentStack--;
    createJump(2, labelName);
  }
  
  public void branchIfGreaterEqual(String labelName) {
    
    writeOpCode(0x9C); // ifge
    currentStack--;
    createJump(2, labelName);
  }
  
  public void branchIfGreater(String labelName) {
    
    writeOpCode(0x9D); // ifgt
    currentStack--;
    createJump(2, labelName);
  }
  
  public void branchIfLessEqual(String labelName) {
    
    writeOpCode(0x9E); // ifle
    currentStack--;
    createJump(2, labelName);
  }
  
  public void compareIntBranchIfEqual(String labelName) {
    
    writeOpCode(0x9F); // if_icmpeq
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  public void compareIntBranchIfNotEqual(String labelName) {
    
    writeOpCode(0xA0); // if_icmpne
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  public void compareIntBranchIfLess(String labelName) {
    
    writeOpCode(0xA1); // if_icmplt
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  public void compareIntBranchIfGreaterEqual(String labelName) {
    
    writeOpCode(0xA2); // if_icmpge
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  public void compareIntBranchIfGreater(String labelName) {
    
    writeOpCode(0xA3); // if_icmpgt
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  public void compareIntBranchIfLessEqual(String labelName) {
    
    writeOpCode(0xA4); // if_icmple
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  public void compareReferenceBranchIfEqual(String labelName) {
    
    writeOpCode(0xA5); // if_acmpeq
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  public void compareReferenceBranchIfNotEqual(String labelName) {
    
    writeOpCode(0xA6); // if_acmpne
    currentStack -= 2;
    createJump(2, labelName);
  }
  
  public void branch(String labelName) {
    
    writeOpCode(0xA7); // goto
    createJump(2, labelName);
    currentStack = 0;
  }
  
  public void jumpSubroutine(String labelName) {
    
    writeOpCode(0xA8); // jsr
    incrementStack(1);
    createJump(2, labelName);
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
        
    currentStack--;
    
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
    
    currentStack = 0;
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
    
    writeOpCode(0xAC); // ireturn
    currentStack = 0;
  }
  
  public void returnLong() {
    
    writeOpCode(0xAD); // lreturn
    currentStack = 0;
  }
  
  public void returnFloat() {
    
    writeOpCode(0xAE); // freturn
    currentStack = 0;
  }
  
  public void returnDouble() {
    
    writeOpCode(0xAF); // dreturn
    currentStack = 0;
  }
  
  public void returnReference() {
    
    writeOpCode(0xB0); // areturn
    currentStack = 0;
  }
  
  public void returnVoid() {
    
    writeOpCode(0xB1); // return
    currentStack = 0;
  }
  
  public void getStatic(Type fieldClass, Type fieldType, String fieldName) {
    
    writeOpCode(0xB2); // getstatic
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
    incrementStack(1);
  }
  
  public void putStatic(Type fieldClass, Type fieldType, String fieldName) {

    writeOpCode(0xB3); // putstatic
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
    currentStack--;
  }
  
  public void getField(Type fieldClass, Type fieldType, String fieldName) {
    
    writeOpCode(0xB4); // getfield
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
  }
  
  public void putField(Type fieldClass, Type fieldType, String fieldName) {

    writeOpCode(0xB5); // putfield
    write2(constantPool.addFieldref(fieldClass, fieldName, fieldType));
    currentStack -= 2;
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
    currentStack -= popCount;
    
    writeOpCode(opCode);
    write2(constantPool.addMethodref(methodClass, methodName, returnType, argTypes));
    
    if (opCode == 0xB9) { // invokeinterface (historical)     
      write1(popCount);
      write1(0);
    }
    
    incrementStack(returnType.getSize());
  }
  
  public void newObject(Type clazz) {
    
    writeOpCode(0xBB); // new
    write2(constantPool.addClass(clazz));
    incrementStack(1);
  }
  
  public void newArray(Type elementType) {
    
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
  }
  
  public void arraylength() {
    
    writeOpCode(0xBE); // arraylength
  }
  
  public void throwException() {

    writeOpCode(0xBF); // athrow
    currentStack = 0;
    incrementStack(1);
  }
  
  public void cast(Type checkedType) {
    
    writeOpCode(0xC0); // checkcast
    write2(constantPool.addClass(checkedType));
  }
  
  public void checkInstance(Type checkedType) {
    
    writeOpCode(0xC1); // instanceof
    write2(constantPool.addClass(checkedType));
  }
  
  public void monitorEnter() {
    
    writeOpCode(0xC2); // monitorenter
    currentStack--;
  }
  
  public void monitorExit() {
    
    writeOpCode(0xC3); // monitorexit
    currentStack--;
  }
  
  public void newMultiReferenceArray(Type clazz, int dimensions) {
    
    writeOpCode(0xC5); // multianewarray
    write2(constantPool.addClass(clazz));
    write1(dimensions);
    currentStack -= (dimensions - 1);
  }
  
  public void branchIfNull(String labelName) {
    
    writeOpCode(0xC6); // ifnull
    currentStack--;
    createJump(2, labelName);
  }
    
  public void branchIfNonNull(String labelName) {
    
    writeOpCode(0xC7); // ifnonnull
    currentStack--;
    createJump(2, labelName);
  }

  public void branchFar(String labelName) {
    
    writeOpCode(0xC8); // goto_w
    createJump(4, labelName);
    currentStack = 0;
  }
  
  public void jumpFarSubroutine(String labelName) {
    
    writeOpCode(0xC9); // jsr_w
    incrementStack(1);
    createJump(4, labelName);
  }
  
  public void generate(DataOutputStream out) throws Exception {
    
    buffer.close();
    byte[] code = buffer.toByteArray();
    resolveJumpList(code);
    
    out.writeShort(nameIndex);
    out.writeInt(12 + code.length + exceptionHandlerList.size() * 8);
    out.writeShort(maxStackSize);
    out.writeShort(maxLocals);
    out.writeInt(code.length);
    out.write(code);
    generateExceptionHandlers(out);
    out.writeShort(0); // attributes
  }
  
  private void resolveJumpList(byte[] code) throws Exception {
    
    for (Jump jump : jumpList) {
      resolveJump(jump, code);
    }
  }
  
  private void resolveJump(Jump jump, byte[] code) throws Exception {
    
    Label label = jump.getLabel();
    if (! label.isDefined()) {
      throw new CafebabeException(text.get("undefinedLabel", label.getName()));
    }
    
    int distance = label.getOffset() - jump.getOpCodeOffset();
    int index = jump.getJumpOffset();
    
    switch (jump.getSize()) {
      case 4:
        code[index++] = (byte) (0xff & (distance >> 24));
        code[index++] = (byte) (0xff & (distance >> 16));
      case 2:
        code[index++] = (byte) (0xff & (distance >> 8));
        code[index]   = (byte) (0xff & distance);
    }
  }
  
  private void generateExceptionHandlers(DataOutputStream out) throws Exception {

    out.writeShort(exceptionHandlerList.size());
    
    for (ExceptionHandlerDefinition handler : exceptionHandlerList) {
      generateProgramCounter(out, handler.getStartLabel());
      generateProgramCounter(out, handler.getEndLabel());
      generateProgramCounter(out, handler.getHandlerLabel());
      if (handler.getExceptionType() == null) {
        out.writeShort(0);
      } else {
        out.writeShort(constantPool.addClass(handler.getExceptionType()));
      }
    }
  }
  
  private void generateProgramCounter(DataOutputStream out, Label label) throws Exception {
    
    if (! label.isDefined()) {
      throw new CafebabeException(text.get("undefinedLabel", label.getName()));
    }
    
    out.writeShort(label.getOffset());
  }
}
