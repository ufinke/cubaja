package de.ufinke.cubaja.util;

import org.junit.*;
import static org.junit.Assert.*;

public class AssignerTest {
  
  @Test
  public void assign() throws Exception {
    
    AData a = new AData();
    a.setString("hello");
    a.setNum(42);
    a.setSomething(1.23);
    
    BData b = new BData();
    
    Assigner assigner = Assigner.create(AData.class, BData.class);
    assigner.assign(a, b);
    
    assertEquals("hello", b.getString());
    assertEquals(42, b.getNum());
  }
  
}
