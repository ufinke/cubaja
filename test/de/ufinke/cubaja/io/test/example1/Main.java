package de.ufinke.cubaja.io.test.example1;

import de.ufinke.cubaja.io.*;
import java.io.*;
import java.util.*;

public class Main {

  static public void main(String[] args) {
    
    try {
      BinaryOutputStream output = new BinaryOutputStream(new FileOutputStream("~/temp/test.txt"));
      output.writeObject(createData());
      output.close();
      BinaryInputStream input = new BinaryInputStream(new FileInputStream("~/temp/test.txt"));
      Data data = input.readObject(Data.class);
      System.out.println(data.getSubData().getName());
      input.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  static private Data createData() {
    
    Data data = new Data();
    data.setDate(new Date());
    data.setInteger(42);
    data.setString("hello");
    SubData subData = new SubData();
    subData.setName("Uwe");
    subData.setNumber(1956);
    data.setSubData(subData);
    return data;
  }
}
