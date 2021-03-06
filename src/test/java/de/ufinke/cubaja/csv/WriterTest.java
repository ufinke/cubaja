package de.ufinke.cubaja.csv;

import org.junit.*;
import static org.junit.Assert.*;
import de.ufinke.cubaja.config.*;
import java.io.*;
import java.math.*;

public class WriterTest {

  @Test
  public void basicTest() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setResourceLoader(new FileResourceLoader("src/test/resources/de/ufinke/cubaja/csv"));
    configurator.setName("data_config");
    CsvConfig config = configurator.configure(new CsvConfig());
    
    Data data = new Data();
    data.setBigDecimalField(new BigDecimal("987.654"));
    data.setBigIntegerField(new BigInteger("123"));
    
    StringWriter sw = new StringWriter();
    
    CsvWriter writer = new CsvWriter(sw, config);
    writer.writeRow(data);
    writer.close();
    
    assertEquals("123;987,65", sw.toString().trim());
  }
}
