package de.ufinke.cubaja.config.ex.simple;

import de.ufinke.cubaja.config.Configurator;
import de.ufinke.cubaja.util.Executor;

public class Main extends Executor {

  static public void main(String[] args) {
    
    new Main().start();
  }
  
  public void execute() throws Exception {
    
    Configurator configurator = new Configurator();
    Config config = configurator.configure(new Config());
  }
}
