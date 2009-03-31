package de.ufinke.cubaja.config.test.example2;

import java.util.ArrayList;
import java.util.List;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.config.DynamicElement;
import de.ufinke.cubaja.config.ParameterFactory;
import de.ufinke.cubaja.config.ParameterFactoryFinder;

public class Config implements DynamicElement {

  private List<GenericModule> moduleList;
  
  public Config() {
    
    moduleList = new ArrayList<GenericModule>();
  }
  
  public void addModule(ModuleConfig module) {
    
    moduleList.add(module.getModule());
  }
  
  public List<GenericModule> getModuleList() {
    
    return moduleList;
  }
  
  public String alternateName(String originalName) {
    
    if (originalName.equals("dynamic")) {
      return "module";
    }
    
    if (originalName.equals("generic")) {
      return "something";
    }
    
    return null;
  }
  
  public void addSomething(HelloConfig hello) {
    
    System.out.println(hello.getHello());
  }
  
  public ParameterFactoryFinder parameterFactoryFinder() {
    
    return new ParameterFactoryFinder() {

      public ParameterFactory findFactory(Class<?> type) throws ConfigException {
        
        return null;
      }
    };
  }
}
