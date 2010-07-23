package de.ufinke.cubaja.csv;

import org.junit.*;
import static org.junit.Assert.*;
import de.ufinke.cubaja.config.*;
import java.io.*;
import java.math.*;

public class ReaderTest {

  @Test
  public void basicTest() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setResourceLoader(new FileResourceLoader("test/de/ufinke/cubaja/csv"));
    configurator.setName("data_config");
    CsvConfig config = configurator.configure(new CsvConfig());
    
    String lines = "123;987,65\n456;-1,2";
    
    StringReader sr = new StringReader(lines);
    CsvReader reader = new CsvReader(sr, config);
    
    int count = 0;
    
    for (Data data : reader.cursor(Data.class)) {
      count++;
      switch (count) {
        case 1:
          assertEquals(new BigInteger("123"), data.getBigIntegerField());
          assertEquals(new BigDecimal("987.65"), data.getBigDecimalField());
          break;
        case 2:
          assertEquals(new BigInteger("456"), data.getBigIntegerField());
          assertEquals(new BigDecimal("-1.2"), data.getBigDecimalField());
          break;
      }
    }

    assertEquals(count, reader.getRowCount());
    
    reader.close();
  }
  
  @Test
  public void builtinTest() throws Exception {
    
    String line = "hello World";
    
    StringReader sr = new StringReader(line);
    CsvReader reader = new CsvReader(sr);
    
    for (String string : reader.cursor(String.class)) {
      assertEquals(line, string);
    }
    
    reader.close();
  }
}
