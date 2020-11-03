package de.ufinke.cubaja.io;

import java.nio.charset.Charset;
import org.junit.*;
import static org.junit.Assert.*;
import java.math.*;

public class MainframeOutputTest {

  @Test
  public void zoned() throws Exception {

    RandomAccessBuffer buffer = new RandomAccessBuffer();
    
    MainframeOutput mo = new MainframeOutput(buffer.getOutputStream(), Charset.forName("IBM273"));
    mo.writeZoned(123, 3);
    mo.writeZoned(-9812.34, 4, 2);
    mo.drainBuffer();
    
    buffer.setPosition(0);
    
    assertEquals(0xF1, buffer.read());
    assertEquals(0xF2, buffer.read());
    assertEquals(0xF3, buffer.read());
    
    assertEquals(0xF9, buffer.read());
    assertEquals(0xF8, buffer.read());
    assertEquals(0xF1, buffer.read());
    assertEquals(0xF2, buffer.read());
    assertEquals(0xF3, buffer.read());
    assertEquals(0xD4, buffer.read());
  }
  
  @Test
  public void packed() throws Exception {

    RandomAccessBuffer buffer = new RandomAccessBuffer();
    
    MainframeOutput mo = new MainframeOutput(buffer.getOutputStream(), Charset.forName("IBM273"));
    mo.writePacked(123, 3);
    mo.writePacked(new BigDecimal("-9812.34"), 4, 2);
    mo.drainBuffer();
    
    buffer.setPosition(0);
    
    assertEquals(0x12, buffer.read());
    assertEquals(0x3C, buffer.read());
    
    assertEquals(0x09, buffer.read());
    assertEquals(0x81, buffer.read());
    assertEquals(0x23, buffer.read());
    assertEquals(0x4D, buffer.read());
  }
  
  @Test
  public void unsignedPacked() throws Exception {

    RandomAccessBuffer buffer = new RandomAccessBuffer();
    
    MainframeOutput mo = new MainframeOutput(buffer.getOutputStream(), Charset.forName("IBM273"));
    mo.writeUnsignedPacked(123, 3);
    mo.writeUnsignedPacked(new BigDecimal("-9812.34"), 4, 2);
    mo.drainBuffer();
    
    buffer.setPosition(0);
    
    assertEquals(0x01, buffer.read());
    assertEquals(0x23, buffer.read());
    
    assertEquals(0x98, buffer.read());
    assertEquals(0x12, buffer.read());
    assertEquals(0x34, buffer.read());
  }
  
  @Test
  public void string() throws Exception {

    RandomAccessBuffer buffer = new RandomAccessBuffer();
    
    MainframeOutput mo = new MainframeOutput(buffer.getOutputStream(), Charset.forName("IBM273"));
    mo.writeString("AB", 3);
    mo.drainBuffer();
    
    buffer.setPosition(0);
    
    assertEquals(0xC1, buffer.read());
    assertEquals(0xC2, buffer.read());
    assertEquals(0x40, buffer.read());
  }
  
}
