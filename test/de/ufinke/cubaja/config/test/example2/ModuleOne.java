package de.ufinke.cubaja.config.test.example2;

public class ModuleOne implements GenericModule {

  private ParmOneConfig config;
  
  public ModuleOne() {
    
  }

  public void setParm(ParmConfig parm) {

    config = (ParmOneConfig) parm;
  }
  
  public Class<? extends ParmConfig> getParmClass() {
    
    return ParmOneConfig.class;
  }
  
  public void testConfig() {
    
    System.out.println(config.getValue());
    System.out.println(config.getSubElement().getNumber());
  }
}
