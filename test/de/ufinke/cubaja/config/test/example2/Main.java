package de.ufinke.cubaja.config.test.example2;

import de.ufinke.cubaja.config.*;

public class Main {

  static public void main(String[] args) {
    
    try {
      Configurator configurator = new Configurator();
      configurator.setBaseName(Main.class.getPackage().getName().replace('.', '/') + "/example_2_config");
      configurator.addParameterFactoryFinder(new ParmParameterFactoryFinder(configurator.infoMap()));
      Config config = configurator.configure(new Config());
      for (GenericModule module : config.getModuleList()) {
        module.testConfig();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
