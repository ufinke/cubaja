package de.ufinke.cubaja.csv.test.example2;

import java.util.Date;
import de.ufinke.cubaja.config.Configurator;
import de.ufinke.cubaja.csv.CsvWriter;
import de.ufinke.cubaja.util.Executor;

public class Main extends Executor {

  static public void main(String[] args) {
    
    new Main().start();
  }
  
  protected void execute() throws Exception {
    
    Configurator configurator = new Configurator();
    configurator.setBaseName("de/ufinke/cubaja/csv/test/example2/config");
    Config config = configurator.configure(new Config());
    
    CsvWriter writer = new CsvWriter(config.getCsv());
    
    Data data = new Data();
    data.setString("hello world");
    data.setDate(new Date());
    data.setNumber(123.456);
    
    writer.writeObject(data);
    writer.nextRow();
    
    writer.close();
  }
}
