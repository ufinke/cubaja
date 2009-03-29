package de.ufinke.cubaja.config.test.example2;

import de.ufinke.cubaja.config.*;
import java.util.*;

public class ModuleConfig implements ManagedElement {

  private GenericModule module;
  private Map<Object, Object> infoMap;
  
  public ModuleConfig() {
    
  }
  
  public void init(Map<Object, Object> infoMap) {
    
    this.infoMap = infoMap;
  }
  
  public void finish() {
    
  }
  
  public void setClass(GenericModule module) {
    
    this.module = module;
    infoMap.put("parmClass", module.getParmClass());
  }
  
  public GenericModule getModule() {
    
    return module;
  }
  
  public void setParm(ParmConfig parm) {
  
    module.setParm(parm);
  }
  
}
