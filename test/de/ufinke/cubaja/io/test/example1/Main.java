package de.ufinke.cubaja.io.test.example1;

import de.ufinke.cubaja.io.*;
import java.io.*;
import java.util.*;

public class Main {

  static public void main(String[] args) {
    
    try {
      BinaryOutputStream output = new BinaryOutputStream(new FileOutputStream("/home/uwe/temp/test.txt"));
      output.writeObject(createData());
      output.close();
      BinaryInputStream input = new BinaryInputStream(new FileInputStream("/home/uwe/temp/test.txt"));
      Data data = input.readObject(Data.class);
      System.out.println(data.getInteger());
      System.out.println(data.getSubData().getName());
      System.out.println(data.getTestEnum());
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
    data.setTestEnum(TestEnum.TWO);
    SubData subData = new SubData();
    subData.setName("Uwe");
    subData.setNumber(1956);
    data.setSubData(subData);
    return data;
  }
}
