package de.ufinke.cubaja.sort.test.example1;

import de.ufinke.cubaja.sort.*;

public class Main {

  static public void main(String[] args) {
    
    try {
      Sorter<String> sorter = new Sorter<String>(new NaturalComparator<String>());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
