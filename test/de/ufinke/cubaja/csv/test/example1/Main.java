package de.ufinke.cubaja.csv.test.example1;

import de.ufinke.cubaja.config.*;
import de.ufinke.cubaja.csv.*;

public class Main {

  static public void main(String[] args) {
    
    try {
      new Main();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private Main() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setBaseName("de/ufinke/cubaja/csv/test/example1/config");
    Config config = configurator.configure(new Config());
    
    CsvReader reader = new CsvReader(config.getCsv());
    
    for (Data data : reader.readAllRows(Data.class)) {
      System.out.println(data.getText() + ", " + data.getNumber() + ", " + data.getChoice());
    }
    
    reader.close();
  }
}
