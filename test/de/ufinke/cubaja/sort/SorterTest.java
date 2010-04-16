package de.ufinke.cubaja.sort;

import org.junit.*;
import static org.junit.Assert.*;
import de.ufinke.cubaja.config.*;
import java.util.*;

public class SorterTest {

  @Test
  public void sort() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setResourceLoader(new FileResourceLoader("test/de/ufinke/cubaja/sort"));
    configurator.setName("sorter_config");
    SorterTestConfig config = configurator.configure(new SorterTestConfig());
    
    Comparator<Integer> comparator = new NaturalComparator<Integer>();
    
    Sorter<Integer> sorter = new Sorter<Integer>(comparator, config.getSort());
    
    long putSum = 0;
    Random random = new Random();
    for (long i = 0; i < config.getRecords(); i++) {
      int number = random.nextInt(1000);
      putSum += number;
      Integer record = Integer.valueOf(number);
      sorter.add(record);
    }
    
    long getSum = 0;
    long recNum = 0;
    SequenceChecker<Integer> checker = new SequenceChecker<Integer>(comparator, sorter);
    for (Integer number : checker) {
      getSum += number;
      recNum++;
    }
    
    assertEquals(putSum, getSum);
    assertEquals(config.getRecords(), recNum);
  }
}
