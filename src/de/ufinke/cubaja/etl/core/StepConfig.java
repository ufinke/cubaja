// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.etl.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import de.ufinke.cubaja.config.ConfigException;
import de.ufinke.cubaja.config.ElementFactory;
import de.ufinke.cubaja.config.ElementFactoryProvider;
import de.ufinke.cubaja.config.Mandatory;
import de.ufinke.cubaja.config.StartElementHandler;
import de.ufinke.cubaja.etl.api.Task;
import de.ufinke.cubaja.etl.api.TaskConfig;
import de.ufinke.cubaja.util.Text;

public class StepConfig implements StartElementHandler, ElementFactoryProvider {

  static private Text text = new Text(StepConfig.class);
  
  private Main main;
  private String name;
  private Map<String, LinkConfig> linkMap;
  private Map<String, Task> taskMap;
  private Task pendingTask;
  
  public StepConfig() {
    
    linkMap = new HashMap<String, LinkConfig>();
    taskMap = new HashMap<String, Task>();
  }

  public void startElement(Map<Object, Object> sharedMap) throws ConfigException {

    main = (Main) sharedMap.get("main");
  }

  public ElementFactory getFactory(String tagName, Map<String, String> attributes) {

    if (tagName.equals("link")) {
      return null;
    }
    
    pendingTask = main.getTask(tagName);
    if (pendingTask == null) {
      return null;
    }    
    final TaskConfig taskConfig = pendingTask.getConfig();
    
    return new ElementFactory() {

      public Object getElement(Annotation[] annotations) {

        return taskConfig;
      }

      public Method getMethod() throws ConfigException {

        try {
          return StepConfig.class.getMethod("addTask", TaskConfig.class);
        } catch (Exception e) {
          throw new ConfigException(e);
        }
      }
    };
  }
  
  public String getName() {
  
    return name;
  }

  @Mandatory
  public void setName(String name) {
  
    this.name = name;
  }
  
  @Mandatory
  public void addTask(TaskConfig taskConfig) throws ConfigException {
    
    String name = taskConfig.getName();
    if (taskMap.containsKey(name)) {
      throw new ConfigException(text.get("taskDuplicate", name));
    }
    taskMap.put(name, pendingTask);
  }
  
  public void addLink(LinkConfig link) throws ConfigException {
    
    String name = link.getName();
    if (linkMap.containsKey(name)) {
      throw new ConfigException(text.get("linkDuplicate", name));
    }
    linkMap.put(name, link);
  }

}
