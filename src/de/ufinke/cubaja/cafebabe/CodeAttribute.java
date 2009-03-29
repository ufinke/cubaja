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

  public void defineLabel(String name) {
    
    Label label = labelMap.get(name);
    if (label == null) {
      label = new Label();
    }
    label.setOffset(buffer.size());
    label.setStackSize(currentStackSize);
    labelMap.put(name, label);
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
  
  public void loadLocalObject(String variableName) {
    
    loadLocalObject(getLocalVariable(variableName));
  }
  
  public void loadLocalObject(int index) {
    
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
    
    writeOpCode(0x2F); // laload
    pop(2);
    push(2);
  }
  
  public void loadFloatArrayElement() {
    
    writeOpCode(0x30); // faload
    pop(2);
    push(1);
  }
  
  public void loadDoubleArrayElement() {
    
    writeOpCode(0x31); // laload
    pop(2);
    push(2);
  }
  
  public void loadObjectArrayElement() {
    
    writeOpCode(0x32); // aaload
    pop(2);
    push(1);
  }
  
  public void loadBooleanArrayElement() {
    
    writeOpCode(0x33); // baload
    pop(2);
    push(1);
  }
  
  public void loadByteArrayElement() {
    
    writeOpCode(0x33); // baload
    pop(2);
    push(1);
  }
  
  public void loadCharArrayElement() {
    
    writeOpCode(0x34); // caload
    pop(2);
    push(1);
  }
  
  public void loadShortArrayElement() {
    
    writeOpCode(0x35); // saload
    pop(2);
    push(1);
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
    
    pop(1);
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
    
    pop(2);
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
    
    pop(1);
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
    
    pop(2);
  }
  
  public void storeLocalObject(String variableName) {
    
    storeLocalObject(getLocalVariable(variableName));
  }
  
  public void storeLocalObject(int index) {
    
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
    
    pop(1);
  }
  
  public void storeIntArrayElement() {
    
    writeOpCode(0x4F); // iastore
    pop(3);
  }
  
  public void storeLongArrayElement() {
    
    writeOpCode(0x50); // lastore
    pop(4);
  }
  
  public void storeFloatArrayElement() {
    
    writeOpCode(0x51); // fastore
    pop(3);
  }
  
  public void storeDoubleArrayElement() {
    
    writeOpCode(0x52); // lastore
    pop(4);
  }
  
  public void storeObjectArrayElement() {
    
    writeOpCode(0x53); // aastore
    pop(3);
  }
  
  public void storeBooleanArrayElement() {
    
    writeOpCode(0x54); // bastore
    pop(3);
  }
  
  public void storeByteArrayElement() {
    
    writeOpCode(0x54); // bastore
    pop(3);
  }
  
  public void storeCharArrayElement() {
    
    writeOpCode(0x55); // castore
    pop(3);
  }
  
  public void storeShortArrayElement() {
    
    writeOpCode(0x56); // sastore
    pop(3);
  }
  
  public void pop() {
    
    writeOpCode(0x57); // pop
    pop(1);
  }
  
  public void popDouble() {
    
    writeOpCode(0x58); // pop2
    pop(2);
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
    
    writeOpCode(0x60); // iadd
    pop(2);
    push(1);
  }
  
  public void addLong() {
    
    writeOpCode(0x61); // ladd
    pop(4);
    push(2);
  }
  
  public void addFloat() {
    
    writeOpCode(0x62); // fadd
    pop(2);
    push(1);
  }
  
  public void addDouble() {
    
    writeOpCode(0x63); // dadd
    pop(4);
    push(2);
  }
  
  public void subtractInt() {
    
    writeOpCode(0x64); // isub
    pop(2);
    push(1);
  }
  
  public void subtractLong() {
    
    writeOpCode(0x65); // lsub
    pop(4);
    push(2);
  }
  
  public void subtractFloat() {
    
    writeOpCode(0x66); // fsub
    pop(2);
    push(1);
  }
  
  public void subtractDouble() {
    
    writeOpCode(0x67); // dsub
    pop(4);
    push(2);
  }
  
  public void multiplyInt() {
    
    writeOpCode(0x68); // imul
    pop(2);
    push(1);
  }
  
  public void mulitiplyLong() {
    
    writeOpCode(0x69); // lmul
    pop(4);
    push(2);
  }
  
  public void multiplyFloat() {
    
    writeOpCode(0x6A); // fmul
    pop(2);
    push(1);
  }
  
  public void multiplyDouble() {
    
    writeOpCode(0x6B); // dmul
    pop(4);
    push(2);
  }
  
  public void divideInt() {
    
    writeOpCode(0x6C); // idiv
    pop(2);
    push(1);
  }
  
  public void divideLong() {
    
    writeOpCode(0x6D); // ldiv
    pop(4);
    push(2);
  }
  
  public void divideFloat() {
    
    writeOpCode(0x6E); // fdiv
    pop(2);
    push(1);
  }
  
  public void divideDouble() {
    
    writeOpCode(0x6F); // ddiv
    pop(4);
    push(2);
  }
  
  public void remainderInt() {
    
    writeOpCode(0x70); // irem
    pop(2);
    push(1);
  }
  
  public void remainderLong() {
    
    writeOpCode(0x71); // lrem
    pop(4);
    push(2);
  }
  
  public void remainderFloat() {
    
    writeOpCode(0x72); // frem
    pop(2);
    push(1);
  }
  
  public void remainderDouble() {
    
    writeOpCode(0x73); // drem
    pop(4);
    push(2);
  }
  
  public void negateInt() {
    
    writeOpCode(0x74); // ineg
    pop(1);
    push(1);
  }
  
  public void negateLong() {
    
    writeOpCode(0x75); // lneg
    pop(2);
    push(2);
  }
  
  public void negateFloat() {
    
    writeOpCode(0x76); // fneg
    pop(1);
    push(1);
  }
  
  public void negateDouble() {
    
    writeOpCode(0x77); // dneg
    pop(2);
    push(2);
  }
  
  public void shiftLeftInt() {
    
    writeOpCode(0x78); // ishl
    pop(2);
    push(1);
  }
  
  public void shiftLeftLong() {
    
    writeOpCode(0x79); // lshl
    pop(3);
    push(2);
  }
  
  public void arithmeticShiftRightInt() {
    
    writeOpCode(0x7A); // ishr
    pop(2);
    push(1);
  }
  
  public void arithmeticShiftRightLong() {
    
    writeOpCode(0x7B); // lshr
    pop(3);
    push(2);
  }
  
  public void logicalShiftRightInt() {
    
    writeOpCode(0x7C); // iushr
    pop(2);
    push(1);
  }
  
  public void logicalShiftRightLong() {
    
    writeOpCode(0x7D); // lushr
    pop(3);
    push(2);
  }
  
  public void booleanAndInt() {
    
    writeOpCode(0x7E); // iand
    pop(2);
    push(1);
  }
  
  public void booleanAndLong() {
    
    writeOpCode(0x7F); // land
    pop(4);
    push(2);
  }
  
  public void booleanOrInt() {
    
    writeOpCode(0x80); // ior
    pop(2);
    push(1);
  }
  
  public void booleanOrLong() {
    
    writeOpCode(0x81); // lor
    pop(4);
    push(2);
  }
  
  public void booleanXorInt() {
    
    writeOpCode(0x82); // ixor
    pop(2);
    push(1);
  }
  
  public void booleanXorLong() {
    
    writeOpCode(0x83); // lxor
    pop(4);
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
    
    writeOpCode(0x85); // i2l
    pop(1);
    push(2);
  }
  
  public void convertIntToFloat() {
    
    writeOpCode(0x86); // i2f
    pop(1);
    push(1);
  }
  
  public void convertIntToDouble() {
    
    writeOpCode(0x87); // i2d
    pop(1);
    push(2);
  }
  
  public void convertLongToInt() {
    
    writeOpCode(0x88); // l2i
    pop(2);
    push(1);
  }
  
  public void convertLongToFloat() {
    
    writeOpCode(0x89); // l2f
    pop(2);
    push(1);
  }
  
  public void convertLongToDouble() {
    
    writeOpCode(0x8A); // l2d
    pop(2);
    push(2);
  }
  
  public void convertFloatToInt() {
    
    writeOpCode(0x8B); // f2i
    pop(1);
    push(1);
  }
  
  public void convertFloatToLong() {
    
    writeOpCode(0x8C); // f2l
    pop(1);
    push(2);
  }
  
  public void convertFloatToDouble() {
    
    writeOpCode(0x8D); // f2d
    pop(1);
    push(2);
  }
  
  public void convertDoubleToInt() {
    
    writeOpCode(0x8E); // d2i
    pop(2);
    push(1);
  }
  
  public void convertDoubleToLong() {
    
    writeOpCode(0x8F); // d2l
    pop(2);
    push(2);
  }
  
  public void convertDoubleToFloat() {
    
    writeOpCode(0x90); // d2f
    pop(2);
    push(1);
  }
  
  public void convertIntToByte() {
    
    writeOpCode(0x91); // i2b
    pop(1);
    push(1);
  }
  
  public void convertIntToChar() {
    
    writeOpCode(0x92); // i2c
    pop(1);
    push(1);
  }
  
  public void convertIntToShort() {
    
    writeOpCode(0x93); // i2s
    pop(1);
    push(1);
  }
  
  public void compareLong() {
    
    writeOpCode(0x94); // lcmp
    pop(4);
    push(1);
  }
  
  public void compareFloat(boolean nanIsMinus) {
    
    writeOpCode(nanIsMinus ? 0x95 : 0x96); // fcmpg : fcmpl
    pop(2);
    push(1);
  }
  
  public void compareDouble(boolean nanIsMinus) {
    
    writeOpCode(nanIsMinus ? 0x97 : 0x98); // dcmpg : dcmpl
    pop(4);
    push(1);
  }
  
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
