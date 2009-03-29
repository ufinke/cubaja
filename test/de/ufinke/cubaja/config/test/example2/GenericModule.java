package de.ufinke.cubaja.config.test.example2;

public interface GenericModule {

  public void setParm(ParmConfig parm);
  
  public Class<? extends ParmConfig> getParmClass();
  
  public void testConfig() throws Exception;
}
