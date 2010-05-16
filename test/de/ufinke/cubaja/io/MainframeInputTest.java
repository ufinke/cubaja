package de.ufinke.cubaja.io;

import java.nio.charset.Charset;
import org.junit.*;
import static org.junit.Assert.*;
import java.math.*;

public class MainframeInputTest {

  @Test
  public void zoned() throws Exception {

    RandomAccessBuffer buffer = new RandomAccessBuffer();
    
    buffer.write(0xF0);
    buffer.write(0xF8);
    buffer.write(0xD5);
    
    buffer.setPosition(0);
    
    MainframeInput mi = new MainframeInput(buffer.getInputStream(), Charset.forName("IBM273"));
    mi.fillBuffer(3);
    assertEquals(new BigDecimal("-8.5"), mi.readZonedBigDecimal(2, 1));
  }
  
  @Test
  public void packed() throws Exception {

    RandomAccessBuffer buffer = new RandomAccessBuffer();
    
    buffer.write(0x12);
    buffer.write(0x34);
    buffer.write(0x5C);
    
    buffer.write(0x00);
    buffer.write(0x98);
    buffer.write(0x7D);
    
    buffer.setPosition(0);
    
    MainframeInput mi = new MainframeInput(buffer.getInputStream(), Charset.forName("IBM273"));
    mi.fillBuffer(6);
    assertEquals(new BigDecimal("123.45"), mi.readPackedBigDecimal(3, 2));
    assertEquals(-987, mi.readPackedInt(5));
  }
  
  @Test
  public void unsignedPacked() throws Exception {

    RandomAccessBuffer buffer = new RandomAccessBuffer();
    
    buffer.write(0x02);
    buffer.write(0x34);
    buffer.write(0x56);
    
    buffer.write(0x00);
    buffer.write(0x01);
    buffer.write(0x29);
    
    buffer.setPosition(0);
    
    MainframeInput mi = new MainframeInput(buffer.getInputStream(), Charset.forName("IBM273"));
    mi.fillBuffer(6);
    assertEquals(new BigDecimal("234.56"), mi.readUnsignedPackedBigDecimal(3, 2));
    assertEquals(129, mi.readUnsignedPackedInt(6));
  }
  
  @Test
  public void string() throws Exception {

    RandomAccessBuffer buffer = new RandomAccessBuffer();
    
    buffer.write(0xC1);
    buffer.write(0xC2);
    buffer.write(0x40);
    
    buffer.setPosition(0);
    
    MainframeInput mi = new MainframeInput(buffer.getInputStream(), Charset.forName("IBM273"));
    mi.fillBuffer(3);
    assertEquals("AB", mi.readString(3).trim());
  }
  
  @Test
  public void eof() throws Exception {

    RandomAccessBuffer buffer = new RandomAccessBuffer();
    
    buffer.writeInt(0);    
    buffer.setPosition(0);
    
    MainframeInput mi = new MainframeInput(buffer.getInputStream(), Charset.forName("IBM273"));
    assertTrue(mi.fillBuffer(4));
    assertFalse(mi.fillBuffer(1));
  }
  
}
