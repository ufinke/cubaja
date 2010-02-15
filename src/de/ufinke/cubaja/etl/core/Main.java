// Copyright (c) 2010, Uwe Finke. All rights reserved.
// Subject to BSD License. See "license.txt" distributed with this package.

package de.ufinke.cubaja.etl.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.ufinke.cubaja.cafebabe.Loader;
import de.ufinke.cubaja.config.Configurator;
import de.ufinke.cubaja.config.StringResourceLoader;
import de.ufinke.cubaja.etl.api.Task;
import de.ufinke.cubaja.util.Executor;
import de.ufinke.cubaja.util.Text;

public class Main extends Executor {

  static private Text text = new Text(Main.class);
  static private Log logger = LogFactory.getLog(Main.class);
  
  static public void main(String[] args) {

    Main main = new Main();
    main.setArgs(args);
    main.start();
  }

  private Config config;
  
  private Loader cafebabeLoader;
  private ExtensionLoader extensionLoader;
  
  private Map<String, Task> taskMap;
  
  private Main() {
    
  }
  
  protected void execute() throws Exception {
    
    extensionLoader = new ExtensionLoader();
    cafebabeLoader = new Loader(extensionLoader);
    
    taskMap = new HashMap<String, Task>();

    Configurator configurator = new Configurator();
    if (getArgs().length == 0) {
      logger.info(text.get("configDefault"));
    } else {
      String baseName = getArgs()[0];
      logger.info(text.get("config", baseName));
      configurator.setBaseName(baseName);
    }
    configurator.infoMap().put("main", this);
    config = configurator.configure(new Config());
    
    for (StepConfig step : config.getSteps()) {
      processStep(step);
    }
  }
  
  void defineTask(TaskdefConfig taskdef) throws Exception {
    
    String name = taskdef.getName();
    
    if (taskMap.containsKey(name)) {
      logger.warn(text.get("taskdefOverride", name));
    }
    
    Class<?> clazz = extensionLoader.loadClass(name);
    Task task = (Task) clazz.newInstance();
    
    taskMap.put(name, task);
    logger.info(text.get("taskdef", name, task.getClass().getName()));
  }
  
  Task getTask(String taskName) {
    
    return taskMap.get(taskName);
  }
  
  void extend(ExtendConfig extend) throws Exception {

    String lib = extend.getLib();
    extensionLoader.addLib(lib);
    
    if (! lib.endsWith(".jar")) {
      return;
    }
    
    ZipFile file = new ZipFile(lib);
    ZipEntry entry = file.getEntry("taskdef.xml");
    if (entry == null) {
      return;
    }
    
    StringResourceLoader srl = new StringResourceLoader();
    
    BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(file.getInputStream(entry))));
    String line = reader.readLine();
    while (line != null) {
      srl.append(line);
    }
    reader.close();
    file.close();
    
    Configurator configurator = new Configurator();
    configurator.setResourceLoader(srl);
    configurator.infoMap().put("main", this);
    configurator.configure(new Config());
  }
  
  private void processStep(StepConfig stepConfig) {
    
  }
}
