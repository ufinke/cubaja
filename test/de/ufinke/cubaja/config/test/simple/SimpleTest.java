package de.ufinke.cubaja.config.test.simple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import de.ufinke.cubaja.config.Configurator;

public class SimpleTest {

  @Test
  public void test() {
    
    try {
      Configurator configurator = new Configurator();
      configurator.setBaseName(SimpleTest.class.getPackage().getName().replace('.', '/') + "/simpletestConfig");
      Config config = configurator.configure(new Config());
      assertEquals("hello world", config.getString());
      SubElement subElement = config.getSubElement();
      assertEquals(42, subElement.getNumber());
      assertEquals(1.23, subElement.getDecimal());
      assertEquals("this is the value\nsplitted into two lines", subElement.getValue());
      SomeEnum[] enums = subElement.getEnums();
      assertEquals(2, enums.length);
      assertEquals(SomeEnum.CONSTANT_A, enums[0]);
      assertEquals(SomeEnum.CONSTANT_C, enums[1]);
      int[] intArray = subElement.getIntArray();
      assertEquals(2, intArray.length);
      assertEquals(1, intArray[0]);
      assertEquals(2, intArray[1]);
    } catch (Exception e) {
      e.printStackTrace();
      fail(e.toString());
    }
  }
  
}
