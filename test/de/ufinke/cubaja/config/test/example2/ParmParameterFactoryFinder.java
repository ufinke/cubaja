package de.ufinke.cubaja.config.test.example2;

import java.util.Map;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.config.ParameterFactory;
import de.ufinke.cubaja.config.ParameterFactoryFinder;

public class ParmParameterFactoryFinder implements ParameterFactoryFinder {

  private ParameterFactory parmFactory;
  
  public ParmParameterFactoryFinder(Map<Object, Object> infoMap) {
  
    parmFactory = new ParmParameterFactory(infoMap);
  }

  public ParameterFactory findFactory(Class<?> type) throws ConfigException {

    ParameterFactory factory = null;
    
    if (ParmConfig.class.isAssignableFrom(type)) {
      factory = parmFactory;
    }
    
    return factory;
  }
}
