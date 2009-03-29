package de.ufinke.cubaja.config.test.example1;

import de.ufinke.cubaja.config.*;

public class Main {

  static public void main(String[] args) {
    
    try {
      Configurator configurator = new Configurator();
      configurator.setBaseName(Main.class.getPackage().getName().replace('.', '/') + "/example_1_config");
      Config config = configurator.configure(new Config());
      System.out.println("Quality=" + config.getSomeValues().getQuality());
      System.out.println("Content=" + config.getMail().getContent());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
