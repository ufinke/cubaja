package de.ufinke.cubaja.config.test.example2;

import java.lang.annotation.Annotation;
import java.util.Map;
import de.ufinke.cubaja.config.*;

public class ParmParameterFactory implements ParameterFactory {

  private Map<Object, Object> infoMap;
  
  public ParmParameterFactory(Map<Object, Object> infoMap) {
    
    this.infoMap = infoMap;
  }

  public Object createParameter(String value, Class<?> type, Annotation[] annotations) throws Exception {

    Class<?> clazz = (Class<?>) infoMap.get("parmClass");
    return clazz.newInstance();
  }
}
