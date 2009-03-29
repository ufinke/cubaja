package de.ufinke.cubaja.config.test.example2;

import de.ufinke.cubaja.config.*;

public class ModuleConfig extends ConfigNode {

  private GenericModule module;
  
  public ModuleConfig() {
    
  }
  
  public void setClass(GenericModule module) {
    
    this.module = module;
    infoMap().put("parmClass", module.getParmClass());
  }
  
  public GenericModule getModule() {
    
    return module;
  }
  
  public void setParm(ParmConfig parm) {
  
    module.setParm(parm);
  }
  
}
