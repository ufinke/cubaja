package de.ufinke.cubaja.io;

import org.junit.*;
import static org.junit.Assert.*;

public class RandomAccesBufferTest {

  @Test
  public void dataInputOutput() throws Exception {

    byte[] byteArray = new byte[] {1, 2};
    byte bytePos = 1;
    byte byteNeg = -1;
    byte byteMax = Byte.MAX_VALUE;
    byte byteMin = Byte.MIN_VALUE;
    short shortPos = 1;
    short shortNeg = -1;
    short shortMax = Short.MAX_VALUE;
    short shortMin = Short.MIN_VALUE;
    int intPos = 1;
    int intNeg = -1;
    int intMax = Integer.MAX_VALUE;
    int intMin = Integer.MIN_VALUE;
    long longPos = 1;
    long longNeg = -1;
    long longMax = Long.MAX_VALUE;
    long longMin = Long.MIN_VALUE;
    float floatPos = (float) 1.23;
    float floatNeg = (float) -1.23;
    double doublePos = 9.87;
    double doubleNeg = -9.87;
    boolean trueValue = true;
    boolean falseValue = false;
    char charPos = 1;
    char charMax = Character.MAX_VALUE;
    char charMin = Character.MIN_VALUE;
    String string = "abcaöü";
    int byteFF = 0xFF;
    int byte77 = 0x77;
    
    RandomAccessBuffer buf = new RandomAccessBuffer();
    
    buf.write(byteArray);
    buf.writeByte(bytePos);
    buf.writeByte(byteNeg);
    buf.writeByte(byteMax);
    buf.writeByte(byteMin);
    buf.writeShort(shortPos);
    buf.writeShort(shortNeg);
    buf.writeShort(shortMax);
    buf.writeShort(shortMin);
    buf.writeInt(intPos);
    buf.writeInt(intNeg);
    buf.writeInt(intMax);
    buf.writeInt(intMin);
    buf.writeLong(longPos);
    buf.writeLong(longNeg);
    buf.writeLong(longMax);
    buf.writeLong(longMin);
    buf.writeFloat(floatPos);
    buf.writeFloat(floatNeg);
    buf.writeDouble(doublePos);
    buf.writeDouble(doubleNeg);
    buf.writeBoolean(trueValue);
    buf.writeBoolean(falseValue);
    buf.writeChar(charPos);
    buf.writeChar(charMax);
    buf.writeChar(charMin);
    buf.writeUTF(string);
    buf.write(byteFF);
    buf.write(byte77);
    
    buf.setPosition(0);
    
    byte[] array = new byte[byteArray.length];
    buf.readFully(array);
    for (int i = 0; i < array.length; i++) {
      assertEquals(byteArray[i], array[i]);
    }
    assertEquals(bytePos, buf.readByte());
    assertEquals(byteNeg, buf.readByte());
    assertEquals(byteMax, buf.readByte());
    assertEquals(byteMin, buf.readByte());
    assertEquals(shortPos, buf.readShort());
    assertEquals(shortNeg, buf.readShort());
    assertEquals(shortMax, buf.readShort());
    assertEquals(shortMin, buf.readShort());
    assertEquals(intPos, buf.readInt());
    assertEquals(intNeg, buf.readInt());
    assertEquals(intMax, buf.readInt());
    assertEquals(intMin, buf.readInt());
    assertEquals(longPos, buf.readLong());
    assertEquals(longNeg, buf.readLong());
    assertEquals(longMax, buf.readLong());
    assertEquals(longMin, buf.readLong());
    assertEquals(floatPos, buf.readFloat(), 0.0001);
    assertEquals(floatNeg, buf.readFloat(), 0.0001);
    assertEquals(doublePos, buf.readDouble(), 0.0001);
    assertEquals(doubleNeg, buf.readDouble(), 0.0001);
    assertTrue(buf.readBoolean());
    assertFalse(buf.readBoolean());
    assertEquals(charPos, buf.readChar());
    assertEquals(charMax, buf.readChar());
    assertEquals(charMin, buf.readChar());
    assertEquals(string, buf.readUTF());
    assertEquals(byteFF, buf.readUnsignedByte());
    assertEquals(byte77, buf.readUnsignedByte());
  }
}
