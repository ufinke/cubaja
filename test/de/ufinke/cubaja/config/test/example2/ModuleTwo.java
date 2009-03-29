package de.ufinke.cubaja.config.test.example2;

public class ModuleTwo implements GenericModule {

  private ParmTwoConfig config;
  
  public ModuleTwo() {
    
  }

  public void setParm(ParmConfig parm) {

    config = (ParmTwoConfig) parm;
  }
  
  public Class<? extends ParmConfig> getParmClass() {
    
    return ParmTwoConfig.class;
  }
  
  public void testConfig() {
    
    System.out.println(config.getFileName());
  }
}
