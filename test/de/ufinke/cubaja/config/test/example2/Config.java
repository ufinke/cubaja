package de.ufinke.cubaja.config.test.example2;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import de.ufinke.cubaja.config.*;

public class Config extends ConfigNode {

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
  
  protected String assignAlternateName(String originalName) {
    
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
  
  protected ParameterFactoryFinder parameterFactorFinder() {
    
    return new ParameterFactoryFinder() {

      public ParameterFactory findFactory(Class<?> type) throws Exception {

        if (type == HelloConfig.class) {
          
          return new ParameterFactory() {

            public Object createParameter(String value, Class<?> parmType, Annotation[] annotations) throws Exception {

              return parmType.newInstance();
            }  
          };
        }
        
        return null;
      }
    };
  }
}
