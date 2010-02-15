// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.etl.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.config.StartElementHandler;

public class Config implements StartElementHandler {

  private Main main;
  private List<StepConfig> stepList;
  
  public Config() {
  
    stepList = new ArrayList<StepConfig>();
  }

  public void startElement(Map<Object, Object> sharedMap) throws ConfigException {

    main = (Main) sharedMap.get("main");
  }
  
  public void addStep(StepConfig step) {
    
    stepList.add(step);
  }
  
  public List<StepConfig> getSteps() {
    
    return stepList;
  }
  
  public void addTaskdef(TaskdefConfig taskdef) throws ConfigException {
    
    try {
      main.defineTask(taskdef);
    } catch (Exception e) {
      throw new ConfigException(e);
    }
  }
  
  public void addExtend(ExtendConfig extend) throws ConfigException {
    
    try {
      main.extend(extend);
    } catch (Exception e) {
      throw new ConfigException(e);
    }
  }
}
