package de.ufinke.cubaja.config.test.example1;

import de.ufinke.cubaja.config.*;

public class Main {

  static public void main(String[] args) {
    
    try {
      Configurator configurator = new Configurator();
      configurator.setBaseName("example_1_config");
      Config config = configurator.configure(new Config());
      System.out.println("Quality=" + config.getSomeValues().getQuality());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
