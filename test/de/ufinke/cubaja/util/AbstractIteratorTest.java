package de.ufinke.cubaja.util;

import org.junit.*;
import static org.junit.Assert.*;

public class AbstractIteratorTest {
  
  static int MAX_VALUE = 23456;
  
  static private class IterableImpl extends AbstractIterable<Integer> {
    
    public IterableImpl() {
      
    }
    
    public void execute() throws Exception {
    
      for (int i = 1; i <= MAX_VALUE; i++) {
        add(Integer.valueOf(i));
      }
    }
  }
  
  @Test
  public void abstractIterator() throws Exception {
    
    Stopwatch stopwatch = new Stopwatch("abstractIterator");
    
    int i = 0;
    for (Integer value : new IterableImpl()) {
      i++;
      assertEquals(i, value.intValue());
    }
    assertEquals(MAX_VALUE, i);
    
    stopwatch.elapsedMillis();
  }
  
}
