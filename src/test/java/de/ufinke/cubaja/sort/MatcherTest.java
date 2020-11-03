package de.ufinke.cubaja.sort;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class MatcherTest {

  @Test
  public void match() {
    
    List<Integer> listA = new ArrayList<Integer>();
    listA.add(1);
    listA.add(2);
    listA.add(4);
    listA.add(5);
    listA.add(7);
    
    List<Integer> listB = new ArrayList<Integer>();
    listB.add(1);
    listB.add(3);
    listB.add(4);
    listB.add(4);
    listB.add(6);
    listB.add(6);
    listB.add(7);
    
    Comparator<Integer> comparator = new NaturalComparator<Integer>();
    Matcher<Integer> matcher = new Matcher<Integer>(comparator);
    MatchSource<Integer> sourceA = matcher.addSource(listA);
    MatchSource<Integer> sourceB = matcher.addSource(listB);
    
    for (Integer key : matcher) {
      switch (key) {
        case 1:
          assertEquals(true, sourceA.matches());
          assertEquals(1, sourceB.getList().size());
          break;
        case 2:
          assertEquals(true, sourceA.matches());
          assertEquals(0, sourceB.getList().size());
          break;
        case 3:
          assertEquals(false, sourceA.matches());
          assertEquals(1, sourceB.getList().size());
          break;
        case 4:
          assertEquals(true, sourceA.matches());
          assertEquals(2, sourceB.getList().size());
          break;
        case 5:
          assertEquals(true, sourceA.matches());
          assertEquals(0, sourceB.getList().size());
          break;
        case 6:
          assertEquals(false, sourceA.matches());
          // skip listB
          break;
        case 7:
          assertEquals(true, sourceA.matches());
          assertEquals(1, sourceB.getList().size());
          break;
        default:
          fail("unexpected key: " + key);
      }
    }
  }
}
