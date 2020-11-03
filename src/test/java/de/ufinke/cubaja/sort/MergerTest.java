package de.ufinke.cubaja.sort;

import org.junit.*;
import static org.junit.Assert.*;
import java.util.*;

public class MergerTest {

  @Test
  public void merge() {
    
    List<Integer> listA = new ArrayList<Integer>();
    listA.add(1);
    listA.add(2);
    listA.add(3);
    
    List<Integer> listB = new ArrayList<Integer>();
    listB.add(1);
    listB.add(4);
    
    List<Integer> listC = new ArrayList<Integer>();
    listC.add(1);
    listC.add(2);
    listC.add(2);
    listC.add(4);
    listC.add(8);
    
    List<Iterable<Integer>> sources = new ArrayList<Iterable<Integer>>();
    sources.add(listA);
    sources.add(listB);
    sources.add(listC);
    
    int putSize = listA.size() + listB.size() + listC.size();
    int putSum = 0;
    for (Iterable<Integer> iterable : sources) {
      for (Integer number : iterable) {
        putSum += number;
      }
    }
    
    int getSize = 0;
    int getSum = 0;
    List<Integer> resultList = new ArrayList<Integer>();
    
    Comparator<Integer> comparator = new NaturalComparator<Integer>();
    Merger<Integer> merger = new Merger<Integer>(comparator, sources);
    SequenceChecker<Integer> checker = new SequenceChecker<Integer>(comparator, merger);
    for (Integer number : checker) {
      getSize++;
      getSum += number;
      resultList.add(number);
    }
    
    assertEquals(putSize, getSize);
    assertEquals(putSum, getSum);
    
    int index = 0;
    assertEquals(1, resultList.get(index++).intValue());
    assertEquals(1, resultList.get(index++).intValue());
    assertEquals(1, resultList.get(index++).intValue());
    assertEquals(2, resultList.get(index++).intValue());
    assertEquals(2, resultList.get(index++).intValue());
    assertEquals(2, resultList.get(index++).intValue());
    assertEquals(3, resultList.get(index++).intValue());
    assertEquals(4, resultList.get(index++).intValue());
    assertEquals(4, resultList.get(index++).intValue());
    assertEquals(8, resultList.get(index++).intValue());
  }
}
